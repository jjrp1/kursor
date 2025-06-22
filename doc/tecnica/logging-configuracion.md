# Configuración del Sistema de Logging

El sistema de *logging* de ***Kursor*** utiliza **SLF4J + Logback**, el estándar de facto de la industria Java. Esta guía detalla todas las opciones de configuración disponibles, desde propiedades del sistema hasta configuración XML avanzada.

## ⚠️ IMPORTANTE: Configuración UTF-8 en Windows

**Problema**: En Windows PowerShell, los caracteres especiales (acentos, ñ, etc.) pueden mostrarse como caracteres extraños en los logs.

**Solución**: Configurar PowerShell para usar UTF-8 correctamente:

```powershell
# Configurar codificación UTF-8 en PowerShell
chcp 65001
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

## Arquitectura de Configuración

### Orden de Prioridad

El sistema busca la configuración en el siguiente orden:

1. **Configuración XML** (`logback.xml`) - Configuración declarativa
2. **Propiedades del Sistema Java** (`-D` flags) - Configuración dinámica
3. **Variables de Entorno** - Configuración por entorno
4. **Valores por Defecto** - Configuración hardcodeada

### Componentes de Configuración

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   logback.xml   │───▶│   Logback Core   │───▶│   Appenders     │
│                 │    │                  │    │                 │
│ - Properties    │    │ - Configuration  │    │ - Console       │
│ - Appenders     │    │ - Filters        │    │ - File          │
│ - Loggers       │    │ - Layouts        │    │ - Error         │
│ - Root          │    │ - Policies       │    │ - Debug         │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │ System Properties│
                       │                  │
                       │ - kursor.log.*   │
                       │ - kursor.dev.*   │
                       └──────────────────┘
```

## Configuración Final Optimizada

### Archivo logback.xml (Configuración Principal)

**Ubicación**: `kursor-ui/src/main/resources/logback.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Propiedades del sistema para configuración dinámica -->
    <property name="LOG_LEVEL" value="${kursor.log.level:-DEBUG}"/>
    <property name="LOG_FILE" value="${kursor.log.file:-kursor.log}"/>
    <property name="LOG_DIR" value="${kursor.log.dir:-log}"/>
    
    <!-- Configuración de patrones de formato -->
    <!-- Patrón para consola: [FECHA] [NIVEL] Clase.Método:Línea - Mensaje -->
    <property name="CONSOLE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1}.%method:%line - %msg%n"/>
    
    <!-- Patrón para archivo: [FECHA] [NIVEL] Clase.Método:Línea - Mensaje -->
    <property name="FILE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1}.%method:%line - %msg%n"/>
    
    <!-- Appender para consola con codificación UTF-8 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder charset="UTF-8">
            <pattern>${CONSOLE_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- Appender para archivo con rotación y codificación UTF-8 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/${LOG_FILE}</file>
        <encoder charset="UTF-8">
            <pattern>${FILE_PATTERN}</pattern>
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
        <encoder charset="UTF-8">
            <pattern>${FILE_PATTERN}</pattern>
        </encoder>
        
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/error.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>60</maxHistory>
            <totalSizeCap>500MB</totalSizeCap>
        </rollingPolicy>
        
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>
    
    <!-- Logger para la aplicación principal -->
    <logger name="com.kursor.ui.KursorApplication" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
    
    <!-- Configuración raíz -->
    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
</configuration>
```

### Configuración Maven (pom.xml)

**Ubicación**: `kursor-ui/pom.xml`

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>com.kursor.ui.KursorApplication</mainClass>
        <options>
            <option>--add-opens</option>
            <option>java.base/java.lang=ALL-UNNAMED</option>
            <option>-Dlogback.configurationFile=src/main/resources/logback.xml</option>
            <option>-Dfile.encoding=UTF-8</option>
            <option>-Dconsole.encoding=UTF-8</option>
            <option>-Duser.language=es</option>
            <option>-Duser.country=ES</option>
            <option>-Djava.util.logging.ConsoleHandler.encoding=UTF-8</option>
        </options>
        <jlinkZipName>kursor</jlinkZipName>
        <jlinkImageName>kursor</jlinkImageName>
        <launcher>launcher</launcher>
        <mainClass>com.kursor.ui.KursorApplication</mainClass>
    </configuration>
