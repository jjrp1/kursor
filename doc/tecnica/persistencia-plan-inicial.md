---
title: Plan Inicial de Persistencia
subtitle: Documentación técnica del sistema de persistencia
description: Análisis y propuestas para la persistencia de sesiones y estrategias en Kursor
keywords: persistencia, sesiones, estrategias, JPA, SQLite, JSON, YAML
status: desarrollo
created: 2025-01-27
modified: 2025-01-27
author: "Juanjo Ruiz"
---

# Plan Inicial de Persistencia

## 1. Introducción

### 1.1 Objetivo

Este documento analiza los requisitos y propuestas para la persistencia de datos en Kursor, específicamente:
- **Persistencia de Sesiones**: Estado del usuario durante el aprendizaje (JPA + SQLite)
- **Persistencia de Estrategias**: Estado interno de las estrategias de aprendizaje (JPA + SQLite)
- **Carga de Cursos**: Soporte para formatos JSON y YAML
- **Gestión de Estado**: Cómo mantener y restaurar el progreso del usuario

### 1.2 Contexto

El sistema de aprendizaje de Kursor requiere persistencia robusta para:
- Permitir pausar y continuar sesiones de aprendizaje
- Mantener estadísticas de progreso del usuario
- Restaurar el estado exacto de las estrategias de aprendizaje
- Soportar múltiples usuarios y cursos
- Cargar cursos desde formatos JSON y YAML

## 2. Arquitectura de Persistencia

### 2.1 Estrategia de Persistencia

#### **2.1.1 Persistencia de Estado (JPA + SQLite)**
- **Sesiones de aprendizaje**: Estado del usuario, progreso, estadísticas
- **Estado de estrategias**: Datos internos de cada estrategia
- **Estadísticas históricas**: Progreso a largo plazo
- **Configuraciones de usuario**: Preferencias y ajustes

#### **2.1.2 Carga de Cursos (JSON/YAML)**
- **Cursos existentes**: Mantener soporte actual para YAML
- **Nuevos cursos**: Agregar soporte para JSON
- **Compatibilidad**: Aplicación puede "alimentarse" con ambos formatos
- **Conversión**: Posibilidad de convertir entre formatos

### 2.2 Estructura de Base de Datos

#### **2.2.1 Esquema de Base de Datos**

```sql
-- Tabla de sesiones
CREATE TABLE sesiones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id VARCHAR(255) NOT NULL,
    curso_id VARCHAR(255) NOT NULL,
    bloque_id VARCHAR(255) NOT NULL,
    estrategia_tipo VARCHAR(100) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_ultima_actividad TIMESTAMP,
    tiempo_total INTEGER DEFAULT 0,
    preguntas_respondidas INTEGER DEFAULT 0,
    aciertos INTEGER DEFAULT 0,
    porcentaje_completitud REAL DEFAULT 0.0,
    estado VARCHAR(50) DEFAULT 'ACTIVA',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de estado de estrategias
CREATE TABLE estados_estrategias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sesion_id INTEGER NOT NULL,
    tipo_estrategia VARCHAR(100) NOT NULL,
    datos_estado TEXT NOT NULL, -- JSON serializado
    progreso REAL DEFAULT 0.0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sesion_id) REFERENCES sesiones(id) ON DELETE CASCADE
);

-- Tabla de estadísticas de usuario
CREATE TABLE estadisticas_usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id VARCHAR(255) NOT NULL,
    curso_id VARCHAR(255) NOT NULL,
    tiempo_total INTEGER DEFAULT 0,
    sesiones_completadas INTEGER DEFAULT 0,
    mejor_racha_dias INTEGER DEFAULT 0,
    fecha_ultima_sesion TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de respuestas de preguntas
CREATE TABLE respuestas_preguntas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sesion_id INTEGER NOT NULL,
    pregunta_id VARCHAR(255) NOT NULL,
    respuesta_correcta BOOLEAN NOT NULL,
    tiempo_respuesta INTEGER,
    intentos INTEGER DEFAULT 1,
    fecha_respuesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sesion_id) REFERENCES sesiones(id) ON DELETE CASCADE
);
```

#### **2.2.2 Entidades JPA**

```java
@Entity
@Table(name = "sesiones")
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;
    
    @Column(name = "curso_id", nullable = false)
    private String cursoId;
    
    @Column(name = "bloque_id", nullable = false)
    private String bloqueId;
    
    @Column(name = "estrategia_tipo", nullable = false)
    private String estrategiaTipo;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_ultima_actividad")
    private LocalDateTime fechaUltimaActividad;
    
    @Column(name = "tiempo_total")
    private Integer tiempoTotal = 0;
    
    @Column(name = "preguntas_respondidas")
    private Integer preguntasRespondidas = 0;
    
    @Column(name = "aciertos")
    private Integer aciertos = 0;
    
    @Column(name = "porcentaje_completitud")
    private Double porcentajeCompletitud = 0.0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSesion estado = EstadoSesion.ACTIVA;
    
    @OneToOne(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EstadoEstrategia estadoEstrategia;
    
    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RespuestaPregunta> respuestas = new ArrayList<>();
    
    // Constructores, getters, setters...
}

@Entity
@Table(name = "estados_estrategias")
public class EstadoEstrategia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;
    
    @Column(name = "tipo_estrategia", nullable = false)
    private String tipoEstrategia;
    
    @Column(name = "datos_estado", nullable = false, columnDefinition = "TEXT")
    private String datosEstado; // JSON serializado
    
    @Column(name = "progreso")
    private Double progreso = 0.0;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;
    
    // Constructores, getters, setters...
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaUltimaModificacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaUltimaModificacion = LocalDateTime.now();
    }
}
```

## 3. Gestión de Cursos (JSON/YAML)

### 3.1 Soporte Multi-Formato

#### **3.1.1 Cargador de Cursos**

```java
public interface CursoLoader {
    
    /**
     * Carga un curso desde un archivo.
     * Detecta automáticamente el formato (JSON o YAML).
     */
    Curso cargarCurso(Path archivoCurso);
    
    /**
     * Carga un curso desde un archivo JSON.
     */
    Curso cargarCursoJson(Path archivoJson);
    
    /**
     * Carga un curso desde un archivo YAML.
     */
    Curso cargarCursoYaml(Path archivoYaml);
    
    /**
     * Guarda un curso en formato JSON.
     */
    void guardarCursoJson(Curso curso, Path archivoDestino);
    
    /**
     * Guarda un curso en formato YAML.
     */
    void guardarCursoYaml(Curso curso, Path archivoDestino);
    
    /**
     * Convierte un curso de YAML a JSON.
     */
    void convertirYamlAJson(Path archivoYaml, Path archivoJson);
    
    /**
     * Convierte un curso de JSON a YAML.
     */
    void convertirJsonAYaml(Path archivoJson, Path archivoYaml);
}
```

