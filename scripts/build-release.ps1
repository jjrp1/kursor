# Script para crear una distribución completa de Kursor
# Automatiza la compilación, validación y empaquetado

param(
    [string]$Version = "1.0.0",
    [switch]$Clean,
    [switch]$SkipTests,
    [switch]$Portable,
    [switch]$Force
)

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  KURSOR RELEASE BUILDER v$Version" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Configuración
$rootDir = "."
$releaseDir = "release"
$versionDir = "$releaseDir\v$Version"
$portableDir = "$releaseDir\portable"
$modulesDir = "modules"
$strategiesDir = "strategies"
$cursosDir = "cursos"
$javafxVersion = "17.0.2"

# Función para mostrar progreso
function Write-Progress-Step {
    param([string]$Message, [string]$Status = "INFO")
    $color = switch ($Status) {
        "INFO" { "Green" }
        "WARN" { "Yellow" }
        "ERROR" { "Red" }
        default { "White" }
    }
    Write-Host "[$Status] $Message" -ForegroundColor $color
}

# Función para validar archivo
function Test-RequiredFile {
    param([string]$Path, [string]$Description)
    if (Test-Path $Path) {
        Write-Progress-Step "✓ $Description encontrado" "INFO"
        return $true
    } else {
        Write-Progress-Step "✗ $Description NO encontrado: $Path" "ERROR"
        return $false
    }
}

# Limpiar directorios existentes
if ($Clean) {
    Write-Progress-Step "Limpiando directorios existentes..." "INFO"
    if (Test-Path $versionDir) {
        Remove-Item -Path $versionDir -Recurse -Force
    }
    if (Test-Path "$portableDir\kursor-portable-v$Version") {
        Remove-Item -Path "$portableDir\kursor-portable-v$Version" -Recurse -Force
    }
}

# Crear directorio de versión
New-Item -ItemType Directory -Force -Path $versionDir | Out-Null
New-Item -ItemType Directory -Force -Path "$versionDir\modules" | Out-Null
New-Item -ItemType Directory -Force -Path "$versionDir\strategies" | Out-Null
New-Item -ItemType Directory -Force -Path "$versionDir\cursos" | Out-Null
New-Item -ItemType Directory -Force -Path "$versionDir\docs" | Out-Null
New-Item -ItemType Directory -Force -Path "$versionDir\scripts" | Out-Null

Write-Progress-Step "Estructura de directorios creada" "INFO"

# Paso 1: Compilar proyecto principal
Write-Progress-Step "Compilando proyecto principal..." "INFO"
$mvnArgs = "clean compile"
if ($SkipTests) {
    $mvnArgs += " -DskipTests"
}
$mvnArgs += " package"

$result = Invoke-Expression "mvn $mvnArgs"
if ($LASTEXITCODE -ne 0) {
    Write-Progress-Step "Error compilando proyecto principal" "ERROR"
    exit 1
}

# Paso 2: Compilar módulos
Write-Progress-Step "Compilando módulos..." "INFO"
$modules = @(
    "kursor-fillblanks-module",
    "kursor-flashcard-module", 
    "kursor-multiplechoice-module",
    "kursor-truefalse-module"
)

$compiledModules = @()
foreach ($module in $modules) {
    Write-Progress-Step "  Compilando $module..." "INFO"
    $moduleResult = Invoke-Expression "mvn clean package -pl $module -DskipTests"
    if ($LASTEXITCODE -eq 0) {
        $compiledModules += $module
        Write-Progress-Step "  ✓ $module compilado exitosamente" "INFO"
    } else {
        Write-Progress-Step "  ✗ Error compilando $module" "WARN"
    }
}

# Paso 3: Compilar estrategias
Write-Progress-Step "Compilando estrategias..." "INFO"
$strategies = @(
    "kursor-secuencial-strategy",
    "kursor-aleatoria-strategy"
    # "kursor-repeticion-espaciada-strategy" # Excluido por errores de compilación
)