</plugin>
```

## Formato de Log Optimizado

### Patrón de Formato Final

```
[FECHA] [NIVEL] Clase.Método:Línea - Mensaje
```

### Ejemplo de Salida

```
[2025-06-19 23:23:00.355] [WARN ] c.k.u.KursorApplication.warning:176 - El directorio de módulos no existe: modules
[2025-06-19 23:23:00.389] [INFO ] c.k.u.KursorApplication.info:167 - Iniciando aplicación Kursor...
[2025-06-19 23:23:00.632] [DEBUG] c.k.u.KursorApplication.debug:158 - Creando CursoPreviewService - Directorio: C:\pds\ultima-version\cursos
```

### Características del Formato

- ✅ **Fecha entre corchetes**: `[2025-06-19 23:23:00.355]`
- ✅ **Nivel entre corchetes**: `[WARN]`, `[INFO]`, `[DEBUG]`
- ✅ **Sin thread innecesario**: Eliminado `[JavaFX Application Thread]`
- ✅ **Método y número de línea**: `warning:176`, `info:167`
- ✅ **Clase abreviada**: `c.k.u.KursorApplication` (muy legible)
- ✅ **Caracteres especiales**: UTF-8 correctamente configurado

## Métodos de Configuración

### 1. Configuración XML (Máxima Prioridad)

#### Archivo logback.xml
```xml
<configuration>
    <!-- Propiedades del sistema -->
    <property name="LOG_LEVEL" value="${kursor.log.level:-INFO}"/>
    <property name="LOG_FILE" value="${kursor.log.file:-kursor.log}"/>
    <property name="LOG_DIR" value="${kursor.log.dir:-log}"/>
    
    <!-- Appender para consola -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="ch.qos.logback.classic.boolex.JaninoEventEvaluator">
                <expression>return "true".equals(System.getProperty("kursor.dev.mode", "false"));</expression>
            </evaluator>
            <OnMatch>ACCEPT</OnMatch>
            <OnMismatch>DENY</OnMismatch>
        </filter>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Appender para archivo principal -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/${LOG_FILE}</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- Configuración raíz -->
    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### 2. Propiedades del Sistema Java (Segunda Prioridad)

#### Línea de Comandos
```bash
# Configuración completa
java -Dkursor.log.level=DEBUG \
     -Dkursor.log.file=myapp.log \
     -Dkursor.log.dir=logs \
     -Dkursor.dev.mode=true \
     -jar kursor-ui.jar

# Configuración mínima
java -Dkursor.log.level=DEBUG -jar kursor-ui.jar
```

#### Maven (pom.xml)
```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>com.kursor.ui.KursorApplication</mainClass>
        <options>
            <option>-Dkursor.log.level=DEBUG</option>
            <option>-Dkursor.log.file=dev.log</option>
            <option>-Dkursor.dev.mode=true</option>
            <option>-Dkursor.log.dir=logs</option>
        </options>
    </configuration>
</plugin>
```

#### Script de Inicio
```bash
#!/bin/bash
# start-kursor.sh

# Configuración por entorno
if [ "$ENVIRONMENT" = "development" ]; then
    LOG_LEVEL="DEBUG"
    LOG_FILE="dev.log"
    DEV_MODE="true"
else
    LOG_LEVEL="INFO"
    LOG_FILE="production.log"
    DEV_MODE="false"
fi

java -Dkursor.log.level=$LOG_LEVEL \
     -Dkursor.log.file=$LOG_FILE \
     -Dkursor.dev.mode=$DEV_MODE \
     -jar kursor-ui.jar
```

### 3. Variables de Entorno (Tercera Prioridad)

#### Windows (PowerShell)
```powershell
# Configurar variables
$env:KURSOR_LOG_LEVEL="DEBUG"
$env:KURSOR_LOG_FILE="dev.log"
$env:KURSOR_LOG_DIR="logs"
$env:KURSOR_DEV_MODE="true"

# Ejecutar aplicación
java -jar kursor-ui.jar
```

#### Windows (CMD)
```cmd
set KURSOR_LOG_LEVEL=DEBUG
set KURSOR_LOG_FILE=dev.log
set KURSOR_LOG_DIR=logs
set KURSOR_DEV_MODE=true
java -jar kursor-ui.jar
```

