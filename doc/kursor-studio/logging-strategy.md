# Estrategia de Logging - Kursor Studio

**Documento técnico especializado en el sistema de logging para Kursor Studio**

---

## 🎯 Objetivo

Implementar un sistema de logging **idéntico** al de Kursor principal para garantizar consistencia, facilidad de mantenimiento y capacidad de debugging efectiva. El sistema será completamente independiente y no requiere modificar código existente de kursor-core.

## 📋 Comparación con Kursor Principal

### Sistema Base (Kursor)
- **Framework**: SLF4J + Logback
- **Propiedades**: `kursor.log.level`, `kursor.log.file`, `kursor.log.dir`
- **Archivos**: `kursor.log`, `error.log`
- **Rotación**: Automática por tamaño y tiempo

### Sistema Objetivo (Kursor Studio)
- **Framework**: SLF4J + Logback (idéntico)
- **Propiedades**: `kursor.studio.log.level`, `kursor.studio.log.file`, `kursor.studio.log.dir`
- **Archivos**: `kursor-studio.log`, `error.log`
- **Rotación**: Automática por tamaño y tiempo (configuración idéntica)

## ⚙️ Configuración Técnica

### Dependencias Maven (pom.xml)
```xml
<dependencies>
    <!-- Logging con SLF4J + Logback -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.14</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- Plugin JavaFX con configuración de logging -->
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>com.kursor.studio.KursorStudioApplication</mainClass>
                <!-- ⚠️ CRÍTICO: Propiedades del sistema -->
                <systemProperties>
                    <systemProperty>
                        <key>kursor.studio.log.level</key>
                        <value>${kursor.studio.log.level}</value>
                    </systemProperty>
                    <systemProperty>
                        <key>kursor.studio.log.file</key>
                        <value>${kursor.studio.log.file}</value>
                    </systemProperty>
                    <systemProperty>
                        <key>kursor.studio.log.dir</key>
                        <value>${kursor.studio.log.dir}</value>
                    </systemProperty>
                </systemProperties>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Configuración logback.xml
**Ubicación**: `kursor-studio/src/main/resources/logback.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Propiedades del sistema específicas para Kursor Studio -->
    <property name="LOG_LEVEL" value="${kursor.studio.log.level:-INFO}"/>
    <property name="LOG_FILE" value="${kursor.studio.log.file:-kursor-studio.log}"/>
    <property name="LOG_DIR" value="${kursor.studio.log.dir:-log}"/>
    
    <!-- Patrones de formato (idénticos a Kursor) -->
    <property name="CONSOLE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{0}.%method:%L - %msg%n"/>
    <property name="FILE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{0}.%method:%L - %msg%n"/>
    
    <!-- Appender para consola con codificación UTF-8 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${CONSOLE_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- Appender para archivo principal con rotación -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/${LOG_FILE}</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        
        <!-- Política de rotación idéntica a Kursor -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- Appender para errores críticos -->
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
        
        <!-- Solo mensajes WARN y ERROR -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>WARN</level>
        </filter>
    </appender>
    
    <!-- Configuración raíz -->
    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
</configuration>
```

### Configuración para Testing
**Ubicación**: `kursor-studio/src/test/resources/logback-test.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Configuración simplificada para tests -->
    <property name="LOG_LEVEL" value="${kursor.studio.log.level:-INFO}"/>
    <property name="TEST_LOG_DIR" value="target/test-logs"/>
    
    <!-- Patrón de formato consistente -->
    <property name="TEST_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1} - %msg%n"/>
    
    <!-- Appender para archivo de test -->
    <appender name="TEST_FILE" class="ch.qos.logback.core.FileAppender">
        <file>${TEST_LOG_DIR}/kursor-studio-test.log</file>
        <encoder charset="UTF-8">
            <pattern>${TEST_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- Configuración raíz para tests -->
    <root level="${LOG_LEVEL}">
        <appender-ref ref="TEST_FILE"/>
    </root>
    
</configuration>
```

## 🚀 Comandos de Ejecución

### Comandos Básicos
```bash
# Ejecutar con logging DEBUG (desarrollo)
mvn javafx:run -Dkursor.studio.log.level=DEBUG

# Ejecutar con logging INFO (producción)
mvn javafx:run -Dkursor.studio.log.level=INFO

# Ejecutar con logging WARN (mínimo)
mvn javafx:run -Dkursor.studio.log.level=WARN

# Configurar archivo y directorio personalizados
mvn javafx:run -Dkursor.studio.log.level=DEBUG \
               -Dkursor.studio.log.file=debug.log \
               -Dkursor.studio.log.dir=logs-debug
```

### Comandos de Verificación
```bash
# Verificar que los logs se están generando
tail -f log/kursor-studio.log

# Verificar logs de errores
tail -f log/error.log

# Contar mensajes por nivel
grep "\\[DEBUG\\]" log/kursor-studio.log | wc -l
grep "\\[INFO\\]" log/kursor-studio.log | wc -l
grep "\\[WARN\\]" log/kursor-studio.log | wc -l
grep "\\[ERROR\\]" log/kursor-studio.log | wc -l
```

## 💻 Implementación en Código

### Patrón Estándar
```java
package com.kursor.studio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInspectorService {
    
    // Logger estático para la clase
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInspectorService.class);
    
    public void inspectTables() {
        logger.info("🔍 Iniciando inspección de tablas de base de datos");
        
        try {
            // Simular carga de tablas
            List<String> tables = loadTables();
            logger.debug("📋 Tablas encontradas: {}", tables.size());
            
            for (String table : tables) {
                int recordCount = getRecordCount(table);
                logger.debug("📊 Tabla '{}' contiene {} registros", table, recordCount);
            }
            
            logger.info("✅ Inspección de tablas completada exitosamente");
            
        } catch (SQLException e) {
            logger.error("❌ Error durante inspección de tablas", e);
            throw new RuntimeException("Error en inspección de base de datos", e);
        }
    }
    
    private void validateTableIntegrity(String tableName) {
        logger.debug("🔧 Validando integridad de tabla: {}", tableName);
        
        // Lógica de validación
        boolean isValid = performValidation(tableName);
        
        if (isValid) {
            logger.info("✅ Tabla '{}' pasó validación de integridad", tableName);
        } else {
            logger.warn("⚠️ Tabla '{}' tiene problemas de integridad", tableName);
        }
    }
}
```

### Controladores JavaFX
```java
package com.kursor.studio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class DatabaseExplorerController {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExplorerController.class);
    
    @FXML
    private TableView<?> tablesView;
    
    @FXML
    private void initialize() {
        logger.debug("🎨 Inicializando controlador de Database Explorer");
        setupTableView();
        loadInitialData();
    }
    
    @FXML
    private void handleRefreshTables() {
        logger.info("🔄 Usuario solicitó refresh de tablas");
        
        try {
            refreshTableData();
            logger.info("✅ Refresh de tablas completado");
        } catch (Exception e) {
            logger.error("❌ Error durante refresh de tablas", e);
            showErrorDialog("Error al actualizar tablas", e.getMessage());
        }
    }
}
```

## 🧪 Testing del Sistema de Logging

### Test de Configuración
```java
package com.kursor.studio.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingConfigurationTest {
    
    @Test
    void testLoggerConfiguration() {
        Logger logger = LoggerFactory.getLogger(LoggingConfigurationTest.class);
        
        // Verificar que el logger no es null
        assertNotNull(logger);
        
        // Verificar que es una instancia de Logback
        assertTrue(logger instanceof ch.qos.logback.classic.Logger);
    }
    
    @Test
    void testLogLevels() {
        Logger logger = LoggerFactory.getLogger("TestLogger");
        
        // Probar todos los niveles
        logger.debug("Mensaje DEBUG de prueba");
        logger.info("Mensaje INFO de prueba");
        logger.warn("Mensaje WARN de prueba");
        logger.error("Mensaje ERROR de prueba");
        
        // El test pasa si no hay excepciones
        assertTrue(true);
    }
}
```

### Script de Verificación
```bash
#!/bin/bash
# verify-logging.sh

