# Kursor Studio - Diseño y Arquitectura

**Versión:** 1.0.0  
**Fecha:** 28 de Junio de 2025  
**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>

## 1. Visión General

### 1.1 Propósito
**Kursor Studio** es una aplicación JavaFX independiente diseñada para inspeccionar, validar y verificar el sistema de persistencia de la aplicación principal Kursor. Permite a desarrolladores y administradores consultar datos almacenados, verificar integridad y monitorear el comportamiento del sistema.

### 1.2 Objetivos Principales
- **Validación de datos**: Verificar que Kursor registra información correctamente
- **Inspección de persistencia**: Consultar tablas, relaciones y esquemas de base de datos
- **Verificación de integridad**: Comprobar consistencia de datos entre tablas
- **Monitoreo operacional**: Supervisar estado del sistema de persistencia

### 1.3 Características Clave
- ✅ **Aplicación completamente separada** (no integrada con Kursor principal)
- ✅ **Orientación monousuario** (sin gestión de usuarios)
- ✅ **Enfoque sencillo y funcional** 
- ✅ **Reutilización de infraestructura** existente de persistencia

## 2. Arquitectura y Diseño

### 2.1 Arquitectura General
```
┌─────────────────────────────────────────┐
│            Kursor Studio                │
├─────────────────────────────────────────┤
│  UI Layer (JavaFX)                      │
│  ├── Dashboard Principal                │
│  ├── Database Explorer                  │
│  ├── Data Validation                    │
│  └── System Status                      │
├─────────────────────────────────────────┤
│  Service Layer                          │
│  ├── Database Inspector Service         │
│  ├── Data Validation Service            │
│  └── Statistics Service                 │
├─────────────────────────────────────────┤
│  Data Access Layer                      │
│  ├── Reutilización de Repositorios      │
│  ├── Entidades JPA existentes           │
│  └── Configuración Persistencia         │
└─────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────┐
│         Base de Datos SQLite            │
│  (Compartida con Kursor principal)      │
└─────────────────────────────────────────┘
```

### 2.2 Módulos Principales

#### Módulo 1: Explorador de la Base de Datos 
- **Vista de tablas**: Listar todas las tablas del sistema
- **Explorador de esquemas**: Mostrar estructura de tablas y relaciones
- **Consulta de datos**: Navegar registros con paginación
- **Búsqueda y filtros**: Filtrar datos por criterios específicos

#### Módulo 2: Cuadro de Mandos de Validación de Datos
- **Panel de estado**: Visión general del sistema
- **Validaciones automáticas**: Verificación de integridad de datos
- **Alertas y notificaciones**: Identificar problemas potenciales
- **Métricas clave**: Estadísticas de uso y rendimiento

#### Módulo 3: Monitorización del Sistema
- **Estado de conexión**: Verificar conectividad con base de datos
- **Logs del sistema**: Visualizar logs de persistencia y aplicación
- **Viewer de logs integrado**: Consultar logs de Kursor Studio en tiempo real
- **Métricas de rendimiento**: Tiempo de respuesta de consultas

## 3. Casos de Uso Principales

### 3.1 Caso de Uso Primario: Validación de Registro de Datos
**Actor**: Desarrollador/Administrador  
**Objetivo**: Verificar que Kursor registra datos correctamente  

**Flujo Principal**:
1. Usuario abre Kursor Studio
2. El "Cuadro de Mandos" muestra un resumen del estado del sistema
3. Usuario navega a sección "Sesiones Recientes"
4. Sistema muestra las últimas sesiones de aprendizaje registradas
5. Usuario inspecciona detalles de una sesión específica
6. Sistema muestra: respuestas, estrategias aplicadas, estadísticas
7. Usuario verifica que los datos son coherentes y completos

### 3.2 Inspección de Tablas y Relaciones
**Actor**: Desarrollador  
**Objetivo**: Explorar estructura y contenido de base de datos  

