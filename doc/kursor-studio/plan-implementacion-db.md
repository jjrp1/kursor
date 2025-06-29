# Plan de Implementación - Conexión Real a Base de Datos

**Fecha:** 28 de Junio de 2025  
**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Versión:** 1.0.0  

---

## 🎯 Objetivo

Implementar la conexión real a la base de datos SQLite de Kursor, reutilizando las entidades JPA del módulo `kursor-core` para mostrar datos reales en lugar de métricas simuladas.

---

## 📋 Fases de Implementación

### Fase 1: Configuración de Persistencia (Prioridad Alta)

#### 1.1 Configurar EntityManager
- [ ] Crear `PersistenceConfig.java` en `kursor-studio`
- [ ] Configurar conexión a SQLite usando las entidades de `kursor-core`
- [ ] Verificar que las entidades se cargan correctamente

#### 1.2 Crear Servicios de Base de Datos
- [ ] `DatabaseConnectionService.java` - Gestión de conexión
- [ ] `EntityQueryService.java` - Consultas genéricas a entidades
- [ ] `DatabaseStatisticsService.java` - Estadísticas reales de BD

### Fase 2: Reemplazar Métricas Simuladas (Prioridad Alta)

#### 2.1 DatabaseInspectorService
- [ ] Reemplazar métricas simuladas con consultas reales
- [ ] Implementar conteo real de registros por tabla
- [ ] Mostrar información real del esquema

#### 2.2 Dashboard
- [ ] Conectar con estadísticas reales de usuarios
- [ ] Mostrar métricas reales de sesiones
- [ ] Implementar gráficos con datos reales

### Fase 3: Explorador de Base de Datos (Prioridad Media)

#### 3.1 Visualización de Tablas
- [ ] Listar tablas reales de la base de datos
- [ ] Mostrar estructura de cada tabla
- [ ] Implementar paginación para grandes volúmenes

#### 3.2 Consultas de Datos
- [ ] Permitir consultas SQL básicas
- [ ] Implementar filtros por entidad
- [ ] Mostrar resultados en formato tabla

---

## 🏗️ Arquitectura Propuesta

### Estructura de Servicios

```
kursor-studio/
├── service/
│   ├── DatabaseConnectionService.java     (NUEVO)
│   ├── EntityQueryService.java            (NUEVO)
│   ├── DatabaseStatisticsService.java     (NUEVO)
│   ├── DatabaseInspectorService.java      (MODIFICAR)
│   └── LogViewerService.java              (EXISTENTE)
├── config/
│   └── PersistenceConfig.java             (NUEVO)
└── model/
    └── DatabaseStatistics.java            (NUEVO)
```

### Dependencias Necesarias

```xml
<!-- Ya incluidas en pom.xml -->
<dependency>
    <groupId>com.kursor</groupId>
    <artifactId>kursor-core</artifactId>
    <version>${project.version}</version>
</dependency>
```

---

## 🔧 Implementación Detallada

### Paso 1: Configuración de Persistencia

#### 1.1 Crear PersistenceConfig.java

```java
@Configuration
public class PersistenceConfig {
    
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        // Configurar conexión a SQLite
        // Usar entidades de kursor-core
    }
    
    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory().createEntityManager();
    }
}
```

#### 1.2 Crear DatabaseConnectionService.java

```java
@Service
public class DatabaseConnectionService {
    
    private final EntityManager entityManager;
    
    public boolean isConnected() {
        // Verificar conexión
    }
    
    public List<String> getTableNames() {
        // Obtener nombres de tablas
    }
    
    public long getTableRecordCount(String tableName) {
        // Contar registros por tabla
    }
}
```

### Paso 2: Servicios de Consulta

#### 2.1 EntityQueryService.java

```java
@Service
public class EntityQueryService {
    
    public <T> List<T> findAll(Class<T> entityClass) {
        // Consulta genérica
    }
    
    public <T> T findById(Class<T> entityClass, Object id) {
        // Consulta por ID
    }
    
    public <T> List<T> findByQuery(String jpql) {
        // Consulta JPQL personalizada
    }
}
```

#### 2.2 DatabaseStatisticsService.java

```java
@Service
public class DatabaseStatisticsService {
    
    public DatabaseStatistics getStatistics() {
        // Estadísticas reales de la BD
    }
    
    public Map<String, Long> getTableCounts() {
        // Conteo por tabla
    }
    
    public List<Session> getRecentSessions() {
        // Sesiones recientes
    }
}
```

### Paso 3: Integración con UI

#### 3.1 Modificar DatabaseInspectorService

```java
@Service
public class DatabaseInspectorService {
    
    private final DatabaseConnectionService connectionService;
    private final DatabaseStatisticsService statisticsService;
    
    public DatabaseStatistics getRealStatistics() {
        // Reemplazar métricas simuladas
        return statisticsService.getStatistics();
    }
    
    public boolean testConnection() {
        return connectionService.isConnected();
    }
}
```

---

## 🧪 Testing

### Tests Unitarios

- [ ] `DatabaseConnectionServiceTest.java`
- [ ] `EntityQueryServiceTest.java`
- [ ] `DatabaseStatisticsServiceTest.java`
- [ ] Tests de integración con base de datos real

### Tests de Integración

- [ ] Verificar conexión a SQLite
- [ ] Validar consultas a entidades reales
- [ ] Comprobar estadísticas reales

---

## ⚠️ Riesgos y Consideraciones

### Riesgos Identificados

1. **Compatibilidad de entidades**: Las entidades de `kursor-core` pueden no ser compatibles
2. **Configuración de SQLite**: Puede requerir configuración específica
3. **Rendimiento**: Consultas a grandes volúmenes de datos
4. **Concurrencia**: Múltiples conexiones simultáneas

### Mitigaciones

1. **Testing exhaustivo**: Verificar compatibilidad antes de implementar
2. **Configuración incremental**: Implementar paso a paso
3. **Paginación**: Implementar para grandes volúmenes
4. **Pool de conexiones**: Usar HikariCP si es necesario

---

## 📅 Cronograma Estimado

| Tarea | Duración | Dependencias |
|-------|----------|--------------|
| Configuración de Persistencia | 2-3 horas | Ninguna |
| Servicios de Base de Datos | 3-4 horas | Persistencia |
| Reemplazo de Métricas | 2-3 horas | Servicios |
| Testing | 2-3 horas | Implementación |
| **Total** | **9-13 horas** | - |

---

## 🎯 Criterios de Éxito

- [ ] La aplicación se conecta a SQLite sin errores
- [ ] Se muestran estadísticas reales en lugar de simuladas
- [ ] El Log Viewer sigue funcionando correctamente
- [ ] Los tests pasan al 100%
- [ ] No hay degradación de rendimiento

---

## 🚀 Próximos Pasos

1. **Implementar PersistenceConfig.java**
2. **Crear DatabaseConnectionService.java**
3. **Implementar EntityQueryService.java**
4. **Crear DatabaseStatisticsService.java**
5. **Modificar DatabaseInspectorService.java**
6. **Testing exhaustivo**
7. **Documentación actualizada**

---

**Nota:** Este plan debe ejecutarse de forma incremental, verificando cada paso antes de continuar con el siguiente. 