echo "🧪 Verificando sistema de logging de Kursor Studio..."

# Limpiar logs anteriores
rm -rf log target/test-logs

# Compilar el proyecto
echo "📦 Compilando proyecto..."
mvn clean compile -q

# Ejecutar brevemente para generar logs
echo "🚀 Ejecutando aplicación para generar logs..."
timeout 10s mvn javafx:run -Dkursor.studio.log.level=DEBUG > /dev/null 2>&1

# Verificar archivos de log
LOG_FILE="log/kursor-studio.log"
ERROR_FILE="log/error.log"

echo "📊 Resultados:"

if [ -f "$LOG_FILE" ]; then
    TOTAL_LOGS=$(wc -l < "$LOG_FILE")
    echo "  ✅ Archivo principal: $LOG_FILE ($TOTAL_LOGS líneas)"
else
    echo "  ❌ Archivo principal no encontrado: $LOG_FILE"
fi

if [ -f "$ERROR_FILE" ]; then
    ERROR_LOGS=$(wc -l < "$ERROR_FILE")
    echo "  ✅ Archivo de errores: $ERROR_FILE ($ERROR_LOGS líneas)"
else
    echo "  ℹ️ Archivo de errores: No se generaron errores"
fi

# Verificar formato de logs
if [ -f "$LOG_FILE" ]; then
    echo "📋 Últimos 3 logs generados:"
    tail -3 "$LOG_FILE"
