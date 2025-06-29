# Script para probar la nueva arquitectura MVC mejorada de Kursor
# Autor: Juan José Ruiz Pérez <jjrp1@um.es>
# Versión: 1.0.0
# Fecha: 2025-01-27

param(
    [switch]$Compile,
    [switch]$Run,
    [switch]$Test,
    [switch]$Clean,
    [switch]$Help
)

# Configuración
$PROJECT_ROOT = Split-Path -Parent $PSScriptRoot
$CORE_MODULE = "kursor-core"
$MAIN_CLASS = "com.kursor.ui.presentation.KursorApplication"
$TARGET_DIR = "$PROJECT_ROOT\$CORE_MODULE\target"
$CLASSES_DIR = "$TARGET_DIR\classes"
$LIB_DIR = "$TARGET_DIR\lib"

# Colores para output
$Green = "Green"
$Yellow = "Yellow"
$Red = "Red"
$Cyan = "Cyan"

function Write-Header {
    param([string]$Message)
    Write-Host "`n" -NoNewline
    Write-Host "=" * 60 -ForegroundColor $Cyan
    Write-Host " $Message" -ForegroundColor $Cyan
    Write-Host "=" * 60 -ForegroundColor $Cyan
    Write-Host ""
}

function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor $Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠ $Message" -ForegroundColor $Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor $Red
}

function Write-Info {
    param([string]$Message)
    Write-Host "ℹ $Message" -ForegroundColor $Cyan
}

function Show-Help {
    Write-Header "Ayuda - Script de Prueba de Nueva Arquitectura"
    Write-Host "Este script permite probar la nueva arquitectura MVC mejorada de Kursor."
    Write-Host ""
    Write-Host "Parámetros disponibles:"
    Write-Host "  -Compile    Compila el proyecto"
    Write-Host "  -Run        Ejecuta la aplicación con la nueva arquitectura"
    Write-Host "  -Test       Ejecuta las pruebas unitarias"
    Write-Host "  -Clean      Limpia los archivos compilados"
    Write-Host "  -Help       Muestra esta ayuda"
    Write-Host ""
    Write-Host "Ejemplos de uso:"
    Write-Host "  .\test-nueva-arquitectura.ps1 -Compile"
    Write-Host "  .\test-nueva-arquitectura.ps1 -Run"
    Write-Host "  .\test-nueva-arquitectura.ps1 -Compile -Run"
    Write-Host "  .\test-nueva-arquitectura.ps1 -Clean"
    Write-Host ""
}

function Test-Prerequisites {
    Write-Header "Verificando Prerrequisitos"
    
    # Verificar Java
    try {
        $javaVersion = java -version 2>&1 | Select-String "version"
        if ($javaVersion) {
            Write-Success "Java encontrado: $javaVersion"
        } else {
            Write-Error "Java no encontrado"
            return $false
        }
    } catch {
        Write-Error "Error al verificar Java: $_"
        return $false
    }
    
    # Verificar Maven
    try {
        $mavenVersion = mvn -version 2>&1 | Select-String "Apache Maven"
        if ($mavenVersion) {
            Write-Success "Maven encontrado: $mavenVersion"
        } else {
            Write-Error "Maven no encontrado"
            return $false
        }
    } catch {
        Write-Error "Error al verificar Maven: $_"
        return $false
    }
    
    # Verificar estructura del proyecto
    if (Test-Path "$PROJECT_ROOT\$CORE_MODULE") {
        Write-Success "Estructura del proyecto verificada"
    } else {
        Write-Error "Estructura del proyecto no encontrada"
        return $false
    }
    
    return $true
}

function Compile-Project {
    Write-Header "Compilando Proyecto"
    
    try {
        Set-Location "$PROJECT_ROOT\$CORE_MODULE"
        
        Write-Info "Ejecutando Maven compile..."
        $result = mvn clean compile 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Proyecto compilado exitosamente"
            return $true
        } else {
            Write-Error "Error durante la compilación:"
            Write-Host $result -ForegroundColor $Red
            return $false
        }
    } catch {
        Write-Error "Error al compilar: $_"
        return $false
    } finally {
        Set-Location $PROJECT_ROOT
    }
}

function Test-Compilation {
    Write-Header "Verificando Compilación"
    
    # Verificar archivos compilados
    $requiredFiles = @(
        "$CLASSES_DIR\com\kursor\ui\presentation\KursorApplication.class",
        "$CLASSES_DIR\com\kursor\ui\presentation\main\MainController.class",
        "$CLASSES_DIR\com\kursor\ui\presentation\main\MainViewModel.class",
        "$CLASSES_DIR\com\kursor\ui\presentation\main\MainView.class",
        "$CLASSES_DIR\com\kursor\ui\application\usecases\LoadCoursesUseCase.class",
        "$CLASSES_DIR\com\kursor\ui\application\services\CourseService.class",
        "$CLASSES_DIR\com\kursor\ui\infrastructure\persistence\CourseRepository.class",
        "$CLASSES_DIR\com\kursor\ui\infrastructure\persistence\CourseRepositoryImpl.class"
    )
    
    $missingFiles = @()
    foreach ($file in $requiredFiles) {
        if (Test-Path $file) {
            Write-Success "Archivo encontrado: $(Split-Path $file -Leaf)"
        } else {
            Write-Warning "Archivo faltante: $(Split-Path $file -Leaf)"
            $missingFiles += $file
        }
    }
    
    if ($missingFiles.Count -eq 0) {
        Write-Success "Todos los archivos de la nueva arquitectura están compilados"
        return $true
    } else {
        Write-Error "Faltan $($missingFiles.Count) archivos compilados"
        return $false
    }
}