#### **3.1.2 Implementación del Cargador**

```java
public class CursoLoaderImpl implements CursoLoader {
    
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;
    
    public CursoLoaderImpl() {
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    @Override
    public Curso cargarCurso(Path archivoCurso) {
        String extension = obtenerExtension(archivoCurso);
        
        switch (extension.toLowerCase()) {
            case "json":
                return cargarCursoJson(archivoCurso);
            case "yaml":
            case "yml":
                return cargarCursoYaml(archivoCurso);
            default:
                throw new IllegalArgumentException("Formato no soportado: " + extension);
        }
    }
    
    @Override
    public Curso cargarCursoJson(Path archivoJson) {
        try {
            return jsonMapper.readValue(archivoJson.toFile(), Curso.class);
        } catch (IOException e) {
            throw new CursoLoadException("Error al cargar curso JSON: " + archivoJson, e);
        }
    }
    
    @Override
    public Curso cargarCursoYaml(Path archivoYaml) {
        try {
            return yamlMapper.readValue(archivoYaml.toFile(), Curso.class);
        } catch (IOException e) {
            throw new CursoLoadException("Error al cargar curso YAML: " + archivoYaml, e);
        }
    }
    
    @Override
    public void convertirYamlAJson(Path archivoYaml, Path archivoJson) {
        try {
            Curso curso = cargarCursoYaml(archivoYaml);
            guardarCursoJson(curso, archivoJson);
        } catch (Exception e) {
            throw new CursoLoadException("Error al convertir YAML a JSON", e);
        }
    }
    
    @Override
    public void convertirJsonAYaml(Path archivoJson, Path archivoYaml) {
        try {
            Curso curso = cargarCursoJson(archivoJson);
            guardarCursoYaml(curso, archivoYaml);
        } catch (Exception e) {
            throw new CursoLoadException("Error al convertir JSON a YAML", e);
        }
    }
}
```

### 3.2 Estructura de Cursos JSON

#### **3.2.1 Ejemplo de Curso en JSON**

```json
{
  "id": "curso_ingles_basico",
  "nombre": "Inglés Básico",
  "descripcion": "Curso de inglés para principiantes",
  "idioma": "es",
  "nivel": "A1",
  "bloques": [
    {
      "id": "vocabulario_colores",
      "titulo": "Vocabulario: Colores",
      "tipo": "flashcard",
      "preguntas": [
        {
          "id": "color_rojo",
          "enunciado": "¿Cómo se dice 'rojo' en inglés?",
          "respuesta": "red",
          "pista": "Color de la sangre",
          "dificultad": 1
        },
        {
          "id": "color_azul",
          "enunciado": "¿Cómo se dice 'azul' en inglés?",
          "respuesta": "blue",
          "pista": "Color del cielo",
          "dificultad": 1
        }
      ]
    },
    {
      "id": "gramatica_verbos",
      "titulo": "Gramática: Verbos Básicos",
      "tipo": "multiple_choice",
      "preguntas": [
        {
          "id": "verbo_to_be",
          "enunciado": "¿Cuál es la forma correcta del verbo 'to be' en presente?",
          "opciones": [
            "I am",
            "I is",
            "I are",
            "I be"
          ],
          "respuestaCorrecta": 0,
          "explicacion": "La forma correcta es 'I am'",
          "dificultad": 2
        }
      ]
    }
  ]
}
```

## 4. Gestión de Persistencia JPA

### 4.1 Configuración de JPA

#### **4.1.1 persistence.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                                 http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
             version="2.2">
    
    <persistence-unit name="kursorPU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        
        <class>com.kursor.persistence.entity.Sesion</class>
        <class>com.kursor.persistence.entity.EstadoEstrategia</class>
        <class>com.kursor.persistence.entity.EstadisticasUsuario</class>
        <class>com.kursor.persistence.entity.RespuestaPregunta</class>
        
        <properties>
            <!-- Configuración de SQLite -->
            <property name="javax.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:sqlite:data/kursor.db"/>
            <property name="javax.persistence.jdbc.user" value=""/>
            <property name="javax.persistence.jdbc.password" value=""/>
            
            <!-- Configuración de Hibernate -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.SQLiteDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="false"/>
            <property name="hibernate.format_sql" value="true"/>
            
            <!-- Configuración de conexión -->
            <property name="hibernate.connection.pool_size" value="1"/>
            <property name="hibernate.connection.autocommit" value="false"/>
        </properties>
    </persistence-unit>
</persistence>
```

### 4.2 Repositorios JPA

#### **4.2.1 Repositorio de Sesiones**

```java
@Repository
public class SesionRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Sesion guardar(Sesion sesion) {
        if (sesion.getId() == null) {
            entityManager.persist(sesion);
            return sesion;
        } else {
            return entityManager.merge(sesion);
        }
    }
    
    public Sesion buscarPorId(Long id) {
        return entityManager.find(Sesion.class, id);
    }
    
    public Sesion buscarSesionActiva(String usuarioId) {
        TypedQuery<Sesion> query = entityManager.createQuery(
            "SELECT s FROM Sesion s WHERE s.usuarioId = :usuarioId AND s.estado = 'ACTIVA'",
            Sesion.class
        );
        query.setParameter("usuarioId", usuarioId);
        
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Sesion> buscarSesionesUsuario(String usuarioId) {
        TypedQuery<Sesion> query = entityManager.createQuery(
            "SELECT s FROM Sesion s WHERE s.usuarioId = :usuarioId ORDER BY s.fechaInicio DESC",
            Sesion.class
        );
        query.setParameter("usuarioId", usuarioId);
        return query.getResultList();
    }
    
    public void eliminar(Long id) {
        Sesion sesion = buscarPorId(id);
        if (sesion != null) {
            entityManager.remove(sesion);
        }
    }
}
```

#### **4.2.2 Repositorio de Estados de Estrategias**

```java
@Repository
public class EstadoEstrategiaRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public EstadoEstrategia guardar(EstadoEstrategia estado) {
        if (estado.getId() == null) {
            entityManager.persist(estado);
            return estado;
        } else {
            return entityManager.merge(estado);
        }
    }
    
    public EstadoEstrategia buscarPorSesion(Long sesionId) {
        TypedQuery<EstadoEstrategia> query = entityManager.createQuery(
            "SELECT e FROM EstadoEstrategia e WHERE e.sesion.id = :sesionId",
            EstadoEstrategia.class
        );
        query.setParameter("sesionId", sesionId);
        
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public void eliminarPorSesion(Long sesionId) {
        Query query = entityManager.createQuery(
            "DELETE FROM EstadoEstrategia e WHERE e.sesion.id = :sesionId"
        );
        query.setParameter("sesionId", sesionId);
        query.executeUpdate();
    }
}
```

## 5. Integración con Estrategias

### 5.1 Serialización de Estado de Estrategias

#### **5.1.1 Gestor de Estado**

```java
@Component
public class EstrategiaStateManager {
    