**Flujo Principal**:
1. Usuario accede a "Explorador de Base de Datos"
2. Sistema muestra lista de tablas disponibles
3. Usuario selecciona tabla (ej: "sesion")
4. Sistema muestra esquema de tabla y datos
5. Usuario puede filtrar, ordenar y paginar resultados
6. Usuario puede ver relaciones con otras tablas

## 4. Interfaz de Usuario

### 4.1 Pantalla Principal - Dashboard
```
┌─────────────────────────────────────────────────────────────┐
│ Kursor Studio                                    [Min][Max][X]│
├─────────────────────────────────────────────────────────────┤
│ [Dashboard] [Database Explorer] [Validation] [System Status] │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  📊 SISTEMA DE PERSISTENCIA - ESTADO GENERAL               │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ 🗃️ Sesiones  │ │ 📝 Respuestas│ │ 📈 Estrategias│         │
│  │    1,247    │ │    5,893    │ │      12      │         │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│                                                             │
│  🔍 SESIONES RECIENTES                                      │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ ID │ Curso        │ Estrategia   │ Fecha       │ Estado │ │
│  ├─────────────────────────────────────────────────────────┤ │
│  │ 123│ Matemáticas  │ Repetición   │ 2025-01-15  │ ✅     │ │
│  │ 122│ Inglés B2    │ Aleatoria    │ 2025-01-15  │ ⏸️     │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                             │
│  ⚠️ VALIDACIONES                                            │
│  ✅ Integridad de datos: OK                                 │
│  ✅ Conexión BD: Activa                                     │
│  ⚠️ Sesiones huérfanas: 2 encontradas                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Database Explorer
```
┌─────────────────────────────────────────────────────────────┐
│ Database Explorer                                           │
├─────────────────────────────────────────────────────────────┤
│ TABLAS                     │ CONTENIDO TABLA: sesion        │
│ ┌─────────────────────────┐ │ ┌─────────────────────────────┐ │
│ │ 📋 sesion          (1247)│ │ │ ID │ Curso │ Estrategia   │ │
│ │ 📝 respuesta_pregunta    │ │ ├─────────────────────────────┤ │
│ │ 📊 estadisticas_usuario  │ │ │ 1  │ Mat   │ aleatoria    │ │
│ │ 🎯 estado_estrategia     │ │ │ 2  │ Ing   │ secuencial   │ │
│ │ 🔄 estado_sesion         │ │ │ 3  │ Hist  │ rep_espaciada│ │
│ └─────────────────────────┘ │ └─────────────────────────────┘ │
│                             │                               │
│ ESQUEMA TABLA               │ [Filtrar] [Exportar] [Refresh] │
│ ┌─────────────────────────┐ │                               │
│ │ id: BIGINT (PK)         │ │ 📄 Mostrando 3 de 1,247       │
│ │ curso_id: VARCHAR(255)  │ │ [◀] Página 1 de 416 [▶]       │
│ │ estrategia: VARCHAR(100)│ │                               │
│ │ fecha_inicio: TIMESTAMP │ │                               │
│ │ estado: VARCHAR(50)     │ │                               │
│ └─────────────────────────┘ │                               │
└─────────────────────────────────────────────────────────────┘
```

## 5. Tecnologías y Dependencias

### 5.1 Stack Tecnológico
- **UI Framework**: JavaFX 17+
- **Persistencia**: JPA/Hibernate (reutilización existente)
- **Base de Datos**: SQLite (compartida con Kursor)
- **Build**: Maven (módulo independiente)
- **Java Version**: 17+

### 5.2 Dependencias Clave

**Reutilización mediante Dependencia Maven**:
- `kursor-core` como dependencia Maven (NO se modifica el código existente)
- Entidades JPA: `com.kursor.persistence.entity.*`
- Repositorios: `com.kursor.persistence.repository.*`
- Configuración: `com.kursor.persistence.config.*`

**Dependencias Propias**:
- JavaFX Controls y FXML
- SQLite JDBC Driver
- **SLF4J + Logback** (sistema de logging idéntico a Kursor)
- Apache Commons (utilidades)

**Arquitectura de Dependencias**:
```
kursor-core ──→ kursor-studio
(NO modificar)   (aplicación nueva)
```

### 5.3 Estructura de Proyecto
```
kursor-studio/
├── pom.xml
├── log/                              # 📝 Logs de Kursor Studio
│   ├── kursor-studio.log
│   └── error.log
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/kursor/studio/
│   │   │       ├── KursorStudioApplication.java
│   │   │       ├── controller/
│   │   │       │   ├── DashboardController.java
│   │   │       │   ├── DatabaseExplorerController.java
│   │   │       │   ├── ValidationController.java
│   │   │       │   └── LogViewerController.java
│   │   │       ├── service/
│   │   │       │   ├── DatabaseInspectorService.java
│   │   │       │   ├── DataValidationService.java
│   │   │       │   ├── SystemStatusService.java
│   │   │       │   └── LogViewerService.java
│   │   │       └── model/
│   │   │           ├── TableInfo.java
│   │   │           ├── ValidationResult.java
│   │   │           ├── SystemStatus.java
│   │   │           └── LogEntry.java
│   │   └── resources/
│   │       ├── logback.xml                # ⚙️ Configuración logging
│   │       ├── fxml/
│   │       │   ├── dashboard.fxml
│   │       │   ├── database-explorer.fxml
│   │       │   ├── validation.fxml
│   │       │   └── log-viewer.fxml
│   │       └── css/
│   │           └── kursor-studio.css
│   └── test/
│       └── resources/
│           └── logback-test.xml          # ⚙️ Config logging tests
└── target/
```

## 6. Plan de Implementación

### 6.1 Fase 1: Fundación (Semana 1)
- [ ] Crear módulo Maven `kursor-studio` independiente
- [ ] Configurar dependencia Maven hacia `kursor-core` (sin modificar kursor-core)
- [ ] Configurar dependencias (SLF4J + Logback incluido)
- [ ] Implementar sistema de logging idéntico a Kursor
- [ ] Configurar `logback.xml` y `pom.xml` con propiedades del sistema
- [ ] Implementar aplicación JavaFX básica con logging
- [ ] Importar y reutilizar entidades JPA existentes desde `kursor-core`

### 6.2 Fase 2: Database Explorer (Semana 2)
- [ ] Implementar servicio de inspección de base de datos
- [ ] Crear interfaz de exploración de tablas
- [ ] Desarrollar vista de esquemas y relaciones
- [ ] Implementar paginación y filtros básicos

### 6.3 Fase 3: Dashboard y Validaciones (Semana 3)
- [ ] Crear dashboard principal con métricas
- [ ] Implementar validaciones automáticas de integridad
- [ ] Desarrollar vista de sesiones recientes
- [ ] Añadir sistema de alertas básico

### 6.4 Fase 4: Pulido y Testing (Semana 4)
- [ ] Implementar **Log Viewer integrado** en la aplicación
- [ ] Mejorar UI/UX y diseño visual
- [ ] Implementar testing unitario (incluyendo tests de logging)
- [ ] Crear documentación de usuario
- [ ] Verificar configuración de logging con comandos `-D`
- [ ] Preparar build y distribución

## 7. Sistema de Logging

### 7.1 Configuración de Logging
Kursor Studio implementará un sistema de logging **idéntico** al de Kursor principal, basado en **SLF4J + Logback**.

#### Configuración logback.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Propiedades del sistema para configuración dinámica -->
    <property name="LOG_LEVEL" value="${kursor.studio.log.level:-INFO}"/>
    <property name="LOG_FILE" value="${kursor.studio.log.file:-kursor-studio.log}"/>
    <property name="LOG_DIR" value="${kursor.studio.log.dir:-log}"/>
    
    <!-- Configuración de patrones de formato -->
    <property name="CONSOLE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{0}.%method:%L - %msg%n"/>
    <property name="FILE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{0}.%method:%L - %msg%n"/>
    
    <!-- Appender para consola con codificación UTF-8 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${CONSOLE_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- Appender para archivo con rotación y codificación UTF-8 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/${LOG_FILE}</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        
        <!-- Política de rotación por tamaño y tiempo -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- Appender para errores críticos con codificación UTF-8 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/error.log</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/error.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>60</maxHistory>
            <totalSizeCap>500MB</totalSizeCap>
        </rollingPolicy>
        
        <!-- Solo mensajes WARN y ERROR van al archivo de errores -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>WARN</level>
        </filter>
    </appender>
    
    <!-- Configuración raíz unificada -->
    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
</configuration>
```

