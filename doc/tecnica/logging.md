# Sistema de Logging - Documentación Técnica

## Resumen Ejecutivo

El sistema de logging de Kursor utiliza **SLF4J + Logback**, el estándar de facto de la industria Java. Esta implementación proporciona trazabilidad completa, debugging eficiente, configuración flexible y funcionalidades avanzadas como rotación automática de logs, múltiples appenders y filtros granulares.

## Arquitectura del Sistema

### Componentes Principales

1. [**SLF4J**](https://slf4j.org/): Interfaz estándar de logging utilizada directamente
2. [**Logback**](https://logback.qos.ch/): Implementación de [SLF4J](https://slf4j.org/) con funcionalidades avanzadas
3. **Configuración XML**: Archivo `logback.xml` para configuración declarativa
4. **Múltiples Appenders**: Consola, archivo principal, error

### Diagrama de Arquitectura

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Application   │───▶│   SLF4J API      │───▶│   Logback Core  │
│                 │    │                  │    │                 │
│ - ModuleManager │    │ - Standard       │    │ - Appenders     │
│ - CursoManager  │    │ - Direct Usage   │    │ - Filters       │
│ - UI Components │    │ - Real Caller    │    │ - Layouts       │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │   Output Files   │
                       │                  │
                       │ - kursor.log     │
                       │ - error.log      │
                       └──────────────────┘
```

## Especificaciones Técnicas

### Niveles de Logging

| Nivel | Descripción | Contexto | Uso Recomendado |
|-------|-------------|----------|-----------------|
| **DEBUG** | Información detallada para debugging | Configurable | Desarrollo, troubleshooting |
| **INFO** | Información general de la aplicación | Configurable | Monitoreo general |
| **WARN** | Situaciones que requieren atención | Configurable | Problemas potenciales |
| **ERROR** | Errores que afectan la funcionalidad | Configurable | Fallos críticos |

### Formato de Log

```
[TIMESTAMP] [LEVEL] CALLER_CLASS - MESSAGE
[STACK_TRACE] (opcional)
```

**Ejemplo:**
```
[2025-06-20 08:56:07.096] [DEBUG] com.kursor.util.ModuleManager - Cargando módulo: kursor-fillblanks-module-1.0.0.jar
[2025-06-20 08:56:07.452] [INFO ] c.kursor.service.CursoPreviewService - Cargado preview del curso: curso_ingles desde curso_ingles.yaml
[2025-06-20 08:56:07.488] [ERROR] com.kursor.util.ModuleManager - El JAR no contiene el archivo de servicio META-INF/services/com.kursor.core.PreguntaModule
```

### Archivos de Log Generados

| Archivo | Contenido | Rotación | Tamaño Máximo |
|---------|-----------|----------|---------------|
| `kursor.log` | Todos los logs | Automática | 10MB por archivo |
| `error.log` | Solo errores | Automática | 10MB por archivo |

## Implementación Técnica

### Uso de SLF4J Directo

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiClase {
    private static final Logger logger = LoggerFactory.getLogger(MiClase.class);
    
    public void miMetodo() {
        logger.info("Método iniciado correctamente");
        logger.debug("Información detallada de debugging");
        logger.warn("Situación que requiere atención");
        
        try {
            // código que puede fallar
        } catch (Exception e) {
            logger.error("Error en el proceso", e);
        }
    }
}
```

### Configuración Logback (logback.xml)

```xml
<configuration>
    <!-- Propiedades del sistema -->
    <property name="LOG_LEVEL" value="${kursor.log.level:-INFO}"/>
    <property name="LOG_FILE" value="${kursor.log.file:-kursor.log}"/>
    
    <!-- Appender para consola -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-5level] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Appender para archivo con rotación -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/${LOG_FILE}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
</configuration>
```

### Gestión de Rendimiento

1. **Logging Asíncrono**: Disponible mediante configuración de Logback
2. **Filtrado Eficiente**: Los logs se filtran antes del procesamiento
3. **Rotación Automática**: Por tamaño y tiempo
4. **Compresión**: Archivos antiguos se comprimen automáticamente

## Casos de Uso

### 1. Desarrollo y Debugging

**Configuración:**
```bash
java -Dkursor.log.level=DEBUG \
     -jar kursor-ui.jar
```

**Salida Esperada:**
```
[2025-06-20 08:56:07.096] [DEBUG] com.kursor.util.ModuleManager - Cargando módulo: kursor-fillblanks-module-1.0.0.jar
[2025-06-20 08:56:07.452] [INFO ] c.kursor.service.CursoPreviewService - Cargado preview del curso: curso_ingles desde curso_ingles.yaml
```

### 2. Producción

**Configuración:**
```bash
java -Dkursor.log.level=INFO \
     -jar kursor-ui.jar
```

**Salida Esperada:**
```
[2025-06-20 08:56:07.452] [INFO ] c.kursor.service.CursoPreviewService - Cargado preview del curso: curso_ingles desde curso_ingles.yaml
[2025-06-20 08:56:07.488] [WARN ] com.kursor.util.ModuleManager - El JAR no contiene implementaciones válidas de PreguntaModule
```

### 3. Troubleshooting Específico

**Configuración:**
```bash
java -Dkursor.log.level=WARN \
     -jar kursor-ui.jar
```

**Salida Esperada:**
```
[2025-06-20 08:56:07.488] [WARN ] com.kursor.util.ModuleManager - El JAR no contiene implementaciones válidas de PreguntaModule: kursor-fillblanks-module-1.0.0.jar
```

## Métricas y Monitoreo

### Indicadores Clave

1. **Tasa de Errores**: Número de logs ERROR por unidad de tiempo
2. **Tiempo de Respuesta**: Latencia en la generación de logs
3. **Tamaño de Archivo**: Crecimiento del archivo de log
4. **Rotación de Logs**: Frecuencia de rotación automática

### Ventajas de SLF4J Directo

1. **Precisión Total**: Los logs muestran la clase, método y línea real del llamador
2. **Estándar de la Industria**: SLF4J es el estándar de facto de Java
3. **Sin Overhead**: No hay wrapper intermedio que agregue latencia
4. **Debugging Perfecto**: Información exacta para debugging y troubleshooting
5. **Configuración Estándar**: Toda la documentación y herramientas de SLF4J disponibles

## Documentación Relacionada

- [Configuración de Logging](logging-configuracion.md)
- [Scripts de Configuración](../scripts/README.md)
- [Guía de Desarrollo](../README.md)

---

**Autor:** Juan José Ruiz Pérez <jjrp1@um.es>  
**Fecha:** 19/06/2025  
**Versión:** 1.0 