    private final ObjectMapper objectMapper;
    private final EstadoEstrategiaRepository estadoRepository;
    
    public EstrategiaStateManager(EstadoEstrategiaRepository estadoRepository) {
        this.objectMapper = new ObjectMapper();
        this.estadoRepository = estadoRepository;
    }
    
    public void guardarEstadoEstrategia(Sesion sesion, EstrategiaAprendizaje estrategia) {
        try {
            // Extraer datos específicos de la estrategia
            Map<String, Object> datosEstado = extraerEstadoEstrategia(estrategia);
            String datosJson = objectMapper.writeValueAsString(datosEstado);
            
            // Buscar estado existente o crear nuevo
            EstadoEstrategia estadoEstrategia = estadoRepository.buscarPorSesion(sesion.getId());
            if (estadoEstrategia == null) {
                estadoEstrategia = new EstadoEstrategia();
                estadoEstrategia.setSesion(sesion);
            }
            
            // Actualizar datos
            estadoEstrategia.setTipoEstrategia(estrategia.getNombre());
            estadoEstrategia.setDatosEstado(datosJson);
            estadoEstrategia.setProgreso(estrategia.getProgreso());
            
            // Guardar
            estadoRepository.guardar(estadoEstrategia);
            
        } catch (Exception e) {
            throw new PersistenceException("Error al guardar estado de estrategia", e);
        }
    }
    
    public EstrategiaAprendizaje restaurarEstadoEstrategia(Sesion sesion, String tipoEstrategia) {
        try {
            EstadoEstrategia estadoEstrategia = estadoRepository.buscarPorSesion(sesion.getId());
            if (estadoEstrategia == null) {
                return null;
            }
            
            // Deserializar datos
            Map<String, Object> datosEstado = objectMapper.readValue(
                estadoEstrategia.getDatosEstado(), 
                new TypeReference<Map<String, Object>>() {}
            );
            
            // Crear estrategia con estado restaurado
            return crearEstrategiaConEstado(tipoEstrategia, datosEstado, sesion.getBloqueId());
            
        } catch (Exception e) {
            throw new PersistenceException("Error al restaurar estado de estrategia", e);
        }
    }
    
    private Map<String, Object> extraerEstadoEstrategia(EstrategiaAprendizaje estrategia) {
        Map<String, Object> estado = new HashMap<>();
        
        if (estrategia instanceof EstrategiaSecuencial) {
            EstrategiaSecuencial secuencial = (EstrategiaSecuencial) estrategia;
            estado.put("indiceActual", secuencial.getIndiceActual());
            estado.put("totalPreguntas", secuencial.getTotalPreguntas());
            
        } else if (estrategia instanceof EstrategiaAleatoria) {
            EstrategiaAleatoria aleatoria = (EstrategiaAleatoria) estrategia;
            estado.put("preguntasDisponibles", aleatoria.getPreguntasDisponibles());
            estado.put("preguntasVistas", aleatoria.getPreguntasVistas());
            
        } else if (estrategia instanceof EstrategiaRepeticionIncorrectas) {
            EstrategiaRepeticionIncorrectas repeticion = (EstrategiaRepeticionIncorrectas) estrategia;
            estado.put("indiceActual", repeticion.getIndiceActual());
            estado.put("enFaseRepeticion", repeticion.estaEnFaseRepeticion());
            estado.put("preguntasIncorrectas", repeticion.getPreguntasIncorrectas());
            estado.put("colaIncorrectas", repeticion.getColaIncorrectas());
        }
        
        return estado;
    }
}
```

## 6. Plan de Implementación

### 6.1 Fase 1: Configuración JPA (1 semana)
- [ ] Configurar JPA con SQLite
- [ ] Crear entidades JPA
- [ ] Implementar repositorios básicos
- [ ] Testing de persistencia

### 6.2 Fase 2: Gestión de Sesiones (1 semana)
- [ ] Implementar SesionRepository
- [ ] Integrar con sistema de sesiones
- [ ] Testing de CRUD de sesiones

### 6.3 Fase 3: Gestión de Estrategias (1 semana)
- [ ] Implementar EstadoEstrategiaRepository
- [ ] Crear EstrategiaStateManager
- [ ] Integrar con estrategias existentes

### 6.4 Fase 4: Soporte JSON/YAML (1 semana)
- [ ] Implementar CursoLoader
- [ ] Agregar soporte JSON para cursos
- [ ] Testing de carga multi-formato

## 7. Conclusiones

### 7.1 Recomendaciones

1. **Empezar con persistencia JSON**: Simple y efectiva para prototipos
2. **Implementar backup automático**: Crítico para datos de usuario
3. **Usar persistencia asíncrona**: No bloquear la interfaz de usuario
4. **Validar datos**: Verificar integridad en cada operación

### 7.2 Próximos Pasos

1. **Implementar gestor JSON básico**
2. **Integrar con sistema de sesiones existente**
3. **Testing exhaustivo de persistencia**
4. **Documentar patrones de uso**

**Recomendación**: Comenzar con la implementación del gestor JSON básico y la integración con sesiones antes de proceder con las estrategias de aprendizaje.

---

## 8. Avances Implementados

### 8.1 Paso 1: Configuración JPA + SQLite ✅ COMPLETADO

#### **8.1.1 Dependencias Agregadas**

**kursor-core/pom.xml:**
```xml
<!-- JPA y Hibernate -->
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.2.13.Final</version>
</dependency>

