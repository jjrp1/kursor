---
title: Plan Inicial de Persistencia
subtitle: Documentación técnica del sistema de persistencia
description: Análisis y propuestas para la persistencia de sesiones y estrategias en Kursor
keywords: persistencia, sesiones, estrategias, serialización, estado
status: desarrollo
created: 2025-01-27
modified: 2025-01-27
author: "Juanjo Ruiz"
---

# Plan Inicial de Persistencia

## 1. Introducción

### 1.1 Objetivo

Este documento analiza los requisitos y propuestas para la persistencia de datos en Kursor, específicamente:
- **Persistencia de Sesiones**: Estado del usuario durante el aprendizaje
- **Persistencia de Estrategias**: Estado interno de las estrategias de aprendizaje
- **Gestión de Estado**: Cómo mantener y restaurar el progreso del usuario

### 1.2 Contexto

El sistema de aprendizaje de Kursor requiere persistencia robusta para:
- Permitir pausar y continuar sesiones de aprendizaje
- Mantener estadísticas de progreso del usuario
- Restaurar el estado exacto de las estrategias de aprendizaje
- Soportar múltiples usuarios y cursos

## 2. Análisis de Requisitos

### 2.1 ¿Qué Necesitamos Persistir?

#### **2.1.1 Sesión de Aprendizaje**
```java
public class Sesion {
    private Long id;
    private Usuario usuario;
    private Curso curso;
    private Bloque bloqueActual;
    private EstrategiaAprendizaje estrategia;
    private Date fechaInicio;
    private Date fechaUltimaActividad;
    private int tiempoTotal;
    private int preguntasRespondidas;
    private int aciertos;
    private double porcentajeCompletitud;
    private EstadoSesion estado; // ACTIVA, PAUSADA, COMPLETADA
}
```

#### **2.1.2 Estado de Estrategia**
```java
public class EstadoEstrategia {
    private String tipoEstrategia;           // "Secuencial", "Aleatoria", etc.
    private Map<String, Object> datosEstado; // Datos específicos de la estrategia
    private int progreso;                    // Progreso general
    private Date fechaCreacion;
    private Date fechaUltimaModificacion;
}
```

#### **2.1.3 Datos Específicos por Estrategia**

**Estrategia Secuencial:**
```json
{
  "tipoEstrategia": "Secuencial",
  "datosEstado": {
    "indiceActual": 5,
    "totalPreguntas": 20
  }
}
```

**Estrategia Aleatoria:**
```json
{
  "tipoEstrategia": "Aleatoria",
  "datosEstado": {
    "preguntasDisponibles": [1, 3, 7, 12, 15],
    "preguntasVistas": [2, 4, 6, 8, 9, 10, 11, 13, 14, 16, 17, 18, 19, 20]
  }
}
```

**Estrategia Repetición de Incorrectas:**
```json
{
  "tipoEstrategia": "RepeticionIncorrectas",
  "datosEstado": {
    "indiceActual": 8,
    "enFaseRepeticion": false,
    "preguntasIncorrectas": [3, 7, 12],
    "colaIncorrectas": [3, 7, 12]
  }
}
```

### 2.2 Preguntas Clave

#### **2.2.1 ¿Dónde se guarda?**
- **Opción A**: Archivos JSON/YAML en sistema de archivos
- **Opción B**: Base de datos SQLite/H2
- **Opción C**: Memoria con backup periódico
- **Opción D**: Combinación (archivos + base de datos)

#### **2.2.2 ¿Cuándo se guarda?**
- **Después de cada respuesta**: Persistencia inmediata
- **Al cerrar la aplicación**: Persistencia al final
- **Periódicamente**: Cada N minutos o acciones
- **Manual**: Al pausar sesión

#### **2.2.3 ¿Qué se guarda exactamente?**
- **Solo datos primitivos**: IDs, índices, contadores
- **Objetos completos**: Serialización completa
- **Referencias**: IDs de preguntas y objetos relacionados
- **Metadatos**: Información adicional para debugging

#### **2.2.4 ¿Cómo se restaura?**
- **Al abrir la aplicación**: Restauración automática
- **Al cambiar de bloque**: Restauración por contexto
- **Manual**: Restauración bajo demanda
- **Incremental**: Restauración parcial

