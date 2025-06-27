# Sistema de Logging - Documentación Completa

**Sistema de logging centralizado con SLF4J + Logback para la aplicación Kursor**

---

## 📋 Índice

1. [🎯 Resumen General](#-resumen-general)
2. [⚙️ Configuración del Sistema](#-configuración-del-sistema)
3. [🚀 Comandos Prácticos](#-comandos-prácticos)
4. [🔧 Resolución de Problemas](#-resolución-de-problemas)
5. [🧪 Testing y Validación](#-testing-y-validación)
6. [📊 Monitorización](#-monitorización)

---

## 🎯 Resumen General

### Tecnologías
- **SLF4J (Simple Logging Facade for Java)** - API de logging
- **Logback Classic** - Implementación del sistema de logging
- **Maven** - Gestión de dependencias y ejecución

### Niveles de Logging
- **DEBUG** - Información de depuración detallada
- **INFO** - Información general de operaciones
- **WARN** - Advertencias que no impiden el funcionamiento
- **ERROR** - Errores que requieren atención

### Ubicaciones de Logs
- **Consola** - Salida estándar con colores UTF-8
- **Archivo** - `kursor-core/log/kursor.log` (rotación automática)

---

## ⚙️ Configuración del Sistema

### 1. Dependencias Maven (`kursor-core/pom.xml`)

```xml
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
```

### 2. Plugin JavaFX Maven ⚠️ **CRÍTICO**

```xml
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>com.kursor.ui.KursorApplication</mainClass>
        <!-- ✅ CONFIGURACIÓN ESENCIAL para propiedades del sistema -->
        <systemProperties>
            <systemProperty>
                <key>kursor.log.level</key>
                <value>${kursor.log.level}</value>
            </systemProperty>
            <systemProperty>
                <key>kursor.log.file</key>
                <value>${kursor.log.file}</value>
            </systemProperty>
            <systemProperty>
                <key>kursor.log.dir</key>
                <value>${kursor.log.dir}</value>
            </systemProperty>
        </systemProperties>
    </configuration>
</plugin>
```

**⚠️ IMPORTANTE:** Sin esta configuración, las propiedades `-Dkursor.log.level=NIVEL` desde la línea de comandos **NO funcionarán**.

### 3. Configuración Logback (`kursor-core/src/main/resources/logback.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Propiedades del sistema para configuración dinámica -->
    <property name="LOG_LEVEL" value="${kursor.log.level:-INFO}"/>
    <property name="LOG_FILE" value="${kursor.log.file:-kursor.log}"/>
    <property name="LOG_DIR" value="${kursor.log.dir:-log}"/>
    
    <!-- Patrones de formato optimizados -->
    <property name="CONSOLE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{0}.%method:%L - %msg%n"/>
    <property name="FILE_PATTERN" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{0}.%method:%L - %msg%n"/>
    
    <!-- Appender para consola con codificación UTF-8 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${CONSOLE_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- Appender para archivo con rotación automática -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/${LOG_FILE}</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>300MB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- Logger raíz con nivel dinámico -->
    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
    
</configuration>
```

---

## 🚀 Comandos Prácticos

### Comandos Básicos

```bash
# Nivel DEBUG - Muestra TODO (DEBUG + INFO + WARN + ERROR)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=DEBUG"

# Nivel INFO - Muestra INFO + WARN + ERROR (Recomendado para uso normal)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=INFO"

# Nivel WARN - Muestra solo WARN + ERROR (Para troubleshooting)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=WARN"

# Nivel ERROR - Muestra solo ERROR (Para problemas críticos)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=ERROR"
```

### Comandos Avanzados

```bash
# Configuración personalizada de archivo de log
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=INFO" "-Dkursor.log.file=mi_aplicacion.log"

# Configuración personalizada del directorio de logs
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=DEBUG" "-Dkursor.log.dir=logs_debug"

# Configuración completa personalizada
mvn javafx:run -pl kursor-core -q \
  "-Dkursor.log.level=INFO" \
  "-Dkursor.log.file=kursor_session.log" \
  "-Dkursor.log.dir=sesiones"
```

### Comandos de Desarrollo

```bash
# Compilación y ejecución con logs DEBUG
mvn clean compile -pl kursor-core && mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=DEBUG"

# Ejecución silenciosa con logs mínimos
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=ERROR"

# Testing con logs activados
mvn test -pl kursor-core "-Dkursor.log.level=DEBUG"
```

---

## 🔧 Resolución de Problemas

### ❌ Problema Común: "Las propiedades `-D` son ignoradas"

#### 🔍 Síntomas
- El comando `mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=INFO"` ignora la propiedad
- Siempre se muestran mensajes DEBUG independientemente del nivel configurado
- Los logs no respetan el nivel especificado

#### 🎯 Causa Principal
El plugin `javafx-maven-plugin` **no está configurado** para pasar las propiedades del sistema (`-D`) a la aplicación JavaFX.

#### ✅ Solución Implementada

**1. Configuración del Plugin JavaFX** (Ya implementado en `kursor-core/pom.xml`)
```xml
<systemProperties>
    <systemProperty>
        <key>kursor.log.level</key>
        <value>${kursor.log.level}</value>
    </systemProperty>
</systemProperties>
```

**2. Configuración de Logback Simplificada** (Ya implementado)
- Eliminadas configuraciones complejas que causaban inconsistencias
- Logger raíz unificado que respeta las propiedades del sistema

**3. Corrección de Niveles en el Código** (Ya implementado)
- Mensajes que incorrectamente usaban `logger.error()` para éxitos ahora usan `logger.info()`

#### 📋 Verificación de la Solución

```bash
# Prueba nivel DEBUG (debe mostrar todos los mensajes)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=DEBUG" | grep -E "(DEBUG|INFO|WARN|ERROR)"

# Prueba nivel INFO (no debe mostrar DEBUG)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=INFO" | grep -E "(DEBUG|INFO|WARN|ERROR)"

# Prueba nivel WARN (solo WARN y ERROR)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=WARN" | grep -E "(DEBUG|INFO|WARN|ERROR)"

# Prueba nivel ERROR (solo ERROR)
mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=ERROR" | grep -E "(DEBUG|INFO|WARN|ERROR)"
```

### 🔧 Otros Problemas Comunes

#### Problema: "Archivos de log no se crean"
```bash
# Verificar permisos del directorio
ls -la kursor-core/log/

# Crear directorio si no existe
mkdir -p kursor-core/log
```

#### Problema: "Codificación incorrecta en logs"
- **Solución**: Verificar que `charset=UTF-8` esté configurado en logback.xml
- **Comando de prueba**: `file kursor-core/log/kursor.log` (debe mostrar UTF-8)

---

## 🧪 Testing y Validación

### Tests Unitarios de Logging

```bash
# Ejecutar tests del sistema de logging
mvn test -pl kursor-core -Dtest="*LoggingTest"

# Tests con diferentes niveles
mvn test -pl kursor-core "-Dkursor.log.level=DEBUG"
mvn test -pl kursor-core "-Dkursor.log.level=INFO"
```

### Configuración de Test (`kursor-core/src/test/resources/logback-test.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_LEVEL" value="${kursor.log.level:-INFO}"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>[TEST] [%d{HH:mm:ss.SSS}] [%-5level] %logger{0}.%method:%L - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

### Validación de Funcionamiento

```bash
# Script de validación completa
echo "=== Validación Sistema de Logging ==="

echo "1. Compilando proyecto..."
mvn clean compile -pl kursor-core -q

echo "2. Probando nivel DEBUG..."
timeout 10 mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=DEBUG" 2>&1 | head -5

echo "3. Probando nivel INFO..."
timeout 10 mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=INFO" 2>&1 | head -5

echo "4. Probando nivel WARN..."
timeout 10 mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=WARN" 2>&1 | head -5

echo "✅ Validación completada"
```

---

## 📊 Monitorización

### Estructura de Logs Generados

```
kursor-core/log/
├── kursor.log                    # Log actual
├── kursor.log.2025-06-27.0.gz   # Log rotado del día
├── kursor.log.2025-06-26.0.gz   # Logs anteriores
└── kursor.log.2025-06-25.0.gz
```

### Análisis de Logs

```bash
# Ver logs en tiempo real
tail -f kursor-core/log/kursor.log

# Filtrar por nivel
grep "ERROR" kursor-core/log/kursor.log
grep "WARN" kursor-core/log/kursor.log

# Estadísticas de logs
echo "Mensajes por nivel:"
grep -c "DEBUG" kursor-core/log/kursor.log
grep -c "INFO" kursor-core/log/kursor.log
grep -c "WARN" kursor-core/log/kursor.log
grep -c "ERROR" kursor-core/log/kursor.log

# Tamaño total de logs
du -sh kursor-core/log/
```

### Formato de Mensaje Estándar

```
[2025-06-27 13:00:05.992] [INFO ] Clase.metodo:línea - Mensaje descriptivo
│                        │       │                    │
│                        │       │                    └─ Mensaje claro y descriptivo
│                        │       └─ Clase, método y línea para debugging
│                        └─ Nivel de logging (DEBUG/INFO/WARN/ERROR)
└─ Timestamp con milisegundos
```

---

## 🏁 Resumen de Mejores Prácticas

### ✅ Comandos Recomendados

| Propósito | Comando |
|-----------|---------|
| **Desarrollo normal** | `mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=INFO"` |
| **Debugging detallado** | `mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=DEBUG"` |
| **Troubleshooting** | `mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=WARN"` |
| **Producción** | `mvn javafx:run -pl kursor-core -q "-Dkursor.log.level=ERROR"` |

### ✅ Lecciones Aprendidas

1. **Plugin JavaFX Maven**: Configurar `<systemProperties>` es **esencial** para que funcionen las propiedades `-D`
2. **Logback**: Mantener configuración simple evita inconsistencias
3. **Niveles de logging**: Usar correctamente `logger.info()` vs `logger.error()` según el tipo de mensaje
4. **Testing**: Validar todos los niveles de logging durante desarrollo

---

**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Fecha:** 27 de junio de 2025  
**Versión:** 2.0 (Documentación Unificada)  
**Estado:** ✅ Problema de propiedades del sistema resuelto completamente 