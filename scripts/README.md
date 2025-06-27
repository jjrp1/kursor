# Scripts de Configuración - Kursor

Este directorio contiene scripts para facilitar la configuración del sistema de logging en diferentes entornos. El sistema utiliza **SLF4J + Logback** como estándar de logging.

## 📁 Scripts Disponibles

### Linux/Mac (Bash)
- **`dev.sh`** - Configuración para desarrollo
- **`prod.sh`** - Configuración para producción  
- **`debug.sh`** - Configuración para debugging específico
- **`test.sh`** - Configuración para testing

### Windows (PowerShell)
- **`dev.ps1`** - Configuración para desarrollo

## 🚀 Uso Rápido

### Linux/Mac
```bash
# Dar permisos de ejecución
chmod +x scripts/*.sh

# Desarrollo
./scripts/dev.sh

# Producción
./scripts/prod.sh

# Debugging
./scripts/debug.sh

# Testing
./scripts/test.sh
```

### Windows
```powershell
# Desarrollo
.\scripts\dev.ps1

# O con cmd
scripts\dev.ps1
```

## ⚙️ Configuraciones por Script

### Desarrollo (`dev.sh`, `dev.ps1`)
```bash
KURSOR_LOG_LEVEL=DEBUG
KURSOR_LOG_FILE=dev.log
KURSOR_LOG_DIR=logs
KURSOR_DEV_MODE=true
```

### Producción (`prod.sh`)
```bash
KURSOR_LOG_LEVEL=INFO
KURSOR_LOG_FILE=production.log
KURSOR_LOG_DIR=/var/log/kursor
KURSOR_DEV_MODE=false
```

### Debugging (`debug.sh`)
```bash
# Variables de entorno + propiedades del sistema
KURSOR_LOG_LEVEL=DEBUG
KURSOR_LOG_FILE=debug.log
KURSOR_LOG_DIR=logs
KURSOR_DEV_MODE=true
KURSOR_LOG_STACKTRACE=true

# + Propiedades del sistema (máxima prioridad)
-Dkursor.log.level=DEBUG
-Dkursor.log.file=debug.log
-Dkursor.log.dir=logs
-Dkursor.dev.mode=true
-Dkursor.log.stacktrace=true
```

### Testing (`test.sh`)
```bash
KURSOR_LOG_LEVEL=WARN
KURSOR_LOG_FILE=test.log
KURSOR_LOG_DIR=logs
KURSOR_DEV_MODE=false
```

## 🔧 Personalización

Puedes modificar cualquier script para ajustar la configuración según tus necesidades:

```bash
# Ejemplo: Modificar nivel de log en dev.sh
export KURSOR_LOG_LEVEL=INFO  # Cambiar de DEBUG a INFO
```

## 📋 Verificación

Cada script muestra la configuración aplicada antes de ejecutar la aplicación:

```
🚀 Iniciando Kursor en modo desarrollo...
📋 Configuración aplicada:
   - Nivel de log: DEBUG
   - Archivo de log: dev.log
   - Directorio: logs
   - Modo desarrollo: true

▶️  Ejecutando aplicación...
```

## 🎯 Casos de Uso

### Desarrollo Diario
```bash
./scripts/dev.sh
```

### Testing de Funcionalidades
```bash
./scripts/test.sh
```

### Debugging de Problemas
```bash
./scripts/debug.sh
```

### Despliegue en Producción
```bash
./scripts/prod.sh
```

## 🔄 Orden de Prioridad

Los scripts utilizan el orden de prioridad del sistema SLF4J + Logback:

1. **Configuración XML** (`logback.xml`) - Configuración declarativa
2. **Propiedades del Sistema** (`-D` flags) - Configuración dinámica
3. **Variables de Entorno** - Configuración por entorno
4. **Valores por Defecto** - Configuración hardcodeada

## 📚 Documentación Relacionada

- [Sistema de Logging](../doc/tecnica/logging.md)
- [Configuración de Logging](../doc/tecnica/logging-configuracion.md)
- [Documentación Técnica](../doc/tecnica/)

## 🆕 Cambios en la Versión 3.0

### Migración a SLF4J + Logback

- **Sistema anterior**: `Wrapper KursorLogger` → **Actual**: `SLF4J directo`
- **Configuración**: Propiedades del sistema → **Nuevo**: XML + propiedades
- **Rotación**: Manual → **Nuevo**: Automática
- **Appenders**: 2 básicos → **Nuevo**: Múltiples avanzados

### Nuevas Propiedades

- `kursor.log.dir` - Directorio de logs
- Configuración XML en `logback.xml`
- Rotación automática por tamaño y tiempo
- Múltiples archivos de log (principal, error, debug)

### Compatibilidad

- **API**: Idéntica (solo cambia el import)
- **Propiedades**: Siguen funcionando
- **Scripts**: No requieren cambios
- **Variables de entorno**: Compatibles