<!-- SQLite -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.42.0.0</version>
</dependency>

<!-- Hibernate Community Dialects (incluye SQLite) -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
    <version>6.2.13.Final</version>
</dependency>

<!-- Jackson para JSON (datos de estrategias) -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.15.2</version>
</dependency>
```

#### **8.1.2 Configuración JPA**

**kursor-core/src/main/resources/META-INF/persistence.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                                 http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
             version="2.2">
    
    <persistence-unit name="kursorPU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        
        <!-- Entidades JPA -->
        <class>com.kursor.persistence.entity.Sesion</class>
        <class>com.kursor.persistence.entity.EstadoEstrategia</class>
        <class>com.kursor.persistence.entity.EstadisticasUsuario</class>
        <class>com.kursor.persistence.entity.RespuestaPregunta</class>
        
        <properties>
            <!-- Configuración de SQLite -->
            <property name="javax.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:sqlite:data/kursor.db"/>
            <property name="javax.persistence.jdbc.user" value=""/>
            <property name="javax.persistence.jdbc.password" value=""/>
            
            <!-- Configuración de Hibernate -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.SQLiteDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="false"/>
            <property name="hibernate.format_sql" value="true"/>
            
            <!-- Configuración de conexión -->
            <property name="hibernate.connection.pool_size" value="1"/>
            <property name="hibernate.connection.autocommit" value="false"/>
            
            <!-- Configuración de logging -->
            <property name="hibernate.use_sql_comments" value="true"/>
            <property name="hibernate.jdbc.batch_size" value="20"/>
            
            <!-- Configuración específica para SQLite -->
            <property name="hibernate.connection.foreign_keys" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

#### **8.1.3 Entidades JPA Implementadas**

**1. Sesion.java** - Estado de sesiones de aprendizaje:
```java
@Entity
@Table(name = "sesiones")
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;
    
    @Column(name = "curso_id", nullable = false)
    private String cursoId;
    
    @Column(name = "bloque_id", nullable = false)
    private String bloqueId;
    
    @Column(name = "estrategia_tipo", nullable = false)
    private String estrategiaTipo;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_ultima_actividad")
    private LocalDateTime fechaUltimaActividad;
    
    @Column(name = "tiempo_total")
    private Integer tiempoTotal = 0;
    
    @Column(name = "preguntas_respondidas")
    private Integer preguntasRespondidas = 0;
    
    @Column(name = "aciertos")
    private Integer aciertos = 0;
    
    @Column(name = "porcentaje_completitud")
    private Double porcentajeCompletitud = 0.0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSesion estado = EstadoSesion.ACTIVA;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EstadoEstrategia estadoEstrategia;
    
    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RespuestaPregunta> respuestas = new ArrayList<>();
    
    // Métodos de negocio: actualizarUltimaActividad(), agregarTiempo(), 
    // registrarRespuesta(), actualizarPorcentajeCompletitud(), completar(), 
    // pausar(), reanudar()
}
```

**2. EstadoSesion.java** - Enum para estados de sesión:
```java
public enum EstadoSesion {
    ACTIVA,     // Sesión activa - el usuario está actualmente trabajando
    PAUSADA,    // Sesión pausada - el usuario ha pausado temporalmente
    COMPLETADA, // Sesión completada - el usuario ha terminado
    CANCELADA   // Sesión cancelada - el usuario ha cancelado
}
```

**3. EstadoEstrategia.java** - Estado interno de estrategias:
```java
@Entity
@Table(name = "estados_estrategias")
public class EstadoEstrategia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;
    
    @Column(name = "tipo_estrategia", nullable = false)
    private String tipoEstrategia;
    
    @Column(name = "datos_estado", nullable = false, columnDefinition = "TEXT")
    private String datosEstado; // JSON serializado
    
    @Column(name = "progreso")
    private Double progreso = 0.0;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;
    
    // Constructores, getters, setters...
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaUltimaModificacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaUltimaModificacion = LocalDateTime.now();
    }
}
```

**4. RespuestaPregunta.java** - Historial de respuestas:
```java
@Entity
@Table(name = "respuestas_preguntas")
public class RespuestaPregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;
    
    @Column(name = "pregunta_id", nullable = false)
    private String preguntaId;
    
    @Column(name = "respuesta_correcta", nullable = false)
    private Boolean respuestaCorrecta;
    
    @Column(name = "tiempo_respuesta")
    private Integer tiempoRespuesta; // en segundos
    
    @Column(name = "intentos")
    private Integer intentos = 1;
    
    @Column(name = "texto_respuesta")
    private String textoRespuesta;
    
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
    
    // Métodos: incrementarIntentos(), esCorrecta(), getTiempoRespuestaFormateado()
}
```

**5. EstadisticasUsuario.java** - Estadísticas históricas:
```java
@Entity
@Table(name = "estadisticas_usuario")
public class EstadisticasUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;
    
    @Column(name = "curso_id", nullable = false)
    private String cursoId;
    
    @Column(name = "total_sesiones", nullable = false)
    private Integer totalSesiones;
    
    @Column(name = "total_preguntas_respondidas", nullable = false)
    private Integer totalPreguntasRespondidas;
    
    @Column(name = "total_respuestas_correctas", nullable = false)
    private Integer totalRespuestasCorrectas;
    
    @Column(name = "tiempo_total_estudio", nullable = false)
    private Long tiempoTotalEstudio; // en milisegundos
    
    @Column(name = "ultima_sesion")
    private LocalDateTime ultimaSesion;
    
    // Getters, setters, constructores...
}
```

#### **8.1.4 Estructura de Base de Datos Generada**

Hibernate generará automáticamente las siguientes tablas:

```sql
-- Tabla de sesiones
CREATE TABLE sesiones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id VARCHAR(255) NOT NULL,
    curso_id VARCHAR(255) NOT NULL,
    bloque_id VARCHAR(255) NOT NULL,
    estrategia_tipo VARCHAR(100) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_ultima_actividad TIMESTAMP,
    tiempo_total INTEGER DEFAULT 0,
    preguntas_respondidas INTEGER DEFAULT 0,
    aciertos INTEGER DEFAULT 0,
    porcentaje_completitud REAL DEFAULT 0.0,
    estado VARCHAR(50) DEFAULT 'ACTIVA',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de estado de estrategias