function Run-Application {
    Write-Header "Ejecutando Aplicación con Nueva Arquitectura"
    
    try {
        Set-Location "$PROJECT_ROOT\$CORE_MODULE"
        
        # Verificar que la aplicación esté compilada
        if (-not (Test-Path "$CLASSES_DIR\com\kursor\ui\presentation\KursorApplication.class")) {
            Write-Error "La aplicación no está compilada. Ejecute -Compile primero."
            return $false
        }
        
        Write-Info "Ejecutando aplicación con nueva arquitectura..."
        Write-Info "Clase principal: $MAIN_CLASS"
        
        # Construir classpath
        $classpath = "$CLASSES_DIR"
        if (Test-Path $LIB_DIR) {
            $libs = Get-ChildItem "$LIB_DIR\*.jar" | ForEach-Object { $_.FullName }
            $classpath += ";" + ($libs -join ";")
        }
        
        # Ejecutar aplicación
        $result = java -cp $classpath $MAIN_CLASS 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Aplicación ejecutada exitosamente"
            return $true
        } else {
            Write-Error "Error durante la ejecución:"
            Write-Host $result -ForegroundColor $Red
            return $false
        }
    } catch {
        Write-Error "Error al ejecutar la aplicación: $_"
        return $false
    } finally {
        Set-Location $PROJECT_ROOT
    }
}

function Run-Tests {
    Write-Header "Ejecutando Pruebas Unitarias"
    
    try {
        Set-Location "$PROJECT_ROOT\$CORE_MODULE"
        
        Write-Info "Ejecutando pruebas con Maven..."
        $result = mvn test 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Pruebas ejecutadas exitosamente"
            return $true
        } else {
            Write-Error "Error durante las pruebas:"
            Write-Host $result -ForegroundColor $Red
            return $false
        }
    } catch {
        Write-Error "Error al ejecutar las pruebas: $_"
        return $false
    } finally {
        Set-Location $PROJECT_ROOT
    }
}

function Clean-Project {
    Write-Header "Limpiando Proyecto"
    
    try {
        Set-Location "$PROJECT_ROOT\$CORE_MODULE"
        
        Write-Info "Ejecutando Maven clean..."
        $result = mvn clean 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Proyecto limpiado exitosamente"
            return $true
        } else {
            Write-Error "Error durante la limpieza:"
            Write-Host $result -ForegroundColor $Red
            return $false
        }
    } catch {
        Write-Error "Error al limpiar: $_"
        return $false
    } finally {
        Set-Location $PROJECT_ROOT
    }
}

function Show-Architecture-Info {
    Write-Header "Información de la Nueva Arquitectura"
    
    Write-Host "La nueva arquitectura implementa los siguientes patrones y principios:"
    Write-Host ""
    Write-Host "📁 Estructura de Paquetes:"
    Write-Host "   ├── presentation/     # Capa de presentación (Vistas, Controladores, ViewModels)"
    Write-Host "   ├── application/      # Capa de aplicación (Casos de uso, Servicios)"
    Write-Host "   └── infrastructure/   # Capa de infraestructura (Repositorios, Servicios externos)"
    Write-Host ""
    Write-Host "🎯 Patrones Implementados:"
    Write-Host "   ├── MVC (Model-View-Controller)"
    Write-Host "   ├── MVVM (Model-View-ViewModel)"
    Write-Host "   ├── Clean Architecture"
    Write-Host "   └── Repository Pattern"
    Write-Host ""
    Write-Host "🔧 Características:"
    Write-Host "   ├── Separación clara de responsabilidades"
    Write-Host "   ├── Binding automático con PropertyChangeSupport"
    Write-Host "   ├── Operaciones asíncronas"
    Write-Host "   ├── Manejo de errores robusto"
    Write-Host "   └── Logging detallado"
    Write-Host ""
}

# Función principal
function Main {
    Write-Header "Script de Prueba - Nueva Arquitectura MVC Kursor"
    
    # Mostrar ayuda si se solicita
    if ($Help) {
        Show-Help
        return
    }
    
    # Verificar prerrequisitos
    if (-not (Test-Prerequisites)) {
        Write-Error "Prerrequisitos no cumplidos. Verifique Java y Maven."
        exit 1
    }
    
    # Mostrar información de la arquitectura
    Show-Architecture-Info
    
    # Ejecutar acciones según parámetros
    $success = $true
    
    if ($Clean) {
        $success = Clean-Project
    }
    
    if ($Compile) {
        $success = Compile-Project -and $success
        if ($success) {
            $success = Test-Compilation -and $success
        }
    }
    
    if ($Test) {
        $success = Run-Tests -and $success
    }
    
    if ($Run) {
        $success = Run-Application -and $success
    }
    
    # Si no se especificaron parámetros, mostrar ayuda
    if (-not ($Compile -or $Run -or $Test -or $Clean -or $Help)) {
        Show-Help
        return
    }
    
    # Mostrar resultado final
    Write-Header "Resultado Final"
    if ($success) {
        Write-Success "Todas las operaciones completadas exitosamente"
        Write-Host ""
        Write-Info "La nueva arquitectura está lista para usar."
        Write-Info "Para más información, consulte la documentación."
    } else {
        Write-Error "Algunas operaciones fallaron"
        Write-Host ""
        Write-Info "Revise los errores anteriores y corrija los problemas."
        Write-Info "Para ayuda, ejecute: .\test-nueva-arquitectura.ps1 -Help"
        exit 1
    }
}

# Ejecutar función principal
Main 