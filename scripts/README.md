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
**Última actualización:** 2025-06-19  
**Migración:** SLF4J + Logback 