## 3. Propuesta de Arquitectura

### 3.1 Estrategia de Persistencia Híbrida

#### **3.1.1 Niveles de Persistencia**

**Nivel 1: Memoria (RAM)**
- Estado actual de sesiones activas
- Acceso más rápido
- Pérdida al cerrar aplicación

**Nivel 2: Archivos Locales (JSON)**
- Sesiones pausadas
- Configuraciones de usuario
- Backup de estado crítico

**Nivel 3: Base de Datos (SQLite)**
- Estadísticas históricas
- Progreso a largo plazo
- Datos de múltiples usuarios

#### **3.1.2 Estructura de Archivos**

```
kursor-portable/
├── data/
│   ├── sessions/
│   │   ├── session_20250127_143022.json
│   │   ├── session_20250127_150145.json
│   │   └── current_session.json
│   ├── strategies/
│   │   ├── secuencial_20250127_143022.json
│   │   ├── aleatoria_20250127_150145.json
│   │   └── repeticion_20250127_143022.json
│   └── users/
│       ├── user_1/
│       │   ├── progress.json
│       │   └── statistics.json
│       └── user_2/
│           ├── progress.json
│           └── statistics.json
```

### 3.2 Interfaces de Persistencia

#### **3.2.1 Gestor de Sesiones**

```java
public interface SesionPersistenceManager {
    
    /**
     * Guarda el estado actual de una sesión.
     */
    void guardarSesion(Sesion sesion);
    
    /**
     * Restaura una sesión desde persistencia.
     */
    Sesion restaurarSesion(Long sesionId);
    
    /**
     * Obtiene la sesión activa del usuario.
     */
    Sesion obtenerSesionActiva(Usuario usuario);
    
    /**
     * Lista todas las sesiones de un usuario.
     */
    List<Sesion> listarSesiones(Usuario usuario);
    
    /**
     * Elimina una sesión.
     */
    void eliminarSesion(Long sesionId);
    
    /**
     * Marca una sesión como completada.
     */
    void completarSesion(Long sesionId);
}
```

#### **3.2.2 Gestor de Estrategias**

```java
public interface EstrategiaPersistenceManager {
    
    /**
     * Guarda el estado de una estrategia.
     */
    void guardarEstadoEstrategia(String sesionId, EstrategiaAprendizaje estrategia);
    
    /**
     * Restaura el estado de una estrategia.
     */
    EstrategiaAprendizaje restaurarEstadoEstrategia(String sesionId, String tipoEstrategia);
    
    /**
     * Verifica si existe estado guardado para una estrategia.
     */
    boolean existeEstadoEstrategia(String sesionId, String tipoEstrategia);
    
    /**
     * Elimina el estado de una estrategia.
     */
    void eliminarEstadoEstrategia(String sesionId, String tipoEstrategia);
}
```

### 3.3 Implementación de Persistencia

#### **3.3.1 Serialización JSON**

```java
public class JsonPersistenceManager implements SesionPersistenceManager {
    
    private final ObjectMapper objectMapper;
    private final String dataDirectory;
    
    public JsonPersistenceManager(String dataDirectory) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.dataDirectory = dataDirectory;
    }
    
    @Override
    public void guardarSesion(Sesion sesion) {
        try {
            String filename = String.format("session_%s.json", 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            Path filePath = Paths.get(dataDirectory, "sessions", filename);
            
            // Crear directorio si no existe
            Files.createDirectories(filePath.getParent());
            
            // Serializar sesión
            String json = objectMapper.writeValueAsString(sesion);
            Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
            
        } catch (IOException e) {
            throw new PersistenceException("Error al guardar sesión", e);
        }
    }
    
    @Override
    public Sesion restaurarSesion(Long sesionId) {
        try {
            // Buscar archivo de sesión
            Path sessionFile = encontrarArchivoSesion(sesionId);
            if (sessionFile == null) {
                return null;
            }
            
            // Deserializar sesión
            String json = Files.readString(sessionFile);
            return objectMapper.readValue(json, Sesion.class);
            
        } catch (IOException e) {
            throw new PersistenceException("Error al restaurar sesión", e);
        }
    }
}
```