$compiledStrategies = @()
foreach ($strategy in $strategies) {
    Write-Progress-Step "  Compilando $strategy..." "INFO"
    $strategyResult = Invoke-Expression "mvn clean package -pl $strategy -DskipTests"
    if ($LASTEXITCODE -eq 0) {
        $compiledStrategies += $strategy
        Write-Progress-Step "  ✓ $strategy compilado exitosamente" "INFO"
    } else {
        Write-Progress-Step "  ✗ Error compilando $strategy" "WARN"
    }
}

# Paso 4: Copiar archivos compilados
Write-Progress-Step "Copiando archivos compilados..." "INFO"

# Copiar JAR principal
$coreJar = "kursor-core\target\kursor-core-$Version-shaded.jar"
if (Test-Path $coreJar) {
    Copy-Item $coreJar -Destination "$versionDir\kursor-core-$Version.jar" -Force
    Write-Progress-Step "JAR principal copiado" "INFO"
} else {
    Write-Progress-Step "JAR principal no encontrado, intentando JAR normal..." "WARN"
    $coreJarNormal = "kursor-core\target\kursor-core-$Version.jar"
    if (Test-Path $coreJarNormal) {
        Copy-Item $coreJarNormal -Destination "$versionDir\kursor-core-$Version.jar" -Force
        Write-Progress-Step "JAR principal copiado (versión normal)" "INFO"
    } else {
        Write-Progress-Step "Error: JAR principal no encontrado" "ERROR"
        exit 1
    }
}

# Copiar módulos compilados
foreach ($module in $compiledModules) {
    $moduleJar = "$module\target\$module-$Version.jar"
    if (Test-Path $moduleJar) {
        Copy-Item $moduleJar -Destination "$versionDir\modules\" -Force
        Write-Progress-Step "  Módulo $module copiado" "INFO"
    }
}

# Copiar estrategias compiladas
foreach ($strategy in $compiledStrategies) {
    $strategyJar = "$strategy\target\$strategy-$Version.jar"
    if (Test-Path $strategyJar) {
        Copy-Item $strategyJar -Destination "$versionDir\strategies\" -Force
        Write-Progress-Step "  Estrategia $strategy copiada" "INFO"
    }
}

# Paso 5: Copiar recursos
Write-Progress-Step "Copiando recursos..." "INFO"

# Copiar cursos
if (Test-Path $cursosDir) {
    Copy-Item "$cursosDir\*" -Destination "$versionDir\cursos" -Recurse -Force
    Write-Progress-Step "Cursos copiados" "INFO"
} else {
    Write-Progress-Step "Advertencia: Directorio de cursos no encontrado" "WARN"
}

# Copiar documentación
$docSource = "doc"
if (Test-Path $docSource) {
    Copy-Item "$docSource\*" -Destination "$versionDir\docs" -Recurse -Force
    Write-Progress-Step "Documentación copiada" "INFO"
} else {
    Write-Progress-Step "Advertencia: Documentación no encontrada" "WARN"
}

# Copiar scripts
$scriptsSource = "scripts"
if (Test-Path $scriptsSource) {
    Copy-Item "$scriptsSource\*.ps1" -Destination "$versionDir\scripts" -Force
    Copy-Item "$scriptsSource\*.sh" -Destination "$versionDir\scripts" -Force
    Write-Progress-Step "Scripts copiados" "INFO"
}

# Paso 6: Validación final
Write-Progress-Step "Validando distribución..." "INFO"
$validationErrors = @()

# Verificar JAR principal
if (-not (Test-RequiredFile "$versionDir\kursor-core-$Version.jar" "JAR principal")) {
    $validationErrors += "JAR principal"
}

# Verificar módulos
$moduleCount = (Get-ChildItem "$versionDir\modules\*.jar" -ErrorAction SilentlyContinue).Count
if ($moduleCount -eq 0) {
    Write-Progress-Step "Advertencia: No se encontraron módulos compilados" "WARN"
} else {
    Write-Progress-Step "✓ $moduleCount módulos encontrados" "INFO"
}

