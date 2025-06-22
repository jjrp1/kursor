# Script para crear una distribución portable de Kursor
# Crea un ZIP completo con JRE incrustado y una estructura organizada

param(
    [string]$Version = "1.0.0",
    [switch]$Clean,
    [switch]$SkipTests
)

Write-Host "Creando distribucion portable de Kursor v$Version..." -ForegroundColor Green

# Configuracion
$rootDir = "."
$targetDir = "kursor-ui\target"
$modulesDir = "modules"
$cursosDir = "cursos"
$portableDir = "kursor-portable"
$releaseDir = "release"
$zipFile = "release\kursor-portable-v$Version.zip"
$javafxVersion = "17.0.2"

# Limpiar directorios existentes
if ($Clean) {
    Write-Host "Limpiando directorios existentes..." -ForegroundColor Yellow
    if (Test-Path $portableDir) {
        Remove-Item -Path $portableDir -Recurse -Force
    }
    if (Test-Path $zipFile) {
        Remove-Item -Path $zipFile -Force
    }
}

# Crear directorio portable
New-Item -ItemType Directory -Force -Path $portableDir
New-Item -ItemType Directory -Force -Path "$portableDir\modules"
New-Item -ItemType Directory -Force -Path "$portableDir\lib"
New-Item -ItemType Directory -Force -Path "$portableDir\config"
New-Item -ItemType Directory -Force -Path "$portableDir\cursos"
New-Item -ItemType Directory -Force -Path "$portableDir\log"
New-Item -ItemType Directory -Force -Path "$portableDir\doc"
New-Item -ItemType Directory -Force -Path "$portableDir\jre"

Write-Host "Estructura de directorios creada" -ForegroundColor Green

# Compilar proyecto si es necesario
if (-not (Test-Path "$targetDir\kursor-ui-$Version.jar")) {
    Write-Host "Compilando proyecto..." -ForegroundColor Cyan
    $mvnArgs = "clean compile"
    if ($SkipTests) {
        $mvnArgs += " -DskipTests"
    }
    $mvnArgs += " package"
    
    $result = Invoke-Expression "mvn $mvnArgs"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error compilando proyecto" -ForegroundColor Red
        exit 1
    }
}

# Copiar JAR principal
$uiJar = "$targetDir\kursor-ui-$Version.jar"
if (Test-Path $uiJar) {
    Copy-Item $uiJar -Destination "$portableDir\kursor.jar" -Force
    Write-Host "JAR principal copiado" -ForegroundColor Green
} else {
    Write-Host "Error: JAR principal no encontrado en $uiJar" -ForegroundColor Red
    exit 1
}

# Copiar modulos
if (Test-Path $modulesDir) {
    Copy-Item "$modulesDir\*.jar" -Destination "$portableDir\modules" -Force
    Write-Host "Modulos copiados" -ForegroundColor Green
} else {
    Write-Host "Advertencia: Directorio de modulos no encontrado" -ForegroundColor Yellow
}

# Copiar cursos
if (Test-Path $cursosDir) {
    Copy-Item "$cursosDir\*" -Destination "$portableDir\cursos" -Recurse -Force
    Write-Host "Cursos copiados" -ForegroundColor Green
} else {
    Write-Host "Advertencia: Directorio de cursos no encontrado" -ForegroundColor Yellow
}

# Copiar configuracion
$logbackSource = "kursor-ui\src\main\resources\logback.xml"
if (Test-Path $logbackSource) {
    Copy-Item $logbackSource -Destination "$portableDir\config\logback.xml" -Force
    Write-Host "Configuracion copiada" -ForegroundColor Green
}

# Copiar documentacion de usuario
$userDocSource = "doc\usuario"
if (Test-Path $userDocSource) {
    Copy-Item $userDocSource -Destination "$portableDir\doc" -Recurse -Force
    Write-Host "Documentacion de usuario copiada" -ForegroundColor Green
} else {
    Write-Host "Advertencia: Documentacion de usuario no encontrada" -ForegroundColor Yellow
}

# Descargar JavaFX
Write-Host "Descargando JavaFX..." -ForegroundColor Cyan
$javafxUrl = "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/$javafxVersion/javafx-controls-$javafxVersion-win.jar"
$javafxFxmlUrl = "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/$javafxVersion/javafx-fxml-$javafxVersion-win.jar"
$javafxGraphicsUrl = "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/$javafxVersion/javafx-graphics-$javafxVersion-win.jar"
$javafxBaseUrl = "https://repo1.maven.org/maven2/org/openjfx/javafx-base/$javafxVersion/javafx-base-$javafxVersion-win.jar"