CREATE TABLE estados_estrategias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sesion_id INTEGER NOT NULL,
    tipo_estrategia VARCHAR(100) NOT NULL,
    datos_estado TEXT NOT NULL, -- JSON serializado
    progreso REAL DEFAULT 0.0,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sesion_id) REFERENCES sesiones(id) ON DELETE CASCADE
);

-- Tabla de respuestas de preguntas
CREATE TABLE respuestas_preguntas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sesion_id INTEGER NOT NULL,
    pregunta_id VARCHAR(255) NOT NULL,
    respuesta_correcta BOOLEAN NOT NULL,
    tiempo_respuesta INTEGER,
    intentos INTEGER DEFAULT 1,
    texto_respuesta VARCHAR(255),
    fecha_respuesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sesion_id) REFERENCES sesiones(id) ON DELETE CASCADE
);

-- Tabla de estadísticas de usuario
CREATE TABLE estadisticas_usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id VARCHAR(255) NOT NULL,
    curso_id VARCHAR(255) NOT NULL,
    tiempo_total INTEGER DEFAULT 0,
    sesiones_completadas INTEGER DEFAULT 0,
    mejor_racha_dias INTEGER DEFAULT 0,
    racha_actual_dias INTEGER DEFAULT 0,
    fecha_ultima_sesion TIMESTAMP,
    fecha_primera_sesion TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **8.1.5 Características Implementadas**

✅ **Persistencia JPA completa** con SQLite
✅ **Entidades con relaciones** (OneToOne, OneToMany)
✅ **Métodos de negocio** en las entidades
✅ **Callbacks JPA** (@PrePersist, @PreUpdate)
✅ **Enums** para estados
✅ **Campos de auditoría** (created_at, updated_at)
✅ **Configuración automática** de base de datos

### 8.2 Paso 2: Repositorios JPA ✅ COMPLETADO

**Estado**: ✅ COMPLETADO

**Objetivo**: Crear los repositorios JPA para manejar las operaciones de base de datos de forma eficiente y segura.

#### **8.2.1 Repositorios Implementados**

**1. SesionRepository.java** - Gestión de sesiones de aprendizaje:
```java
public class SesionRepository {
    // Métodos principales:
    public Sesion guardar(Sesion sesion)
    public Optional<Sesion> buscarPorId(Long id)
    public Sesion buscarSesionActiva(String usuarioId)
    public List<Sesion> buscarSesionesUsuario(String usuarioId)
    public List<Sesion> buscarSesionesUsuarioPorCurso(String usuarioId, String cursoId)
    public List<Sesion> buscarSesionesPorEstado(EstadoSesion estado)
    public List<Sesion> buscarSesionesInactivas(int diasInactividad)
    public boolean eliminar(Long id)
    public int eliminarSesionesUsuario(String usuarioId)
    public long contarSesionesUsuario(String usuarioId)
    public long contarSesionesCompletadas(String usuarioId)
}
```

**2. EstadoEstrategiaRepository.java** - Gestión de estado interno de estrategias:
```java
public class EstadoEstrategiaRepository {
    // Métodos principales:
    public EstadoEstrategia guardar(EstadoEstrategia estadoEstrategia)
    public Optional<EstadoEstrategia> buscarPorId(Long id)
    public Optional<EstadoEstrategia> buscarPorSesion(Long sesionId)
    public List<EstadoEstrategia> buscarPorTipo(String tipoEstrategia)
    public List<EstadoEstrategia> buscarPorUsuario(String usuarioId)
    public List<EstadoEstrategia> buscarPorUsuarioYCurso(String usuarioId, String cursoId)
    public List<EstadoEstrategia> buscarModificadosDespues(LocalDateTime fechaLimite)
    public boolean actualizarProgreso(Long id, Double progreso)
    public boolean actualizarDatosEstado(Long id, String datosEstado)
    public boolean eliminar(Long id)
    public int eliminarPorUsuario(String usuarioId)
    public long contarPorUsuario(String usuarioId)
    public long contarPorTipo(String tipoEstrategia)
}
```

**3. RespuestaPreguntaRepository.java** - Gestión de historial de respuestas:
```java
public class RespuestaPreguntaRepository {
    // Métodos principales:
    public RespuestaPregunta guardar(RespuestaPregunta respuestaPregunta)
    public Optional<RespuestaPregunta> buscarPorId(Long id)
    public List<RespuestaPregunta> buscarPorSesion(Long sesionId)
    public List<RespuestaPregunta> buscarPorPregunta(String preguntaId)
    public List<RespuestaPregunta> buscarPorUsuario(String usuarioId)
    public List<RespuestaPregunta> buscarPorUsuarioYCurso(String usuarioId, String cursoId)
    public List<RespuestaPregunta> buscarRespuestasCorrectas(String usuarioId)
    public List<RespuestaPregunta> buscarRespuestasIncorrectas(String usuarioId)
    public List<RespuestaPregunta> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin)
    public List<RespuestaPregunta> buscarConMultiplesIntentos(int intentosMinimos)
    public boolean actualizarIntentos(Long id, Integer intentos)
    public boolean eliminar(Long id)
    public int eliminarPorSesion(Long sesionId)
    public int eliminarPorUsuario(String usuarioId)
    public long contarPorUsuario(String usuarioId)
    public long contarRespuestasCorrectas(String usuarioId)
    public double calcularPorcentajeAciertos(String usuarioId)
}
```

**4. EstadisticasUsuarioRepository.java** - Gestión de estadísticas históricas:
```java
public class EstadisticasUsuarioRepository {
    // Métodos principales:
    public EstadisticasUsuario guardar(EstadisticasUsuario estadisticasUsuario)
    public Optional<EstadisticasUsuario> buscarPorId(Long id)
    public Optional<EstadisticasUsuario> buscarPorUsuarioYCurso(String usuarioId, String cursoId)
    public List<EstadisticasUsuario> buscarPorUsuario(String usuarioId)
    public List<EstadisticasUsuario> buscarPorCurso(String cursoId)
    public List<EstadisticasUsuario> buscarMejoresRachas(int limite)
    public List<EstadisticasUsuario> buscarUsuariosMasActivos(int limite)
    public List<EstadisticasUsuario> buscarUsuariosInactivos(int diasInactividad)
    public boolean agregarTiempo(Long id, Integer tiempoAdicional)
    public boolean registrarSesionCompletada(Long id)
    public boolean actualizarRacha(Long id, Integer rachaActual, Integer mejorRacha)
    public boolean actualizarFechaUltimaSesion(Long id, LocalDateTime fechaUltimaSesion)
    public boolean eliminar(Long id)
    public int eliminarPorUsuario(String usuarioId)
    public int eliminarPorCurso(String cursoId)
    public long contarPorUsuario(String usuarioId)
    public long contarPorCurso(String cursoId)
    public double calcularTiempoPromedioCurso(String cursoId)
}
```

