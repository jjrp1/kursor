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

### 7.1 Ventajas de la Arquitectura

1. **JPA + SQLite**: Persistencia robusta y portable
2. **JSON/YAML**: Flexibilidad en formatos de cursos
3. **Separación clara**: Estado vs contenido
4. **Escalabilidad**: Fácil migración a otros SGBD

### 7.2 Próximos Pasos

1. **Configurar JPA con SQLite**
2. **Implementar entidades y repositorios**
3. **Integrar con sistema de sesiones**
4. **Agregar soporte JSON para cursos**

**Recomendación**: Comenzar con la configuración de JPA y la implementación de las entidades básicas antes de proceder con la integración de estrategias. 