try {
    Invoke-WebRequest -Uri $javafxUrl -OutFile "$portableDir\lib\javafx-controls-$javafxVersion-win.jar"
    Invoke-WebRequest -Uri $javafxFxmlUrl -OutFile "$portableDir\lib\javafx-fxml-$javafxVersion-win.jar"
    Invoke-WebRequest -Uri $javafxGraphicsUrl -OutFile "$portableDir\lib\javafx-graphics-$javafxVersion-win.jar"
    Invoke-WebRequest -Uri $javafxBaseUrl -OutFile "$portableDir\lib\javafx-base-$javafxVersion-win.jar"
    Write-Host "JavaFX descargado" -ForegroundColor Green
} catch {
    Write-Host "Error al descargar JavaFX: $_" -ForegroundColor Red
    exit 1
}

# Descargar JRE portable
Write-Host "Descargando JRE portable..." -ForegroundColor Cyan
$jreUrl = "https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip"
$jreZip = "jre-temp.zip"

try {
    Invoke-WebRequest -Uri $jreUrl -OutFile $jreZip
    Expand-Archive -Path $jreZip -DestinationPath "$portableDir" -Force
    Rename-Item "$portableDir\jdk-17.0.2" "$portableDir\jre"
    Remove-Item $jreZip
    Write-Host "JRE portable descargado" -ForegroundColor Green
} catch {
    Write-Host "Error al descargar JRE: $_" -ForegroundColor Red
    exit 1
}

# Crear script CMD
$runCmd = @"
@echo off
set DEBUG_MODE=%1
set LOG_LEVEL=INFO
if "%DEBUG_MODE%"=="DEBUG" set LOG_LEVEL=DEBUG

echo Iniciando Kursor...
echo.

REM Verificar JRE
if not exist "jre\bin\java.exe" (
    echo Error: JRE no encontrado en jre\bin\java.exe
    pause
    exit /b 1
)

REM Ejecutar aplicacion
jre\bin\java.exe --module-path lib --add-modules javafx.controls,javafx.fxml -Dlogback.configurationFile=config\logback.xml -Dfile.encoding=UTF-8 -Dkursor.log.level=%LOG_LEVEL% -jar kursor.jar

pause
"@

$runCmd | Out-File -FilePath "$portableDir\run.cmd" -Encoding ASCII

# Crear script PowerShell
$runPs1 = @"
param([string]`$Mode = "INFO")
`$LOG_LEVEL = if (`$Mode -eq "DEBUG") { "DEBUG" } else { "INFO" }

Write-Host "Iniciando Kursor..." -ForegroundColor Green
Write-Host ""

# Verificar JRE
if (-not (Test-Path "jre\bin\java.exe")) {
    Write-Host "Error: JRE no encontrado en jre\bin\java.exe" -ForegroundColor Red
    exit 1
}

# Ejecutar aplicacion
& "jre\bin\java.exe" --module-path lib --add-modules javafx.controls,javafx.fxml -Dlogback.configurationFile=config\logback.xml -Dfile.encoding=UTF-8 -Dkursor.log.level=`$LOG_LEVEL -jar kursor.jar
"@

$runPs1 | Out-File -FilePath "$portableDir\run.ps1" -Encoding UTF8

Write-Host "Scripts de ejecucion creados" -ForegroundColor Green

# Crear README
$readmeContent = @"
# Kursor - Plataforma de Formacion Interactiva v$Version

## Instalacion

1. **Extraer** este ZIP en cualquier carpeta
2. **Ejecutar** run.cmd o run.ps1
3. **¡Listo!** No requiere Java instalado

## Uso

- **Normal**: run.cmd o run.ps1
- **Debug**: run.cmd DEBUG o run.ps1 DEBUG

## Estructura

- kursor.jar - Aplicacion principal
- jre/ - Java Runtime Environment (portable)
- lib/ - Bibliotecas JavaFX
- modules/ - Tipos de preguntas
- cursos/ - Cursos disponibles
- config/ - Configuracion
- log/ - Archivos de log
- doc/ - Documentacion de usuario

## Soporte

- Manual: doc/manual-usuario.md
- FAQ: doc/faq.md
- Guia: doc/guia-inicio-rapido.md

---

Kursor v$Version - Plataforma de Formacion Interactiva
"@

$readmeContent | Out-File -FilePath "$portableDir\README.md" -Encoding UTF8

Write-Host "README creado" -ForegroundColor Green

# Comprimir en ZIP
Write-Host "Creando ZIP..." -ForegroundColor Cyan
if (-not (Test-Path $releaseDir)) {
    New-Item -ItemType Directory -Force -Path $releaseDir
}

Compress-Archive -Path $portableDir -DestinationPath $zipFile -Force
Remove-Item -Path $portableDir -Recurse -Force

# Mostrar resultado
$zipSize = (Get-Item $zipFile).Length / 1MB
Write-Host "ZIP portable creado: $zipFile" -ForegroundColor Green
Write-Host "Tamaño: $([math]::Round($zipSize, 2)) MB" -ForegroundColor Cyan

Write-Host "`nDistribucion completada exitosamente!" -ForegroundColor Green
Write-Host "Ubicacion: $zipFile" -ForegroundColor Yellow 