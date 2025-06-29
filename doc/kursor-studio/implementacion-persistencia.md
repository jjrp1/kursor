# Implementación de Persistencia - Kursor Studio

**Fecha:** 28 de Junio de 2025  
**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Versión:** 3.0.0  

---

## 🎯 Resumen

Se ha implementado exitosamente la conexión real a la base de datos SQLite en Kursor Studio, reutilizando las entidades JPA del módulo `kursor-core` y configurando **Hibernate** como proveedor de persistencia unificado.

**⚠️ IMPORTANTE**: El sistema trabaja con **dos bases de datos separadas**:
- **kursor-studio**: Base de datos local para configuración y logs
- **kursor**: Base de datos objetivo que se quiere explorar

---

## 🚨 PROBLEMA IDENTIFICADO - Separación de Bases de Datos

### ❌ **Situación Actual (INCORRECTA)**

La implementación actual **mezcla completamente** las dos bases de datos:

1. **Una sola persistence-unit**: `kursorPU` apunta a `data/kursor.db` (la BD de kursor)
2. **Sin entidades propias**: No hay entidades para la configuración de kursor-studio
3. **Reutilización incorrecta**: Usa las entidades de kursor-core para TODO
4. **Sin separación de contextos**: Todo va a la misma BD

### 📋 **Análisis del Problema**

#### Configuración Actual (persistence.xml)
```xml
<persistence-unit name="kursorPU" transaction-type="RESOURCE_LOCAL">
    <!-- ❌ PROBLEMA: Solo una BD, solo entidades de kursor -->
    <class>com.kursor.persistence.entity.Sesion</class>
    <class>com.kursor.persistence.entity.EstadoEstrategia</class>
    <class>com.kursor.persistence.entity.EstadisticasUsuario</class>
    <class>com.kursor.persistence.entity.RespuestaPregunta</class>
    
    <properties>
        <!-- ❌ PROBLEMA: URL fija a la BD de kursor -->
        <property name="javax.persistence.jdbc.url" value="jdbc:sqlite:data/kursor.db"/>
    </properties>
</persistence-unit>
```

#### Problemas Específicos
1. **No hay BD para kursor-studio**: ¿Dónde guardamos `DatabaseConfiguration`?
2. **URL fija**: No podemos configurar la ubicación de la BD de kursor
3. **Entidades mezcladas**: Las entidades de kursor-core no son para configuración
4. **Sin separación**: Imposible distinguir entre configuración y datos a explorar

---

## ✅ SOLUCIÓN PROPUESTA - Arquitectura de Dos Bases de Datos

### 🏗️ **Arquitectura Correcta**

```
┌─────────────────────────────────────────────────────────────┐
│                    Kursor Studio                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   kursor-studio │    │           kursor                │ │
│  │  (Configuración)│    │      (Datos a Explorar)         │ │
│  │                 │    │                                 │ │
│  │ • DatabaseConfig│    │ • Sesion                        │ │
│  │ • Logs          │    │ • EstadisticasUsuario           │ │
│  │ • Settings      │    │ • EstadoEstrategia              │ │
│  │ • Cache         │    │ • RespuestaPregunta             │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 🔧 **Implementación Propuesta**

#### 1. Dos Persistence Units Separados

```xml
<!-- kursor-studio.db - Configuración local -->
<persistence-unit name="kursorStudioPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    
    <!-- Entidades propias de kursor-studio -->
    <class>com.kursor.studio.model.DatabaseConfiguration</class>
    <!-- Futuras entidades: LogEntry, Settings, etc. -->
    
    <properties>
        <property name="javax.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
        <property name="javax.persistence.jdbc.url" value="jdbc:sqlite:data/kursor-studio.db"/>
        
        <!-- Hibernate para kursor-studio -->
        <property name="hibernate.dialect" value="org.hibernate.community.dialect.SQLiteDialect"/>
        <property name="hibernate.hbm2ddl.auto" value="update"/>
    </properties>
</persistence-unit>

<!-- kursor.db - Datos a explorar (configuración dinámica) -->
<persistence-unit name="kursorPU" transaction-type="RESOURCE_LOCAL">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    
    <!-- Entidades de kursor-core para explorar -->
    <class>com.kursor.persistence.entity.Sesion</class>
    <class>com.kursor.persistence.entity.EstadoEstrategia</class>
    <class>com.kursor.persistence.entity.EstadisticasUsuario</class>
    <class>com.kursor.persistence.entity.RespuestaPregunta</class>
    
    <properties>
        <property name="javax.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
        <!-- URL dinámica - se configura desde DatabaseConfiguration -->
        <property name="javax.persistence.jdbc.url" value="jdbc:sqlite:${kursor.database.path}"/>
        
        <!-- Hibernate para kursor -->
        <property name="hibernate.dialect" value="org.hibernate.community.dialect.SQLiteDialect"/>
        <property name="hibernate.hbm2ddl.auto" value="validate"/>
    </properties>
