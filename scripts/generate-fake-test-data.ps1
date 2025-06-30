# Script de PowerShell para generar datos JSON de métricas de testing
# Genera datos de ejemplo basados en la estructura del proyecto

Write-Host "=== Generando métricas de testing con PowerShell ===" -ForegroundColor Green

# Obtener timestamp
$timestamp = Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ"

# Obtener información del commit (si estamos en un repositorio git)
$commitHash = "ejemplo"
$commitMessage = "Datos generados automáticamente"

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

# Datos de ejemplo basados en la estructura del proyecto
$testData = @{
    timestamp = $timestamp
    commit = @{
        hash = $commitHash
        message = $commitMessage
    }
    tests = @{
        total = 42
        passed = 40
        failed = 2
        skipped = 0
        success_rate = 95
        execution_time = "2.3"
    }
    coverage = @{
        average = 92
        modules_count = 9
    }
    modules = @(
        @{
            name = "kursor-core"
            coverage = 89
            lines_covered = 1847
            lines_total = 2075
        },
        @{
            name = "flashcard-module"
            coverage = 88
            lines_covered = 234
            lines_total = 266
        },
        @{
            name = "multiplechoice-module"
            coverage = 95
            lines_covered = 312
            lines_total = 328
        },
        @{
            name = "fillblanks-module"
            coverage = 90
            lines_covered = 198
            lines_total = 220
        },
        @{
            name = "truefalse-module"
            coverage = 87
            lines_covered = 156
            lines_total = 179
        },
        @{
            name = "secuencial-strategy"
            coverage = 93
            lines_covered = 145
            lines_total = 156
        },
        @{
            name = "aleatoria-strategy"
            coverage = 91
            lines_covered = 89
            lines_total = 98
        },
        @{
            name = "repeticion-espaciada-strategy"
            coverage = 96
            lines_covered = 234
            lines_total = 244
        },
        @{
            name = "repetir-incorrectas-strategy"
            coverage = 89
            lines_covered = 123
            lines_total = 138
        }
    )
    status = "success"
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

Write-Host "Archivo de métricas generado: $outputFile" -ForegroundColor Green
Write-Host "Contenido del archivo:" -ForegroundColor Cyan
Write-Host $jsonData

Write-Host "=== Generación completada ===" -ForegroundColor Green 