#### **8.2.2 Configuración del EntityManagerFactory**

**PersistenceConfig.java** - Configuración centralizada de JPA:
```java
public class PersistenceConfig {
    // Métodos principales:
    public static void initialize()
    public static EntityManagerFactory getEntityManagerFactory()
    public static EntityManager createEntityManager()
    public static void shutdown()
    public static boolean isInitialized()
    public static String getDatabasePath()
    public static boolean databaseExists()
    public static long getDatabaseSize()
    public static String getDatabaseInfo()
}
```

**Características de la configuración:**
- ✅ **Inicialización automática** del directorio de datos
- ✅ **Configuración SQLite** optimizada
- ✅ **Gestión de conexiones** con pool de tamaño 1
- ✅ **Logging detallado** de operaciones
- ✅ **Métodos de utilidad** para información de BD
- ✅ **Cierre seguro** de recursos

#### **8.2.3 Pruebas de Integración**

**PersistenceTest.java** - Pruebas completas de la persistencia:
```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersistenceTest {
    // Pruebas implementadas:
    @Test void testCrearSesion()
    @Test void testBuscarSesion()
    @Test void testCrearEstadoEstrategia()
    @Test void testCrearRespuestaPregunta()
    @Test void testCrearEstadisticasUsuario()
    @Test void testBuscarSesionesPorUsuario()
    @Test void testBuscarEstadoEstrategiaPorSesion()
    @Test void testBuscarRespuestasPorSesion()
    @Test void testBuscarEstadisticasPorUsuarioYCurso()
    @Test void testActualizarProgresoEstadoEstrategia()
    @Test void testActualizarIntentosRespuesta()
    @Test void testAgregarTiempoEstadisticas()
    @Test void testContarEntidades()
    @Test void testCalcularPorcentajeAciertos()
    @Test void testLimpiarDatos()
}
```

**Cobertura de pruebas:**
- ✅ **Operaciones CRUD** completas para todas las entidades
- ✅ **Búsquedas complejas** con filtros y ordenamiento
- ✅ **Actualizaciones específicas** de campos
- ✅ **Cálculos estadísticos** (porcentajes, conteos)
- ✅ **Gestión de transacciones** JPA
- ✅ **Limpieza de datos** de prueba

#### **8.2.4 Características Implementadas**

✅ **Repositorios completos** con métodos CRUD y consultas específicas
✅ **Gestión de transacciones** JPA automática
✅ **Logging detallado** en todas las operaciones
✅ **Manejo de errores** robusto con excepciones descriptivas
✅ **Consultas optimizadas** con índices y relaciones
✅ **Métodos de utilidad** para estadísticas y análisis
✅ **Pruebas de integración** completas
✅ **Configuración centralizada** del EntityManagerFactory

### 8.3 Paso 3: Integración con Estrategias ✅ COMPLETADO

**Estado**: ✅ COMPLETADO

**Objetivo**: Integrar los repositorios JPA con las estrategias de aprendizaje como módulos de carga dinámica.

#### **8.3.1 Interfaz EstrategiaAprendizaje Actualizada**

**EstrategiaAprendizaje.java** - Interfaz mejorada con nuevos métodos:
```java
public interface EstrategiaAprendizaje extends Serializable {
    // Métodos principales:
    String getNombre();
    Pregunta primeraPregunta();
    void registrarRespuesta(Respuesta respuesta);
    boolean hayMasPreguntas();
    Pregunta siguientePregunta();
    double getProgreso();
    void guardarEstado();
    void restaurarEstado();
    void reiniciar();
}
```

**Características de la interfaz actualizada:**
- ✅ **Flujo iterativo claro** con `primeraPregunta()` y `siguientePregunta()`
- ✅ **Reactividad** con `registrarRespuesta()` sin parámetro pregunta
- ✅ **Control de flujo** con `hayMasPreguntas()`
- ✅ **Seguimiento de progreso** con `getProgreso()`
- ✅ **Persistencia** con `guardarEstado()` y `restaurarEstado()`
- ✅ **Reinicio** con `reiniciar()`

#### **8.3.2 Clase Respuesta Implementada**

**Respuesta.java** - Clase simple para respuestas de usuario:
```java
public class Respuesta implements Serializable {
    private final boolean esCorrecta;
    private final String textoRespuesta;
    private final int tiempoRespuesta;
    private final int intentos;
    private final LocalDateTime fechaRespuesta;
    
    // Constructores y métodos:
    public Respuesta(boolean esCorrecta, String textoRespuesta, int tiempoRespuesta, int intentos)
    public Respuesta(boolean esCorrecta, String textoRespuesta)
    public Respuesta(boolean esCorrecta)
    public boolean esCorrecta()
    public String getTextoRespuesta()
    public int getTiempoRespuesta()
    public int getIntentos()
    public LocalDateTime getFechaRespuesta()
    public String getTiempoRespuestaFormateado()
    public boolean tieneTexto()
    public boolean tomoTiempo()
    public boolean requirioMultiplesIntentos()
}
```

#### **8.3.3 Estrategias Implementadas**

**1. EstrategiaSecuencial.java** - Estrategia secuencial actualizada:
```java
public class EstrategiaSecuencial implements EstrategiaAprendizaje {
    // Características:
    // - Recorre preguntas en orden secuencial
    // - No reacciona a respuestas del usuario
    // - Progreso lineal basado en índice actual
    // - Completa todas las preguntas una vez
    
    // Métodos implementados:
    public Pregunta primeraPregunta()
    public void registrarRespuesta(Respuesta respuesta) // No reacciona
    public boolean hayMasPreguntas()
    public Pregunta siguientePregunta()
    public double getProgreso()
    public void reiniciar()
}
```