</persistence-unit>
```

#### 2. Configuración Dinámica

```java
// DatabaseConfigurationService.java
public class DatabaseConfigurationService {
    
    private EntityManagerFactory kursorStudioEMF;
    private EntityManagerFactory kursorEMF;
    
    public void initializeKursorDatabase(String databasePath) {
        // Cerrar conexión anterior si existe
        if (kursorEMF != null) {
            kursorEMF.close();
        }
        
        // Crear nueva conexión con la BD de kursor
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", "jdbc:sqlite:" + databasePath);
        
        kursorEMF = Persistence.createEntityManagerFactory("kursorPU", properties);
    }
}
```

#### 3. Separación de Servicios

```java
// Servicios para kursor-studio (configuración)
@Service
public class DatabaseConfigurationService {
    @PersistenceContext(unitName = "kursorStudioPU")
    private EntityManager em;
    // ... gestión de configuración
}

// Servicios para kursor (exploración)
@Service  
public class DatabaseExplorerService {
    @PersistenceContext(unitName = "kursorPU")
    private EntityManager em;
    // ... exploración de datos
}
```

---

## 📋 PLAN DE IMPLEMENTACIÓN

### Fase 1: Separación de Persistence Units
- [ ] Crear `kursorStudioPU` para configuración local
- [ ] Modificar `kursorPU` para configuración dinámica
- [ ] Implementar `DatabaseConfiguration` entity
- [ ] Actualizar `PersistenceConfig` para manejar dos EMFs

### Fase 2: Configuración Dinámica
- [ ] Implementar `DatabaseConfigurationService`
- [ ] Crear diálogo de configuración de BD
- [ ] Integrar en flujo de inicio de aplicación
- [ ] Validación y testing de conexiones

### Fase 3: Separación de Servicios
- [ ] Refactorizar servicios existentes
- [ ] Separar contextos de persistencia
- [ ] Actualizar UI para usar servicios correctos
- [ ] Testing completo de separación

### Fase 4: Documentación y Testing
- [ ] Actualizar documentación técnica
- [ ] Crear tests unitarios
- [ ] Validación de funcionalidad completa
- [ ] Optimización y limpieza

---

## 🔄 MIGRACIÓN DE DATOS

### Consideraciones de Migración
1. **Datos existentes**: Si hay datos en la BD actual, necesitamos migrarlos
2. **Configuración**: Crear configuración inicial por defecto
3. **Backup**: Hacer backup antes de la migración
4. **Rollback**: Plan de rollback si algo falla

### Estrategia de Migración
```java
// 1. Detectar configuración existente
if (existingDatabaseExists()) {
    // 2. Crear configuración por defecto
    DatabaseConfiguration config = new DatabaseConfiguration();
    config.setKursorDatabasePath("data/kursor.db"); // Ruta por defecto
    config.setDescription("Migración automática");
    
    // 3. Guardar en nueva BD de kursor-studio
    saveConfiguration(config);
}
```

---

## ⚠️ RIESGOS Y MITIGACIONES

### Riesgos Identificados
1. **Pérdida de datos**: Migración incorrecta
2. **Configuración corrupta**: BD de kursor-studio corrupta
3. **Performance**: Dos conexiones simultáneas
4. **Complejidad**: Manejo de dos contextos JPA

### Mitigaciones
1. **Backup automático**: Antes de cualquier cambio
2. **Validación robusta**: Verificar integridad de ambas BD
3. **Lazy loading**: Conectar solo cuando sea necesario
4. **Logging detallado**: Para debugging de problemas

---

## 📊 MÉTRICAS DE ÉXITO

### Funcionalidad
- ✅ Separación completa de contextos de BD
- ✅ Configuración dinámica de BD de kursor
- ✅ Persistencia robusta de configuración
- ✅ Migración sin pérdida de datos

### Performance
- ⚡ Inicialización < 3 segundos
- ⚡ Cambio de BD < 2 segundos
- ⚡ Consultas sin degradación
- ⚡ Memoria optimizada

### Robustez
- 🛡️ Manejo de errores de conexión
- 🛡️ Validación de integridad
- 🛡️ Rollback automático
- 🛡️ Logging completo

---

## 🎯 ESTADO ACTUAL

**Estado:** 🔴 **REQUIERE CORRECCIÓN**  
**Problema:** Mezcla de contextos de BD  
**Solución:** Separación completa propuesta  
**Próximo paso:** Implementación de la corrección  

---

**Nota:** Esta corrección es crítica para el correcto funcionamiento del Explorador de Base de Datos y la separación de responsabilidades entre configuración y exploración de datos. 