#### **3.3.2 Gestión de Estado de Estrategias**

```java
public class EstrategiaStateManager {
    
    private final EstrategiaPersistenceManager persistenceManager;
    
    public void guardarEstadoEstrategia(String sesionId, EstrategiaAprendizaje estrategia) {
        // Extraer datos específicos de la estrategia
        Map<String, Object> estado = extraerEstadoEstrategia(estrategia);
        
        // Crear objeto de estado
        EstadoEstrategia estadoEstrategia = new EstadoEstrategia();
        estadoEstrategia.setTipoEstrategia(estrategia.getNombre());
        estadoEstrategia.setDatosEstado(estado);
        estadoEstrategia.setFechaUltimaModificacion(new Date());
        
        // Guardar estado
        persistenceManager.guardarEstadoEstrategia(sesionId, estrategia);
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

## 4. Estrategias de Backup y Recuperación

### 4.1 Backup Automático

#### **4.1.1 Política de Backup**
- **Backup incremental**: Cada 5 minutos durante sesión activa
- **Backup completo**: Al pausar o completar sesión
- **Backup de seguridad**: Al cerrar aplicación

#### **4.1.2 Estructura de Backup**
```
data/
├── backup/
│   ├── 20250127/
│   │   ├── sessions_backup_143022.json
│   │   ├── sessions_backup_143527.json
│   │   └── sessions_backup_144032.json
│   └── 20250128/
│       └── sessions_backup_090000.json
```

### 4.2 Recuperación de Errores

#### **4.2.1 Detección de Corrupción**
- **Checksum**: Verificar integridad de archivos
- **Validación JSON**: Verificar estructura de datos
- **Logs de error**: Registrar problemas de persistencia

#### **4.2.2 Estrategias de Recuperación**
- **Backup más reciente**: Restaurar desde último backup válido
- **Estado por defecto**: Reiniciar sesión desde el principio
- **Recuperación parcial**: Restaurar solo datos válidos

## 5. Consideraciones de Rendimiento

### 5.1 Optimizaciones

#### **5.1.1 Persistencia Asíncrona**
```java
public class AsyncPersistenceManager {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public void guardarSesionAsync(Sesion sesion) {
        executor.submit(() -> {
            try {
                guardarSesion(sesion);
            } catch (Exception e) {
                logger.error("Error en persistencia asíncrona", e);
            }
        });
    }
}
```

#### **5.1.2 Compresión de Datos**
- **GZIP**: Comprimir archivos JSON grandes
- **Deduplicación**: Evitar datos redundantes
- **Limpieza**: Eliminar datos antiguos

### 5.2 Monitoreo

#### **5.2.1 Métricas de Rendimiento**
- **Tiempo de guardado**: Duración de operaciones de persistencia
- **Tamaño de archivos**: Monitorear crecimiento de datos
- **Frecuencia de acceso**: Patrones de uso

#### **5.2.2 Alertas**
- **Espacio en disco**: Advertencias cuando se agote
- **Errores de persistencia**: Notificar problemas críticos
- **Rendimiento lento**: Alertar sobre degradación

## 6. Plan de Implementación

### 6.1 Fase 1: Infraestructura Básica (1-2 semanas)
- [ ] Definir interfaces de persistencia
- [ ] Implementar gestor JSON básico
- [ ] Crear estructura de directorios
- [ ] Testing de serialización/deserialización

### 6.2 Fase 2: Integración con Sesiones (1 semana)
- [ ] Integrar persistencia con sistema de sesiones
- [ ] Implementar guardado automático
- [ ] Testing de restauración de sesiones

### 6.3 Fase 3: Integración con Estrategias (1 semana)
- [ ] Implementar persistencia de estado de estrategias
- [ ] Integrar con estrategias existentes
- [ ] Testing de restauración de estrategias

### 6.4 Fase 4: Optimización y Backup (1 semana)
- [ ] Implementar backup automático
- [ ] Optimizar rendimiento
- [ ] Testing de recuperación de errores

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