**2. EstrategiaAleatoria.java** - Estrategia aleatoria actualizada:
```java
public class EstrategiaAleatoria implements EstrategiaAprendizaje {
    // Características:
    // - Selecciona preguntas de forma aleatoria
    // - No reacciona a respuestas del usuario
    // - Puede repetir preguntas indefinidamente
    // - Progreso relativo basado en preguntas procesadas
    
    // Métodos implementados:
    public Pregunta primeraPregunta()
    public void registrarRespuesta(Respuesta respuesta) // No reacciona
    public boolean hayMasPreguntas()
    public Pregunta siguientePregunta()
    public double getProgreso()
    public void reiniciar()
}
```

**3. EstrategiaRepeticionEspaciada.java** - Estrategia de repetición espaciada actualizada:
```java
public class EstrategiaRepeticionEspaciada implements EstrategiaAprendizaje {
    // Características:
    // - Repite preguntas en intervalos regulares
    // - Intervalo configurable (por defecto 3)
    // - No reacciona a respuestas del usuario
    // - Ciclo completo que vuelve al inicio
    
    // Métodos implementados:
    public Pregunta primeraPregunta()
    public void registrarRespuesta(Respuesta respuesta) // No reacciona
    public boolean hayMasPreguntas()
    public Pregunta siguientePregunta()
    public double getProgreso()
    public void reiniciar()
    public int getIntervalo()
    public void setIntervalo(int intervalo)
}
```

**4. EstrategiaRepetirIncorrectas.java** - Nueva estrategia implementada:
```java
public class EstrategiaRepetirIncorrectas implements EstrategiaAprendizaje {
    // Características:
    // - Dos fases: originales + incorrectas
    // - Reactiva: registra preguntas incorrectas
    // - Evita duplicados en cola de incorrectas
    // - Progreso basado en preguntas procesadas
    
    // Estado interno:
    private List<Pregunta> preguntasBloque;
    private Queue<Pregunta> colaIncorrectas;
    private int indiceActual = 0;
    private boolean enFaseRepeticion = false;
    private Set<String> preguntasIncorrectas;
    private Pregunta preguntaActual;
    
    // Métodos implementados:
    public Pregunta primeraPregunta()
    public void registrarRespuesta(Respuesta respuesta) // Reactiva
    public boolean hayMasPreguntas()
    public Pregunta siguientePregunta()
    public double getProgreso()
    public void reiniciar()
    public int getCantidadIncorrectas()
    public boolean estaEnFaseRepeticion()
}
```

#### **8.3.4 Algoritmo de EstrategiaRepetirIncorrectas**

**Flujo de la estrategia:**
1. **Fase 1 - Preguntas originales:**
   - Recorre todas las preguntas del bloque en orden secuencial
   - Registra preguntas incorrectas en una cola
   - Evita duplicados usando un Set de IDs

2. **Fase 2 - Repetición de incorrectas:**
   - Al terminar las originales, cambia a fase de repetición
   - Repite solo las preguntas que el usuario falló
   - Termina cuando no hay más incorrectas para repetir

**Ejemplo de flujo:**
```
Preguntas: [A, B, C, D]
Respuestas: A(correcta), B(incorrecta), C(correcta), D(incorrecta)
Secuencia: A, B, C, D, B, D
```

#### **8.3.5 Pruebas de Integración**

**EstrategiasTest.java** - Pruebas completas de todas las estrategias:
```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EstrategiasTest {
    // Pruebas implementadas:
    @Test void testEstrategiaSecuencial()
    @Test void testEstrategiaAleatoria()
    @Test void testEstrategiaRepeticionEspaciada()
    @Test void testEstrategiaRepetirIncorrectas()
    @Test void testMetodosPersistencia()
    @Test void testCasosEdge()
}
```

**Cobertura de pruebas:**
- ✅ **Flujo completo** de cada estrategia
- ✅ **Reactividad** de EstrategiaRepetirIncorrectas
- ✅ **Progreso** y estado interno
- ✅ **Casos edge** (lista vacía, una pregunta)
- ✅ **Métodos de persistencia** (no lanzan excepciones)
- ✅ **Reinicio** de estrategias

#### **8.3.6 Características Implementadas**

✅ **4 estrategias completas** con implementación de interfaz actualizada
✅ **Estrategia reactiva** (RepetirIncorrectas) que ajusta comportamiento
✅ **3 estrategias no reactivas** (Secuencial, Aleatoria, Repetición Espaciada)
✅ **Flujo iterativo estándar** con `primeraPregunta()` y `siguientePregunta()`
✅ **Control de progreso** con `getProgreso()` en todas las estrategias
✅ **Métodos de persistencia** preparados para integración JPA
✅ **Pruebas exhaustivas** de todas las estrategias
✅ **Casos edge** manejados correctamente
✅ **Documentación completa** de algoritmos y comportamiento

### 8.4 Próximo Paso: Integración JPA con Estrategias

**Estado**: 🔄 EN PROGRESO

**Objetivo**: Integrar las estrategias con los repositorios JPA para persistencia completa del estado.

**Tareas**:
- [ ] Implementar `EstrategiaStateManager` con JPA
- [ ] Integrar `guardarEstado()` y `restaurarEstado()` con repositorios
- [ ] Crear serialización/deserialización JSON de estado
- [ ] Testing de persistencia con estrategias
- [ ] Documentación de integración completa 

## 8. Integración con Arquitectura Modular de Estrategias

### 8.1 Contexto de la Modularización

Con la nueva arquitectura modular de estrategias, donde cada estrategia se implementa como un módulo independiente siguiendo el patrón `kursor-(nombre)-strategy`, el sistema de persistencia debe adaptarse para trabajar con esta nueva estructura.

### 8.2 Cambios en la Arquitectura de Persistencia

#### **8.2.1 StrategyManager**

El `StrategyManager` será responsable de cargar dinámicamente las estrategias desde los JARs ubicados en la carpeta `/strategies/`:

```java
public class StrategyManager {
    private final Map<String, EstrategiaModule> estrategiasCargadas;
    private final String directorioStrategies;
    
    public StrategyManager(String directorioStrategies) {
        this.directorioStrategies = directorioStrategies;
        this.estrategiasCargadas = new HashMap<>();
        cargarEstrategias();
    }
    
    public void cargarEstrategias() {
        // Cargar JARs desde /strategies/ usando ServiceLoader
        File directorio = new File(directorioStrategies);
        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".jar"));
            if (archivos != null) {
                for (File archivo : archivos) {
                    cargarEstrategiaDesdeJar(archivo);
                }
            }
        }
    }
    
    public EstrategiaAprendizaje crearEstrategia(String nombre, List<Pregunta> preguntas) {
        EstrategiaModule modulo = estrategiasCargadas.get(nombre);
        if (modulo == null) {
            throw new IllegalArgumentException("Estrategia no encontrada: " + nombre);
        }
        return modulo.crearEstrategia(preguntas);
    }
}
```

#### **8.2.2 EstrategiaStateManager Actualizado**

El `EstrategiaStateManager` se actualiza para trabajar con el `StrategyManager`:

```java
public class EstrategiaStateManager {
    private final ObjectMapper objectMapper;
    private final EstadoEstrategiaRepository estadoRepository;
    private final StrategyManager strategyManager;
    private final EntityManager entityManager;
    
    public EstrategiaStateManager(EntityManager entityManager, StrategyManager strategyManager) {
        this.entityManager = entityManager;
        this.estadoRepository = new EstadoEstrategiaRepository(entityManager);
        this.strategyManager = strategyManager;
        this.objectMapper = new ObjectMapper();
    }
    
    public EstrategiaAprendizaje restaurarEstadoEstrategia(Sesion sesion, String tipoEstrategia) {
        // Usar StrategyManager para crear la estrategia
        List<Pregunta> preguntas = cargarPreguntasDelBloque(sesion.getBloqueId());
        EstrategiaAprendizaje estrategia = strategyManager.crearEstrategia(tipoEstrategia, preguntas);
        
        // Restaurar estado desde JSON...
        return estrategia;
    }
}
```

### 8.3 Estructura de Distribución Final

```
kursor-portable/
├── strategies/
│   ├── kursor-secuencial-strategy.jar
│   ├── kursor-aleatoria-strategy.jar
│   ├── kursor-repeticion-espaciada-strategy.jar
│   └── kursor-repetir-incorrectas-strategy.jar
├── modules/
│   ├── kursor-multiplechoice-module.jar
│   ├── kursor-truefalse-module.jar
│   ├── kursor-flashcard-module.jar
│   └── kursor-fillblanks-module.jar
├── kursor-core.jar
├── kursor.db
└── [otros archivos de distribución]
```

### 8.4 Flujo de Persistencia con Estrategias Modulares

#### **8.4.1 Inicio de Sesión**

```java
// 1. Crear sesión en base de datos
Sesion sesion = new Sesion();
sesion.setUsuarioId(usuarioId);
sesion.setCursoId(cursoId);
sesion.setBloqueId(bloqueId);
sesion.setEstrategiaTipo(tipoEstrategia);
sesion.setFechaInicio(LocalDateTime.now());
sesion.setEstado(EstadoSesion.ACTIVA);
sesion.setProgreso(0.0);

sesionRepository.guardar(sesion);

// 2. Crear estrategia usando StrategyManager
List<Pregunta> preguntas = cargarPreguntasDelBloque(bloqueId);
EstrategiaAprendizaje estrategia = strategyManager.crearEstrategia(tipoEstrategia, preguntas);

// 3. Guardar estado inicial
estrategiaStateManager.guardarEstadoEstrategia(sesion, estrategia);
```

#### **8.4.2 Reanudar Sesión**

```java
// 1. Buscar sesión existente
Sesion sesion = sesionRepository.buscarPorId(sesionId).orElse(null);
if (sesion != null) {
    // 2. Restaurar estrategia usando StrategyManager
    EstrategiaAprendizaje estrategia = estrategiaStateManager.restaurarEstadoEstrategia(sesion, sesion.getEstrategiaTipo());
    
    // 3. Continuar con la sesión
    return estrategia;
}
```

### 8.5 Compatibilidad y Migración

#### **8.5.1 Compatibilidad Hacia Atrás**

- La interfaz `EstrategiaAprendizaje` se mantiene igual
- Los datos JSON de estado son compatibles
- Las entidades JPA no cambian
- El `EstrategiaStateManager` mantiene su API pública

#### **8.5.2 Plan de Migración**

1. **Fase 1**: Implementar `StrategyManager` y `EstrategiaModule`
2. **Fase 2**: Migrar cada estrategia a su módulo independiente
3. **Fase 3**: Actualizar `EstrategiaStateManager` para usar `StrategyManager`
4. **Fase 4**: Remover estrategias del módulo core
5. **Fase 5**: Actualizar pruebas y documentación

### 8.6 Ventajas de la Integración

#### **8.6.1 Técnicas**
- **Carga dinámica**: Estrategias se cargan solo cuando se necesitan
- **Escalabilidad**: Fácil agregar nuevas estrategias sin modificar el core
- **Mantenibilidad**: Cada estrategia es independiente y puede actualizarse por separado
- **Testing**: Pruebas unitarias independientes por estrategia

#### **8.6.2 Arquitectónicas**
- **Consistencia**: Mismo patrón que los módulos de preguntas
- **Flexibilidad**: Estrategias pueden tener dependencias específicas
- **Reutilización**: Estrategias pueden usarse en otros proyectos
- **Distribución**: JARs independientes en `/strategies/`

### 8.7 Consideraciones de Rendimiento

#### **8.7.1 Carga de Estrategias**
- Las estrategias se cargan una sola vez al inicio
- Uso de `ServiceLoader` para carga dinámica eficiente
- Cache de estrategias cargadas en memoria

#### **8.7.2 Persistencia**
- El estado JSON se mantiene compacto
- Consultas optimizadas para sesiones activas
- Limpieza automática de estados obsoletos

### 8.8 Próximos Pasos

1. **Implementar StrategyManager**: Crear el gestor de estrategias modulares
2. **Definir EstrategiaModule**: Crear la interfaz para módulos de estrategias
3. **Migrar estrategias**: Mover cada estrategia a su módulo independiente
4. **Actualizar persistencia**: Integrar EstrategiaStateManager con StrategyManager
5. **Pruebas completas**: Verificar todo el flujo de persistencia con estrategias modulares
6. **Documentación**: Actualizar guías de usuario y desarrollador

---

**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Fecha:** 2024-12-19  
**Versión:** 2.0.0  
**Estado:** En desarrollo 