---

**Versión:** 3.0.0  
**Última actualización:** 2025-06-27  
**Migración:** SLF4J + Logback 

# Scripts de Automatización - Kursor

Este directorio contiene scripts para automatizar el proceso de build, validación y distribución de Kursor.

## Scripts Disponibles

### 1. `check-status.ps1` - Validación Rápida
**Propósito**: Verificar rápidamente el estado de la configuración antes de ejecutar la aplicación.

**Uso**:
```powershell
.\scripts\check-status.ps1
```

**Qué verifica**:
- ✅ Módulos de preguntas disponibles
- ✅ Estrategias de aprendizaje disponibles  
- ✅ Cursos disponibles
- ✅ JAR principal compilado

**Salida**:
- Configuración completa (4/4 checks)
- Configuración parcial (2-3/4 checks)
- Configuración incompleta (0-1/4 checks)

### 2. `build-simple.ps1` - Build Simplificado
**Propósito**: Compilar y copiar módulos/estrategias a las carpetas de carga dinámica.

**Opciones**:
- `-Clean`: Limpiar directorios antes de copiar
- `-SkipTests`: Saltar pruebas durante compilación
- `-CopyOnly`: Solo copiar archivos ya compilados

**Uso**:
```powershell
# Build completo
.\scripts\build-simple.ps1 -Clean -SkipTests

# Solo copiar archivos existentes
.\scripts\build-simple.ps1 -CopyOnly
```

### 3. `build-release.ps1` - Build de Release Completo
**Propósito**: Crear una distribución completa para release.

**Opciones**:
- `-Version`: Versión a compilar (default: 1.0.0)
- `-Clean`: Limpiar directorios existentes
- `-SkipTests`: Saltar pruebas durante compilación
- `-Portable`: Crear distribución portable con JRE
- `-Force`: Forzar recompilación

**Uso**:
```powershell
# Release básico
.\scripts\build-release.ps1 -Version 1.0.0 -Clean

# Distribución portable completa
.\scripts\build-release.ps1 -Version 1.0.0 -Clean -Portable -SkipTests
```

## Flujo de Trabajo Recomendado

### Para Desarrollo Diario
1. **Verificar estado**: `.\scripts\check-status.ps1`
2. **Si hay problemas**: `.\scripts\build-simple.ps1 -Clean`
3. **Ejecutar aplicación**: `java -jar kursor-core\target\kursor-core-1.0.0.jar`

### Para Crear Release
1. **Validar configuración**: `.\scripts\check-status.ps1`
2. **Crear release**: `.\scripts\build-release.ps1 -Version X.X.X -Clean -Portable`
3. **Distribuir**: Usar archivos generados en `release/`

## Estructura de Directorios

```
scripts/
├── check-status.ps1      # Validación rápida
├── build-simple.ps1      # Build simplificado
├── build-release.ps1     # Build de release completo
└── README.md            # Esta documentación

release/
├── latest/              # Última versión
├── vX.X.X/              # Versión específica
├── portable/            # Distribuciones portables
└── config/              # Configuración de logging

modules/                 # Módulos de preguntas (carga dinámica)
strategies/              # Estrategias de aprendizaje (carga dinámica)
```

## Troubleshooting

### Error: "No se encontraron módulos"
```powershell
.\scripts\build-simple.ps1 -Clean -SkipTests
```

### Error: "No se encontraron estrategias"
```powershell
.\scripts\build-simple.ps1 -Clean -SkipTests
```

### Error: "JAR principal no encontrado"
```powershell
mvn clean package
```

### Error de compilación en módulos/estrategias
- Verificar dependencias en `pom.xml`
- Revisar logs de Maven
- Algunos módulos pueden tener errores de compilación

## Configuración

### Variables de Entorno
- `JAVA_HOME`: Debe apuntar a Java 17
- `MAVEN_HOME`: Debe apuntar a Maven 3.6+

### Requisitos
- PowerShell 5.0+
- Java 17
- Maven 3.6+
- Conexión a internet (para descargar JavaFX en distribución portable)

## Notas Importantes

1. **Codificación**: Los scripts usan UTF-8 sin BOM para evitar problemas de caracteres especiales
2. **Rutas**: Los scripts asumen que se ejecutan desde el directorio raíz del proyecto
3. **Permisos**: Algunos comandos pueden requerir permisos de administrador
4. **Logs**: Los scripts muestran progreso detallado con colores para facilitar el debugging

## Integración Continua

Los scripts están diseñados para ser utilizados en:
- GitHub Actions
- Jenkins
- Azure DevOps
- GitLab CI

Ejemplo para GitHub Actions:
```yaml
- name: Build Kursor
  run: |
    .\scripts\build-release.ps1 -Version ${{ github.ref_name }} -Clean -Portable
```

---

**Última actualización**: $(Get-Date -Format "yyyy-MM-dd") 