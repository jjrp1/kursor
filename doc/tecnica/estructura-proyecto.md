# Estructura del Proyecto Kursor

## 🚨 Problema Actual: Estructura Confusa

La estructura actual del proyecto presenta varias complicaciones:

### ❌ **Problemas Identificados:**

1. **Módulos duplicados**: JARs en `modules/` y también en carpetas individuales
2. **JARs dispersos**: Ejecutables en múltiples `target/` folders
3. **Configuración fragmentada**: Archivos de configuración en múltiples lugares
4. **Scripts complejos**: Deployment manual y propenso a errores
5. **Rutas confusas**: Diferentes rutas para desarrollo vs producción

### 📁 **Estructura Actual (Problemática):**

```
ultima-version/
├── kursor-core/                    # Módulo core
│   ├── target/
│   │   └── kursor-core-1.0.0.jar
│   └── src/
├── kursor-ui/                      # Módulo UI
│   ├── target/
│   │   └── kursor-ui-1.0.0.jar
│   └── src/
├── kursor-fillblanks-module/       # Módulo individual
│   ├── target/
│   │   └── kursor-fillblanks-module-1.0.0.jar
│   └── src/
├── kursor-flashcard-module/        # Módulo individual
│   ├── target/
│   │   └── kursor-flashcard-module-1.0.0.jar
│   └── src/
├── kursor-multiplechoice-module/   # Módulo individual
│   ├── target/
│   │   └── kursor-multiplechoice-module-1.0.0.jar
│   └── src/
├── kursor-truefalse-module/        # Módulo individual
│   ├── target/
│   │   └── kursor-truefalse-module-1.0.0.jar
│   └── src/
├── modules/                        # ❌ DUPLICADO: Módulos compilados
│   ├── kursor-fillblanks-module-1.0.0.jar
│   ├── kursor-flashcard-module-1.0.0.jar
│   ├── kursor-multiplechoice-module-1.0.0.jar
│   └── kursor-truefalse-module-1.0.0.jar
├── cursos/                         # Cursos YAML
├── log/                           # Logs
├── doc/                           # Documentación
└── scripts/                       # Scripts de deployment
```

## ✅ **Solución Propuesta: Estructura Organizada**

### 🎯 **Objetivos:**

1. **Separación clara**: Desarrollo vs Distribución
2. **Unificación de módulos**: Una sola ubicación para JARs
3. **Configuración centralizada**: Archivos de configuración en un lugar
4. **Scripts simplificados**: Deployment automático y confiable
5. **Rutas consistentes**: Misma estructura en desarrollo y producción

### 📁 **Estructura Propuesta (Organizada):**

```
ultima-version/
├── src/                           # 🔧 DESARROLLO
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/kursor/core/
│   │   │   ├── com/kursor/ui/
│   │   │   └── com/kursor/modules/
│   │   └── resources/
│   └── test/
├── target/                        # 🏗️ COMPILACIÓN
│   ├── classes/
│   ├── test-classes/
│   └── generated-sources/
├── dist/                          # 📦 DISTRIBUCIÓN
│   ├── kursor.jar                # JAR principal
│   ├── modules/                  # Módulos de preguntas
│   │   ├── kursor-fillblanks-module.jar
│   │   ├── kursor-flashcard-module.jar
│   │   ├── kursor-multiplechoice-module.jar
│   │   └── kursor-truefalse-module.jar
│   ├── lib/                      # Dependencias externas
│   │   ├── javafx-controls.jar
│   │   ├── javafx-fxml.jar
│   │   └── javafx-graphics.jar
│   ├── config/                   # Configuración
│   │   ├── logback.xml
│   │   └── application.properties
│   ├── cursos/                   # Cursos YAML
│   │   ├── curso_ingles/
│   │   ├── curso_ingles_b2/
│   │   └── flashcards_ingles/
│   ├── log/                      # Logs
│   ├── doc/                      # Documentación
│   └── scripts/                  # Scripts de ejecución
│       ├── run.bat
│       ├── run.sh
│       └── install-java.bat
├── build/                        # 🛠️ HERRAMIENTAS DE BUILD
│   ├── scripts/
│   │   ├── build-all.ps1
│   │   ├── create-distribution.ps1
│   │   └── clean-all.ps1
│   └── config/
│       ├── deployment-config.json
│       └── build-config.json
└── doc/                          # 📚 DOCUMENTACIÓN
    ├── tecnica/
    ├── usuario/
    └── api/
```

## 🔧 **Implementación de la Solución**

### **Paso 1: Crear Estructura de Distribución**

```powershell
# Crear directorios de distribución
New-Item -ItemType Directory -Force -Path "dist"
New-Item -ItemType Directory -Force -Path "dist/modules", "dist/lib", "dist/config", "dist/cursos", "dist/log", "dist/doc", "dist/scripts"

# Crear directorios de build
New-Item -ItemType Directory -Force -Path "build/scripts", "build/config"
```

### **Paso 2: Scripts de Build Organizados**

#### **build-all.ps1** - Build Completo
```powershell
# Uso: .\build\scripts\build-all.ps1 [-Clean] [-SkipTests]
# Compila todos los módulos y crea la distribución organizada
```

**Características:**
- ✅ Compila todos los módulos automáticamente
- ✅ Organiza JARs en estructura clara
- ✅ Copia configuración y recursos
- ✅ Crea scripts de ejecución multiplataforma
- ✅ Genera README para la distribución

#### **clean-all.ps1** - Limpieza Completa
```powershell
# Uso: .\build\scripts\clean-all.ps1 [-KeepDist] [-KeepLogs]
# Elimina todos los archivos generados
```

