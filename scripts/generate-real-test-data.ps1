# Script de PowerShell para generar datos JSON de métricas de testing REALES
# Ejecuta tests con Maven y extrae información de reportes Jacoco y Surefire

Write-Host "=== Generando métricas de testing REALES con PowerShell ===" -ForegroundColor Green

# Verificar que Maven esté disponible
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Instala Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Verificar que estamos en un directorio con pom.xml
if (-not (Test-Path "pom.xml")) {
    Write-Host "ERROR: No se encontró pom.xml en el directorio actual" -ForegroundColor Red
    Write-Host "Ejecuta este script desde la raíz del proyecto" -ForegroundColor Yellow
    exit 1
}

Write-Host "Ejecutando tests con cobertura..." -ForegroundColor Yellow

# Ejecutar tests con cobertura
try {
    $result = & mvn clean test jacoco:report 2>&1
    $exitCode = $LASTEXITCODE
    
    if ($exitCode -ne 0) {
        Write-Host "ERROR: Los tests fallaron. Revisa los errores arriba." -ForegroundColor Red
        Write-Host "Sugerencia: Ejecuta 'mvn clean compile' primero para verificar compilación" -ForegroundColor Yellow
        exit 1
    }
    
    Write-Host "Tests ejecutados exitosamente!" -ForegroundColor Green
} catch {
    Write-Host "ERROR: No se pudieron ejecutar los tests: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Buscar reportes generados
Write-Host "Buscando reportes de testing..." -ForegroundColor Yellow

$jacocoReports = @()
$surefireReports = @()

# Buscar reportes de Jacoco
Get-ChildItem -Path . -Recurse -Name "jacoco.xml" | ForEach-Object {
    $jacocoReports += $_
    Write-Host "  Encontrado reporte Jacoco: $_" -ForegroundColor Cyan
}

# Buscar reportes de Surefire
Get-ChildItem -Path . -Recurse -Name "TEST-*.xml" | ForEach-Object {
    $surefireReports += $_
    Write-Host "  Encontrado reporte Surefire: $_" -ForegroundColor Cyan
}

if ($jacocoReports.Count -eq 0 -and $surefireReports.Count -eq 0) {
    Write-Host "ADVERTENCIA: No se encontraron reportes de testing" -ForegroundColor Yellow
    Write-Host "Usando datos de fallback..." -ForegroundColor Yellow
    & "$PSScriptRoot\generate-fake-test-data.ps1"
    exit 0
}

# Inicializar contadores
$totalTests = 0
$passedTests = 0
$failedTests = 0
$skippedTests = 0
$totalCoverage = 0
$modulesCount = 0
$executionTime = 0

# Procesar reportes de Surefire
Write-Host "Procesando reportes de Surefire..." -ForegroundColor Yellow
foreach ($report in $surefireReports) {
    if (Test-Path $report) {
        $content = Get-Content $report -Raw
        
        # Extraer métricas usando regex
        if ($content -match 'tests="(\d+)"') {
            $tests = [int]$matches[1]
            $totalTests += $tests
        }
        
        if ($content -match 'failures="(\d+)"') {
            $failures = [int]$matches[1]
            $failedTests += $failures
        }
        
        if ($content -match 'errors="(\d+)"') {
            $errors = [int]$matches[1]
            $failedTests += $errors
        }
        
        if ($content -match 'skipped="(\d+)"') {
            $skips = [int]$matches[1]
            $skippedTests += $skips
        }
        
        if ($content -match 'time="([\d.]+)"') {
            $time = [double]$matches[1]
            $executionTime += $time
        }
    }
}

$passedTests = $totalTests - $failedTests - $skippedTests

# Procesar reportes de Jacoco
Write-Host "Procesando reportes de Jacoco..." -ForegroundColor Yellow
$modules = @()

foreach ($report in $jacocoReports) {
    if (Test-Path $report) {
        $content = Get-Content $report -Raw
        
        # Extraer métricas de cobertura
        if ($content -match 'INSTRUCTION.*covered="(\d+)".*missed="(\d+)"') {
            $covered = [int]$matches[1]
            $missed = [int]$matches[2]
            $totalLines = $covered + $missed
            
            if ($totalLines -gt 0) {
                $coverage = [math]::Round(($covered * 100) / $totalLines)
                $totalCoverage += $coverage
                $modulesCount++
                
                # Extraer nombre del módulo del path
                $modulePath = Split-Path $report -Parent
                $moduleName = Split-Path $modulePath -Leaf
                
                $modules += @{
                    name = $moduleName
                    coverage = $coverage
                    lines_covered = $covered
                    lines_total = $totalLines
                }
                
                Write-Host "  Módulo $moduleName`: $coverage% cobertura ($covered/$totalLines líneas)" -ForegroundColor Green
            }
        }
    }
}

# Calcular promedios
$avgCoverage = if ($modulesCount -gt 0) { [math]::Round($totalCoverage / $modulesCount) } else { 0 }
$successRate = if ($totalTests -gt 0) { [math]::Round(($passedTests * 100) / $totalTests) } else { 0 }

# Obtener timestamp
$timestamp = Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ"

# Obtener información del commit
$commitHash = "unknown"
$commitMessage = "No commit info"

try {
    if (Get-Command git -ErrorAction SilentlyContinue) {
        $commitHash = git rev-parse --short HEAD 2>$null
        if ($LASTEXITCODE -eq 0) {
            $commitMessage = git log -1 --pretty=%B 2>$null | Select-Object -First 1
        }
    }
} catch {
    Write-Host "No se pudo obtener información del commit, usando valores por defecto" -ForegroundColor Yellow
}

# Determinar estado del build
$buildStatus = if ($failedTests -eq 0 -and $totalTests -gt 0) { "success" } else { "failure" }

# Generar datos JSON
$testData = @{
    timestamp = $timestamp
    commit = @{
        hash = $commitHash
        message = $commitMessage
    }
    tests = @{
        total = $totalTests
        passed = $passedTests
        failed = $failedTests
        skipped = $skippedTests
        success_rate = $successRate
        execution_time = [math]::Round($executionTime, 1).ToString()
    }
    coverage = @{
        average = $avgCoverage
        modules_count = $modulesCount
    }
    modules = $modules
    status = $buildStatus
}

# Convertir a JSON con formato legible
$jsonData = $testData | ConvertTo-Json -Depth 10

# Crear directorio si no existe
$outputDir = "docs/reports/data"
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    Write-Host "Directorio creado: $outputDir" -ForegroundColor Yellow
}

# Guardar el archivo JSON
$outputFile = "$outputDir/test-metrics.json"
$jsonData | Out-File -FilePath $outputFile -Encoding UTF8

Write-Host "`nArchivo de métricas REALES generado: $outputFile" -ForegroundColor Green
Write-Host "Resumen:" -ForegroundColor Cyan
Write-Host "  - Tests totales: $totalTests" -ForegroundColor White
Write-Host "  - Tests exitosos: $passedTests" -ForegroundColor Green
Write-Host "  - Tests fallidos: $failedTests" -ForegroundColor Red
Write-Host "  - Tests omitidos: $skippedTests" -ForegroundColor Yellow
Write-Host "  - Tasa de éxito: $successRate%" -ForegroundColor Cyan
Write-Host "  - Cobertura promedio: $avgCoverage%" -ForegroundColor Cyan
Write-Host "  - Módulos analizados: $modulesCount" -ForegroundColor White
Write-Host "  - Tiempo de ejecución: $executionTime segundos" -ForegroundColor White

Write-Host "`n=== Generación de métricas REALES completada ===" -ForegroundColor Green 