#### Linux/Mac (Bash)
```bash
# Configurar variables
export KURSOR_LOG_LEVEL=DEBUG
export KURSOR_LOG_FILE=dev.log
export KURSOR_LOG_DIR=logs
export KURSOR_DEV_MODE=true

# Ejecutar aplicación
java -jar kursor-ui.jar
```

#### Archivo .env (Linux/Mac)
```bash
# .env
KURSOR_LOG_LEVEL=DEBUG
KURSOR_LOG_FILE=dev.log
KURSOR_LOG_DIR=logs
KURSOR_DEV_MODE=true
```

```bash
# Cargar y ejecutar
source .env
java -jar kursor-ui.jar
```

### 4. Configuración en IDEs

#### IntelliJ IDEA
```
Run Configuration → VM Options:
-Dkursor.log.level=DEBUG -Dkursor.log.file=dev.log -Dkursor.dev.mode=true
```

#### Eclipse
```
Run Configuration → Arguments → VM Arguments:
-Dkursor.log.level=DEBUG -Dkursor.log.file=dev.log -Dkursor.dev.mode=true
```

## Propiedades de Configuración Disponibles

### Propiedades Principales

| Propiedad | Descripción | Valor por Defecto | Ejemplo |
|-----------|-------------|-------------------|---------|
| `kursor.log.level` | Nivel de logging | `INFO` | `DEBUG`, `INFO`, `WARN`, `ERROR` |
| `kursor.log.file` | Nombre del archivo de log | `kursor.log` | `myapp.log`, `dev.log` |
| `kursor.log.dir` | Directorio de logs | `log` | `logs`, `/var/log/kursor` |
| `kursor.dev.mode` | Modo desarrollo | `false` | `true`, `false` |

### Propiedades Avanzadas

| Propiedad | Descripción | Valor por Defecto | Ejemplo |
|-----------|-------------|-------------------|---------|
| `kursor.log.maxFileSize` | Tamaño máximo por archivo | `10MB` | `5MB`, `100MB` |
| `kursor.log.maxHistory` | Días de retención | `30` | `7`, `90` |
| `kursor.log.totalSizeCap` | Tamaño total máximo | `1GB` | `500MB`, `5GB` |
| `kursor.log.pattern` | Patrón de formato | Automático | Personalizado |

## Niveles de Logging

### Jerarquía de Niveles

```
ERROR (Más crítico)
  ↓
WARN
  ↓
INFO
  ↓
DEBUG (Menos crítico)
```

### Descripción de Niveles

| Nivel | Descripción | Uso Recomendado |
|-------|-------------|-----------------|
| `ERROR` | Errores críticos que impiden el funcionamiento | Errores de sistema, excepciones no manejadas |
| `WARN` | Advertencias que no impiden el funcionamiento | Configuraciones faltantes, recursos no encontrados |
| `INFO` | Información general del flujo de la aplicación | Inicio/fin de operaciones, estados importantes |
| `DEBUG` | Información detallada para desarrollo | Valores de variables, flujo de métodos |

### Configuración por Entorno

#### Desarrollo
```bash
# Nivel DEBUG para máximo detalle
-Dkursor.log.level=DEBUG
-Dkursor.dev.mode=true
```

#### Testing
```bash
# Nivel INFO para información relevante
-Dkursor.log.level=INFO
-Dkursor.dev.mode=false
```

#### Producción
```bash
# Nivel WARN para solo problemas
-Dkursor.log.level=WARN
-Dkursor.dev.mode=false
```

## Configuración de Appenders

### Console Appender

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder charset="UTF-8">
        <pattern>[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1}.%method:%line - %msg%n</pattern>
    </encoder>
</appender>
```

### File Appender

```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_DIR}/${LOG_FILE}</file>
    <encoder charset="UTF-8">
        <pattern>[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1}.%method:%line - %msg%n</pattern>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
        <totalSizeCap>1GB</totalSizeCap>
    </rollingPolicy>