#### Configuración Maven pom.xml
```xml
<!-- Plugin JavaFX para ejecución -->
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>com.kursor.studio.KursorStudioApplication</mainClass>
        <systemProperties>
            <systemProperty>
                <key>kursor.studio.log.level</key>
                <value>${kursor.studio.log.level}</value>
            </systemProperty>
        </systemProperties>
    </configuration>
</plugin>
```

### 7.2 Comandos de Logging
```bash
# Ejecutar con logging DEBUG para desarrollo
mvn javafx:run -Dkursor.studio.log.level=DEBUG

# Ejecutar con logging INFO para producción
mvn javafx:run -Dkursor.studio.log.level=INFO

# Ejecutar con logging WARN para mínimo
mvn javafx:run -Dkursor.studio.log.level=WARN
```

### 7.3 Uso en el Código
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInspectorService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInspectorService.class);
    
    public void inspectTables() {
        logger.info("Iniciando inspección de tablas de base de datos");
        
        try {
            // lógica de inspección
            logger.debug("Tabla 'sesion' encontrada con {} registros", count);
        } catch (Exception e) {
            logger.error("Error durante inspección de tablas", e);
        }
    }
}
```

### 7.4 Visualización de Logs en la Aplicación
Kursor Studio incluirá un **Log Viewer integrado** que permitirá:
- Ver logs en tiempo real dentro de la aplicación
- Filtrar por nivel de logging (DEBUG, INFO, WARN, ERROR)
- Buscar texto específico en los logs
- Exportar logs filtrados

## 8. Consideraciones Técnicas

### 8.1 Reutilización de Código
- **Máxima reutilización** de entidades JPA, repositorios y configuración
- **Compartir base de datos SQLite** entre Kursor y Kursor Studio
- **Evitar duplicación** de lógica de persistencia

### 8.2 Gestión de Concurrencia
- **Acceso de solo lectura** principalmente desde Studio
- **Gestión de locks** para evitar conflictos con Kursor principal
- **Refresh automático** cuando se detecten cambios

### 8.3 Rendimiento
- **Paginación obligatoria** para tablas grandes
- **Consultas optimizadas** con índices apropiados
- **Cache local** para metadatos de esquema

## 9. Criterios de Éxito

### 9.1 Funcionales
- [x] Permite inspeccionar todas las tablas de persistencia
- [x] Muestra datos de sesiones y respuestas de manera comprensible
- [x] Valida integridad de datos automáticamente
- [x] Proporciona métricas útiles del sistema
- [x] Sistema de logging completo y configurable

### 9.2 No Funcionales
- [x] Interfaz intuitiva y fácil de usar
- [x] Tiempo de respuesta < 2 segundos para consultas típicas
- [x] Manejo robusto de errores
- [x] Documentación completa
- [x] Logs estructurados para debugging y monitoreo

## 10. Próximos Pasos

1. **Validar diseño** con stakeholders
2. **Crear prototipo** de interfaz principal
3. **Configurar entorno** de desarrollo
4. **Comenzar implementación** Fase 1

---

**Notas**: Este documento es un borrador inicial y será refinado durante el desarrollo. Las funcionalidades pueden ajustarse según feedback y necesidades emergentes. 