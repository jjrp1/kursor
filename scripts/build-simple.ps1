# Script de build simplificado para desarrollo rápido
# Compila y copia módulos/estrategias a las carpetas de carga dinámica

param(
    [switch]$Clean,
    [switch]$SkipTests,
    [switch]$CopyOnly
)

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  KURSOR BUILD SIMPLIFICADO" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Configuración
$modulesDir = "modules"
$strategiesDir = "strategies"
$version = "1.0.0"

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

# Limpiar directorios si se solicita
if ($Clean) {
    Write-Progress-Step "Limpiando directorios..." "INFO"
    if (Test-Path $modulesDir) {
        Remove-Item "$modulesDir\*.jar" -Force
    }
    if (Test-Path $strategiesDir) {
        Remove-Item "$strategiesDir\*.jar" -Force
    }
}

# Crear directorios si no existen
if (-not (Test-Path $modulesDir)) {
    New-Item -ItemType Directory -Force -Path $modulesDir | Out-Null
    Write-Progress-Step "Directorio modules/ creado" "INFO"
}

if (-not (Test-Path $strategiesDir)) {
    New-Item -ItemType Directory -Force -Path $strategiesDir | Out-Null
    Write-Progress-Step "Directorio strategies/ creado" "INFO"
}

# Compilar si no es solo copia
if (-not $CopyOnly) {
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
    
    # Compilar módulos
    Write-Progress-Step "Compilando módulos..." "INFO"
    $modules = @(
        "kursor-fillblanks-module",
        "kursor-flashcard-module", 
        "kursor-multiplechoice-module",
        "kursor-truefalse-module"
    )
    
    foreach ($module in $modules) {
        Write-Progress-Step "  Compilando $module..." "INFO"
        $moduleResult = Invoke-Expression "mvn clean package -pl $module -DskipTests"
        if ($LASTEXITCODE -eq 0) {
            Write-Progress-Step "  ✓ $module compilado" "INFO"
        } else {
            Write-Progress-Step "  ✗ Error en $module" "WARN"
        }
    }
    
    # Compilar estrategias
    Write-Progress-Step "Compilando estrategias..." "INFO"
    $strategies = @(
        "kursor-secuencial-strategy",
        "kursor-aleatoria-strategy"
    )
    
    foreach ($strategy in $strategies) {
        Write-Progress-Step "  Compilando $strategy..." "INFO"
        $strategyResult = Invoke-Expression "mvn clean package -pl $strategy -DskipTests"
        if ($LASTEXITCODE -eq 0) {
            Write-Progress-Step "  ✓ $strategy compilado" "INFO"
        } else {
            Write-Progress-Step "  ✗ Error en $strategy" "WARN"
        }
    }
}

# Copiar JARs a directorios de carga dinámica
Write-Progress-Step "Copiando JARs..." "INFO"

# Copiar módulos
$moduleCount = 0
foreach ($module in $modules) {
    $moduleJar = "$module\target\$module-$version.jar"
    if (Test-Path $moduleJar) {
        Copy-Item $moduleJar -Destination "$modulesDir\" -Force
        $moduleCount++
        Write-Progress-Step "  Módulo $module copiado" "INFO"
    }
}

# Copiar estrategias
$strategyCount = 0
foreach ($strategy in $strategies) {
    $strategyJar = "$strategy\target\$strategy-$version.jar"
    if (Test-Path $strategyJar) {
        Copy-Item $strategyJar -Destination "$strategiesDir\" -Force
        $strategyCount++
        Write-Progress-Step "  Estrategia $strategy copiada" "INFO"
    }
}

# Resumen final
Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "  BUILD COMPLETADO" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Módulos copiados: $moduleCount" -ForegroundColor White
Write-Host "Estrategias copiadas: $strategyCount" -ForegroundColor White
Write-Host ""
Write-Host "Directorio modules/: $((Get-ChildItem $modulesDir -ErrorAction SilentlyContinue).Count) archivos" -ForegroundColor Cyan
Write-Host "Directorio strategies/: $((Get-ChildItem $strategiesDir -ErrorAction SilentlyContinue).Count) archivos" -ForegroundColor Cyan
Write-Host ""
Write-Host "¡Listo para ejecutar la aplicación!" -ForegroundColor Green 