</appender>
```

### Error Appender

```xml
<appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_DIR}/error.log</file>
    <encoder charset="UTF-8">
        <pattern>[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1}.%method:%line - %msg%n</pattern>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>${LOG_DIR}/error.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>60</maxHistory>
        <totalSizeCap>500MB</totalSizeCap>
    </rollingPolicy>
    <filter class="ch.qos.logback.classic.filter.LevelFilter">
        <level>ERROR</level>
        <onMatch>ACCEPT</onMatch>
        <onMismatch>DENY</onMismatch>
    </filter>
</appender>
```

## Patrones de Formato

### Patrones Disponibles

| Patrón | Descripción | Ejemplo |
|--------|-------------|---------|
| `%d{yyyy-MM-dd HH:mm:ss.SSS}` | Fecha y hora | `2025-06-19 23:23:00.355` |
| `%-5level` | Nivel de log (5 caracteres) | `WARN `, `INFO `, `DEBUG` |
| `%logger{1}` | Nombre de clase (1 nivel) | `c.k.u.KursorApplication` |
| `%method` | Nombre del método | `warning`, `info`, `debug` |
| `%line` | Número de línea | `176`, `167`, `158` |
| `%msg` | Mensaje de log | `El directorio de módulos no existe` |
| `%n` | Salto de línea | `\n` |

### Patrones Personalizados

#### Patrón Simple
```
%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

#### Patrón Detallado
```
[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1}.%method:%line - %msg%n
```

#### Patrón para Debug
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] %logger{1}.%method:%line - %msg%n
```

## Configuración de Filtros

### Filtro por Nivel

```xml
<filter class="ch.qos.logback.classic.filter.LevelFilter">
    <level>ERROR</level>
    <onMatch>ACCEPT</onMatch>
    <onMismatch>DENY</onMismatch>
</filter>
```

### Filtro por Evaluador

```xml
<filter class="ch.qos.logback.core.filter.EvaluatorFilter">
    <evaluator class="ch.qos.logback.classic.boolex.JaninoEventEvaluator">
        <expression>return "true".equals(System.getProperty("kursor.dev.mode", "false"));</expression>
    </evaluator>
    <OnMatch>ACCEPT</OnMatch>
    <OnMismatch>DENY</OnMismatch>
</filter>
```

## Configuración de Rotación

### Política de Rotación por Tamaño y Tiempo

```xml
<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
    <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
    <maxFileSize>10MB</maxFileSize>
    <maxHistory>30</maxHistory>
    <totalSizeCap>1GB</totalSizeCap>
</rollingPolicy>
```

### Parámetros de Rotación

| Parámetro | Descripción | Valor Recomendado |
|-----------|-------------|-------------------|
| `maxFileSize` | Tamaño máximo por archivo | `10MB` |
| `maxHistory` | Días de retención | `30` |
| `totalSizeCap` | Tamaño total máximo | `1GB` |

## Configuración de Loggers Específicos

### Logger de Aplicación

```xml
<logger name="com.kursor.ui.KursorApplication" level="DEBUG" additivity="false">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</logger>
```

### Logger de Módulos

```xml
<logger name="com.kursor.core.util.ModuleManager" level="DEBUG" additivity="false">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</logger>
```

### Logger de Servicios

```xml
<logger name="com.kursor.core.service" level="INFO" additivity="false">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</logger>
```

## Configuración de Codificación

### UTF-8 en Windows

Para evitar problemas con caracteres especiales en Windows:

#### PowerShell
```powershell
# Configurar codificación UTF-8
chcp 65001
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

#### Java Options
```bash
-Dfile.encoding=UTF-8
-Dconsole.encoding=UTF-8
-Duser.language=es
-Duser.country=ES
-Djava.util.logging.ConsoleHandler.encoding=UTF-8
```

#### Maven Configuration
```xml
<options>
    <option>-Dfile.encoding=UTF-8</option>
    <option>-Dconsole.encoding=UTF-8</option>
    <option>-Duser.language=es</option>
    <option>-Duser.country=ES</option>
    <option>-Djava.util.logging.ConsoleHandler.encoding=UTF-8</option>
</options>
```

## Configuración de Performance

### Configuración Optimizada

