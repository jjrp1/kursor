# Script de validacion rapida para verificar la configuracion
# Verifica que modulos y estrategias esten disponibles

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  KURSOR - VALIDACION DE CONFIGURACION" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Configuracion
$modulesDir = "modules"
$strategiesDir = "strategies"
$cursosDir = "cursos"

# Funcion para mostrar progreso
function Write-Progress-Step {
    param([string]$Message, [string]$Status = "INFO")
    $color = switch ($Status) {
        "INFO" { "Green" }
        "WARN" { "Yellow" }
        "ERROR" { "Red" }
        "SUCCESS" { "Cyan" }
        default { "White" }
    }
    Write-Host "[$Status] $Message" -ForegroundColor $color
}

# Funcion para validar directorio
function Test-Directory {
    param([string]$Path, [string]$Description)
    if (Test-Path $Path) {
        $fileCount = (Get-ChildItem $Path -ErrorAction SilentlyContinue).Count
        if ($fileCount -gt 0) {
            Write-Progress-Step "$Description - $fileCount archivos" "SUCCESS"
            return $true
        } else {
            Write-Progress-Step "$Description - directorio vacio" "WARN"
            return $false
        }
    } else {
        Write-Progress-Step "$Description - no encontrado" "ERROR"
        return $false
    }
}

# Funcion para listar archivos
function Show-Files {
    param([string]$Path, [string]$Description)
    if (Test-Path $Path) {
        $files = Get-ChildItem $Path -ErrorAction SilentlyContinue
        if ($files.Count -gt 0) {
            Write-Host "  $Description" -ForegroundColor White
            foreach ($file in $files) {
                Write-Host "    - $($file.Name)" -ForegroundColor Gray
            }
        }
    }
}

# Validacion principal
Write-Progress-Step "Iniciando validacion..." "INFO"
Write-Host ""

# Verificar modulos
$modulesOk = Test-Directory $modulesDir "Modulos de preguntas"
Show-Files $modulesDir "Modulos disponibles"

Write-Host ""

# Verificar estrategias
$strategiesOk = Test-Directory $strategiesDir "Estrategias de aprendizaje"
Show-Files $strategiesDir "Estrategias disponibles"

Write-Host ""

# Verificar cursos
$cursosOk = Test-Directory $cursosDir "Cursos disponibles"
Show-Files $cursosDir "Cursos disponibles"

Write-Host ""

# Verificar JAR principal
$coreJar = "kursor-core\target\kursor-core-1.0.0.jar"
$coreJarShaded = "kursor-core\target\kursor-core-1.0.0-shaded.jar"

if (Test-Path $coreJarShaded) {
    Write-Progress-Step "JAR principal (shaded) - encontrado" "SUCCESS"
    $coreOk = $true
} elseif (Test-Path $coreJar) {
    Write-Progress-Step "JAR principal - encontrado" "SUCCESS"
    $coreOk = $true
} else {
    Write-Progress-Step "JAR principal - no encontrado" "ERROR"
    Write-Progress-Step "  Ejecute: mvn clean package" "INFO"
    $coreOk = $false
}

Write-Host ""

# Resumen final
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  RESUMEN DE VALIDACION" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$totalChecks = 4
$passedChecks = 0

if ($modulesOk) { $passedChecks++ }
if ($strategiesOk) { $passedChecks++ }
if ($cursosOk) { $passedChecks++ }
if ($coreOk) { $passedChecks++ }

Write-Host "Checks pasados: $passedChecks/$totalChecks" -ForegroundColor White

if ($passedChecks -eq $totalChecks) {
    Write-Host ""
    Write-Host "CONFIGURACION COMPLETA!" -ForegroundColor Green
    Write-Host "La aplicacion esta lista para ejecutarse." -ForegroundColor Green
    Write-Host ""
    Write-Host "Para ejecutar:" -ForegroundColor Cyan
    Write-Host "  java -jar kursor-core\target\kursor-core-1.0.0.jar" -ForegroundColor Yellow
    Write-Host ""
} elseif ($passedChecks -ge 2) {
    Write-Host ""
    Write-Host "CONFIGURACION PARCIAL" -ForegroundColor Yellow
    Write-Host "La aplicacion puede ejecutarse pero con funcionalidad limitada." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Recomendaciones:" -ForegroundColor Cyan
    if (-not $modulesOk) {
        Write-Host "  - Ejecute: .\scripts\build-simple.ps1 -Clean" -ForegroundColor Yellow
    }
    if (-not $strategiesOk) {
        Write-Host "  - Ejecute: .\scripts\build-simple.ps1 -Clean" -ForegroundColor Yellow
    }
    if (-not $coreOk) {
        Write-Host "  - Ejecute: mvn clean package" -ForegroundColor Yellow
    }
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "CONFIGURACION INCOMPLETA" -ForegroundColor Red
    Write-Host "La aplicacion no puede ejecutarse correctamente." -ForegroundColor Red
    Write-Host ""
    Write-Host "Acciones requeridas:" -ForegroundColor Cyan
    Write-Host "  1. Ejecute: mvn clean package" -ForegroundColor Yellow
    Write-Host "  2. Ejecute: .\scripts\build-simple.ps1 -Clean" -ForegroundColor Yellow
    Write-Host ""
}

# Informacion adicional
Write-Host "================================================" -ForegroundColor Gray
Write-Host "Informacion adicional:" -ForegroundColor Gray
Write-Host "  - Directorio actual: $(Get-Location)" -ForegroundColor Gray
Write-Host "================================================" -ForegroundColor Gray 