**Características:**
- ✅ Limpia todos los directorios `target/`
- ✅ Elimina directorio `dist/` (opcional)
- ✅ Limpia logs (opcional)
- ✅ Elimina archivos temporales e IDE
- ✅ Elimina directorio `modules/` duplicado

#### **dev.ps1** - Desarrollo Simplificado
```powershell
# Uso: .\build\scripts\dev.ps1 [-Debug] [-SkipTests] [-Clean]
# Compila y ejecuta en modo desarrollo
```

**Características:**
- ✅ Compila todos los módulos
- ✅ Configura classpath automáticamente
- ✅ Ejecuta aplicación directamente
- ✅ Modo DEBUG opcional
- ✅ Configuración UTF-8 automática

## 🚀 **Uso de los Scripts**

### **Desarrollo Diario**
```powershell
# Ejecutar en modo desarrollo
.\build\scripts\dev.ps1

# Ejecutar con debug
.\build\scripts\dev.ps1 -Debug

# Limpiar y ejecutar
.\build\scripts\dev.ps1 -Clean -Debug
```

### **Crear Distribución**
```powershell
# Build completo
.\build\scripts\build-all.ps1

# Build sin tests
.\build\scripts\build-all.ps1 -SkipTests

# Build limpio
.\build\scripts\build-all.ps1 -Clean
```

### **Limpieza**
```powershell
# Limpieza completa
.\build\scripts\clean-all.ps1

# Mantener distribución
.\build\scripts\clean-all.ps1 -KeepDist

# Mantener logs
.\build\scripts\clean-all.ps1 -KeepLogs
```

## 📦 **Estructura de Distribución Final**

### **Contenido de `dist/`:**
```
dist/
├── kursor.jar                    # Aplicación principal
├── README.md                     # Instrucciones de uso
├── modules/                      # Módulos de preguntas
│   ├── kursor-fillblanks-module.jar
│   ├── kursor-flashcard-module.jar
│   ├── kursor-multiplechoice-module.jar
│   └── kursor-truefalse-module.jar
├── lib/                          # Dependencias externas
│   ├── javafx-controls-17.0.2-win.jar
│   ├── javafx-fxml-17.0.2-win.jar
│   └── javafx-graphics-17.0.2-win.jar
├── config/                       # Configuración
│   └── logback.xml
├── cursos/                       # Cursos disponibles
│   ├── curso_ingles/
│   ├── curso_ingles_b2/
│   └── flashcards_ingles/
├── log/                          # Logs (vacío inicialmente)
├── doc/                          # Documentación
└── scripts/                      # Scripts de ejecución
    ├── run.bat                   # Windows
    ├── run.sh                    # Linux/Mac
    └── install-java.bat          # Instalador Java Windows
```

## 🎯 **Beneficios de la Nueva Estructura**

### ✅ **Para Desarrolladores:**
- **Scripts simplificados**: Un comando para compilar y ejecutar
- **Estructura clara**: Separación entre desarrollo y distribución
- **Configuración centralizada**: Un solo lugar para configuraciones
- **Debugging mejorado**: Modo DEBUG integrado

### ✅ **Para Distribución:**
- **Estructura portable**: Todo organizado en `dist/`
- **Scripts multiplataforma**: Windows, Linux y Mac
- **Instalación automática**: Java se instala automáticamente
- **Documentación incluida**: README y docs en la distribución

### ✅ **Para Mantenimiento:**
- **Limpieza automática**: Script para limpiar todo
- **Build reproducible**: Mismo resultado siempre
- **Configuración versionada**: Archivos de configuración en control de versiones
- **Logs organizados**: Estructura clara para logs

## 🔄 **Migración de la Estructura Actual**

### **Paso 1: Backup**
```powershell
# Crear backup de la estructura actual
Copy-Item -Path "modules" -Destination "modules-backup" -Recurse
```

### **Paso 2: Limpiar Duplicados**
```powershell
# Eliminar directorio modules duplicado
Remove-Item -Path "modules" -Recurse -Force
```

### **Paso 3: Usar Nuevos Scripts**
```powershell
# Compilar y crear distribución
.\build\scripts\build-all.ps1 -Clean

# Ejecutar en desarrollo
.\build\scripts\dev.ps1 -Debug
```

## 📋 **Checklist de Implementación**

- [ ] ✅ Estructura `dist/` creada
- [ ] ✅ Estructura `build/` creada
- [ ] ✅ Script `build-all.ps1` implementado
- [ ] ✅ Script `clean-all.ps1` implementado
- [ ] ✅ Script `dev.ps1` implementado
- [ ] ✅ Scripts de ejecución creados
- [ ] ✅ README de distribución generado
- [ ] ✅ Configuración centralizada
- [ ] ✅ Documentación actualizada

## 🎉 **Resultado Final**

Con esta nueva estructura, el proyecto Kursor ahora tiene:

1. **Organización clara**: Separación entre desarrollo y distribución
2. **Scripts automatizados**: Build, limpieza y ejecución simplificados
3. **Configuración centralizada**: Un solo lugar para configuraciones
4. **Distribución portable**: Estructura lista para distribución
5. **Mantenimiento simplificado**: Limpieza y organización automática

**Comandos principales:**
- **Desarrollo**: `.\build\scripts\dev.ps1`
- **Build**: `.\build\scripts\build-all.ps1`
- **Limpieza**: `.\build\scripts\clean-all.ps1`
- **Ejecutar distribución**: `cd dist && scripts\run.bat`

Esta estructura resuelve completamente las complicaciones de ubicación de archivos y proporciona una base sólida para el desarrollo y distribución del proyecto.