```xml
<configuration>
    <!-- Configuración de performance -->
    <property name="LOG_LEVEL" value="${kursor.log.level:-INFO}"/>
    
    <!-- Appender asíncrono para mejor performance -->
    <appender name="ASYNC_CONSOLE" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="CONSOLE"/>
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
    </appender>
    
    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="FILE"/>
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
    </appender>
    
    <!-- Configuración raíz -->
    <root level="${LOG_LEVEL}">
        <appender-ref ref="ASYNC_CONSOLE"/>
        <appender-ref ref="ASYNC_FILE"/>
    </root>
</configuration>
```

### Parámetros de Performance

| Parámetro | Descripción | Valor Recomendado |
|-----------|-------------|-------------------|
| `queueSize` | Tamaño de cola asíncrona | `512` |
| `discardingThreshold` | Umbral de descarte | `0` (no descartar) |

## Troubleshooting

### Problemas Comunes

#### 1. Caracteres Extraños en Windows
**Síntoma**: `m├│dulos` en lugar de `módulos`

**Solución**:
```powershell
chcp 65001
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

#### 2. Logs No Aparecen
**Síntoma**: No se ven mensajes de log

**Solución**:
```bash
# Verificar nivel de log
-Dkursor.log.level=DEBUG

# Verificar configuración
-Dlogback.configurationFile=src/main/resources/logback.xml
```

#### 3. Thread Innecesario en Logs
**Síntoma**: `[JavaFX Application Thread]` aparece en logs

**Solución**: Usar patrón sin thread:
```
[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{1}.%method:%line - %msg%n
```

#### 4. Archivos de Log No Se Crean
**Síntoma**: No se generan archivos de log

**Solución**:
```bash
# Verificar directorio
-Dkursor.log.dir=log

# Verificar permisos
mkdir log
chmod 755 log
```

### Comandos de Diagnóstico

#### Verificar Configuración
```bash
# Verificar propiedades del sistema
java -Dkursor.log.level=DEBUG -Dkursor.log.file=test.log -jar kursor-ui.jar
```

#### Verificar Archivos de Log
```bash
# Listar archivos de log
ls -la log/

# Ver contenido del log
tail -f log/kursor.log
```

#### Verificar Codificación
```bash
# Verificar codificación de archivos
file -i log/kursor.log

# Verificar configuración de Java
java -Dfile.encoding=UTF-8 -jar kursor-ui.jar
```

## Conclusiones y Mejores Prácticas

### ✅ Configuración Final Optimizada

1. **Formato de Log**: `[FECHA] [NIVEL] Clase.Método:Línea - Mensaje`
2. **Codificación UTF-8**: Configurada correctamente para Windows
3. **Nivel DEBUG**: Para desarrollo con información detallada
4. **Rotación automática**: Archivos comprimidos y limpieza automática
5. **Separación de errores**: Archivo específico para errores críticos

### 🎯 Beneficios Obtenidos

- ✅ **Legibilidad**: Logs completamente legibles con caracteres especiales
- ✅ **Información detallada**: Método y línea para debugging
- ✅ **Performance**: Configuración optimizada sin impactar rendimiento
- ✅ **Mantenibilidad**: Rotación automática y limpieza de archivos
- ✅ **Flexibilidad**: Configuración dinámica por propiedades del sistema

### 📋 Checklist de Configuración

- [ ] Archivo `logback.xml` configurado con UTF-8
- [ ] Maven `pom.xml` con opciones de codificación
- [ ] PowerShell configurado para UTF-8
- [ ] Nivel de log configurado (DEBUG para desarrollo)
- [ ] Patrón de formato optimizado
- [ ] Appenders configurados (Console, File, Error)
- [ ] Rotación automática configurada
- [ ] Filtros aplicados según necesidades

### 🔧 Configuración Recomendada por Entorno

#### Desarrollo
```bash
-Dkursor.log.level=DEBUG
-Dkursor.dev.mode=true
-Dfile.encoding=UTF-8
```

#### Testing
```bash
-Dkursor.log.level=INFO
-Dkursor.dev.mode=false
-Dfile.encoding=UTF-8
```

#### Producción
```bash
-Dkursor.log.level=WARN
-Dkursor.dev.mode=false
-Dfile.encoding=UTF-8
```

Esta configuración garantiza que el sistema de logging de Kursor sea robusto, legible y eficiente en todos los entornos de ejecución.

---

**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Fecha:** 19/06/2025  
**Versión:** 1.0 