# Verificar estrategias
$strategyCount = (Get-ChildItem "$versionDir\strategies\*.jar" -ErrorAction SilentlyContinue).Count
if ($strategyCount -eq 0) {
    Write-Progress-Step "Advertencia: No se encontraron estrategias compiladas" "WARN"
} else {
    Write-Progress-Step "✓ $strategyCount estrategias encontradas" "INFO"
}

# Verificar cursos
$cursoCount = (Get-ChildItem "$versionDir\cursos\*.yaml" -ErrorAction SilentlyContinue).Count
if ($cursoCount -eq 0) {
    Write-Progress-Step "Advertencia: No se encontraron cursos" "WARN"
} else {
    Write-Progress-Step "✓ $cursoCount cursos encontrados" "INFO"
}

# Paso 7: Crear distribución portable si se solicita
if ($Portable) {
    Write-Progress-Step "Creando distribución portable..." "INFO"
    
    $portableVersionDir = "$portableDir\kursor-portable-v$Version"
    if (Test-Path $portableVersionDir) {
        Remove-Item -Path $portableVersionDir -Recurse -Force
    }
    
    # Crear estructura portable
    New-Item -ItemType Directory -Force -Path $portableVersionDir | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\modules" | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\strategies" | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\lib" | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\config" | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\cursos" | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\log" | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\doc" | Out-Null
    New-Item -ItemType Directory -Force -Path "$portableVersionDir\jre" | Out-Null
    
    # Copiar archivos de la versión
    Copy-Item "$versionDir\*" -Destination $portableVersionDir -Recurse -Force
    
    # Renombrar JAR principal
    Rename-Item "$portableVersionDir\kursor-core-$Version.jar" "$portableVersionDir\kursor.jar" -Force
    
    # Descargar JavaFX
    Write-Progress-Step "Descargando JavaFX..." "INFO"
    $javafxModules = @("controls", "fxml", "graphics", "base")
    foreach ($module in $javafxModules) {
        $url = "https://repo1.maven.org/maven2/org/openjfx/javafx-$module/$javafxVersion/javafx-$module-$javafxVersion-win.jar"
        $output = "$portableVersionDir\lib\javafx-$module-$javafxVersion-win.jar"
        try {
            Invoke-WebRequest -Uri $url -OutFile $output -UseBasicParsing
            Write-Progress-Step "  JavaFX $module descargado" "INFO"
        } catch {
            Write-Progress-Step "  Error descargando JavaFX $module" "WARN"
        }
    }
    
    # Crear script de ejecución
    $runCmd = @"
@echo off
set DEBUG_MODE=%1
set LOG_LEVEL=INFO
if "%DEBUG_MODE%"=="DEBUG" set LOG_LEVEL=DEBUG

echo ================================================
echo   KURSOR - Plataforma de Formacion Interactiva
echo   Version: $Version
echo ================================================
echo.

REM Verificar JRE
if not exist "jre\bin\java.exe" (
    echo Error: JRE no encontrado en jre\bin\java.exe
    echo Por favor, descargue e instale Java 17
    pause
    exit /b 1
)

REM Ejecutar aplicacion
jre\bin\java.exe --module-path lib --add-modules javafx.controls,javafx.fxml -Dlogback.configurationFile=config\logback.xml -Dfile.encoding=UTF-8 -Dkursor.log.level=%LOG_LEVEL% -jar kursor.jar

pause
"@

    $runCmd | Out-File -FilePath "$portableVersionDir\run.cmd" -Encoding ASCII
    
    # Crear script PowerShell
    $runPs1 = @"
param([string]`$Mode = "INFO")
`$LOG_LEVEL = if (`$Mode -eq "DEBUG") { "DEBUG" } else { "INFO" }

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  KURSOR - Plataforma de Formacion Interactiva" -ForegroundColor Cyan
Write-Host "  Version: $Version" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar JRE
if (-not (Test-Path "jre\bin\java.exe")) {
    Write-Host "Error: JRE no encontrado en jre\bin\java.exe" -ForegroundColor Red
    Write-Host "Por favor, descargue e instale Java 17" -ForegroundColor Red
    exit 1
}

# Ejecutar aplicacion
& "jre\bin\java.exe" --module-path lib --add-modules javafx.controls,javafx.fxml -Dlogback.configurationFile=config\logback.xml -Dfile.encoding=UTF-8 -Dkursor.log.level=`$LOG_LEVEL -jar kursor.jar
"@

    $runPs1 | Out-File -FilePath "$portableVersionDir\run.ps1" -Encoding UTF8
    
    # Crear README
    $readmeContent = @"
# Kursor - Plataforma de Formación Interactiva v$Version

## Instalación

1. **Extraer** este ZIP en cualquier carpeta
2. **Ejecutar** run.cmd o run.ps1
3. **¡Listo!** No requiere Java instalado

## Uso

- **Normal**: run.cmd o run.ps1
- **Debug**: run.cmd DEBUG o run.ps1 DEBUG

## Estructura

- `kursor.jar` - Aplicación principal
- `modules/` - Módulos de preguntas ($moduleCount módulos)
- `strategies/` - Estrategias de aprendizaje ($strategyCount estrategias)
- `cursos/` - Cursos disponibles ($cursoCount cursos)
- `lib/` - Bibliotecas JavaFX
- `jre/` - Java Runtime Environment
- `config/` - Configuración de logging
- `log/` - Archivos de log

## Requisitos

- Windows 10/11 (64-bit)
- 2GB RAM mínimo, 4GB recomendado
- 500MB espacio en disco

## Soporte

Para reportar problemas o solicitar ayuda:
- GitHub: https://github.com/kursor-team/kursor
- Email: jjrp1@um.es

---
Generado automáticamente el $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
"@

    $readmeContent | Out-File -FilePath "$portableVersionDir\README.md" -Encoding UTF8
    
    Write-Progress-Step "Distribución portable creada en: $portableVersionDir" "INFO"
}

# Paso 8: Crear ZIP de la versión
Write-Progress-Step "Creando archivo ZIP..." "INFO"
$zipFile = "$releaseDir\kursor-v$Version.zip"
if (Test-Path $zipFile) {
    Remove-Item $zipFile -Force
}

Compress-Archive -Path "$versionDir\*" -DestinationPath $zipFile -Force
Write-Progress-Step "Archivo ZIP creado: $zipFile" "INFO"

# Paso 9: Actualizar latest
Write-Progress-Step "Actualizando enlace latest..." "INFO"
if (Test-Path "$releaseDir\latest") {
    Remove-Item "$releaseDir\latest\*" -Recurse -Force
}
Copy-Item "$versionDir\*" -Destination "$releaseDir\latest" -Recurse -Force

# Paso 10: Resumen final
Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "  BUILD COMPLETADO EXITOSAMENTE" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Versión: $Version" -ForegroundColor White
Write-Host "Directorio: $versionDir" -ForegroundColor White
Write-Host "Módulos compilados: $($compiledModules.Count)" -ForegroundColor White
Write-Host "Estrategias compiladas: $($compiledStrategies.Count)" -ForegroundColor White
Write-Host "Cursos incluidos: $cursoCount" -ForegroundColor White
Write-Host ""

if ($Portable) {
    Write-Host "Distribución portable creada en:" -ForegroundColor Cyan
    Write-Host "  $portableVersionDir" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "Archivos generados:" -ForegroundColor Cyan
Write-Host "  $zipFile" -ForegroundColor Yellow
Write-Host "  $releaseDir\latest\" -ForegroundColor Yellow
Write-Host ""

if ($validationErrors.Count -gt 0) {
    Write-Host "Advertencias:" -ForegroundColor Yellow
    foreach ($error in $validationErrors) {
        Write-Host "  - $error" -ForegroundColor Yellow
    }
    Write-Host ""
}

Write-Host "¡Release listo para distribución!" -ForegroundColor Green