fi

echo "🎯 Verificación completada"
```

## 📊 Diferencias con Kursor Principal

| Aspecto | Kursor | Kursor Studio |
|---------|--------|---------------|
| **Propiedades del sistema** | `kursor.log.*` | `kursor.studio.log.*` |
| **Archivo principal** | `kursor.log` | `kursor-studio.log` |
| **Archivo de test** | `test.log` | `kursor-studio-test.log` |
| **Clase principal** | `KursorApplication` | `KursorStudioApplication` |
| **Paquete base** | `com.kursor.ui` | `com.kursor.studio` |

## ✅ Checklist de Implementación

### Fase 1: Configuración Base
- [ ] Agregar dependencias SLF4J + Logback al `pom.xml`
- [ ] Configurar plugin JavaFX con propiedades del sistema
- [ ] Crear `logback.xml` en `src/main/resources`
- [ ] Crear `logback-test.xml` en `src/test/resources`

### Fase 2: Implementación en Código
- [ ] Agregar loggers a todas las clases principales
- [ ] Implementar mensajes de log informativos
- [ ] Agregar logs de debug para desarrollo
- [ ] Configurar logs de error con stack traces

### Fase 3: Testing y Verificación
- [ ] Crear tests unitarios para logging
- [ ] Verificar configuración con comandos `-D`
- [ ] Probar rotación de archivos
- [ ] Validar formato y codificación UTF-8

### Fase 4: Log Viewer Integrado
- [ ] Implementar `LogViewerController`
- [ ] Crear interfaz para visualizar logs
- [ ] Agregar filtros por nivel
- [ ] Implementar búsqueda en logs

## 🎯 Beneficios Esperados

1. **Consistencia**: Sistema idéntico a Kursor principal
2. **Debugging efectivo**: Logs detallados para resolución de problemas
3. **Monitoreo**: Capacidad de seguir operaciones en tiempo real
4. **Mantenimiento**: Fácil diagnóstico de issues
5. **Desarrollo**: Información detallada durante implementación

---

**Nota**: Este documento debe consultarse durante toda la implementación para garantizar que el sistema de logging cumple con los estándares establecidos en Kursor principal. 