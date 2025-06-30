# Script para actualizar datos de testing REALES y subir cambios al repositorio
# Este script ejecuta tests reales con Maven y sube los datos a GitHub Pages

Write-Host "=== Actualizando datos de testing REALES para GitHub Pages ===" -ForegroundColor Green

# Verificar si estamos en un repositorio git
if (-not (Test-Path ".git")) {
    Write-Host "ERROR: No se encontró un repositorio git en el directorio actual" -ForegroundColor Red
    exit 1
}

# Verificar que Maven esté disponible
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Instala Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Generar datos de testing reales
Write-Host "Generando datos de testing reales..." -ForegroundColor Yellow
& "$PSScriptRoot\generate-real-test-data.ps1"

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: No se pudieron generar los datos de testing reales" -ForegroundColor Red
    Write-Host "¿Quieres usar datos ficticios como fallback? (s/n): " -ForegroundColor Yellow -NoNewline
    $response = Read-Host
    
    if ($response -eq "s" -or $response -eq "S" -or $response -eq "y" -or $response -eq "Y") {
        Write-Host "Generando datos ficticios como fallback..." -ForegroundColor Yellow
        & "$PSScriptRoot\generate-fake-test-data.ps1"
    } else {
        exit 1
    }
}

# Verificar que el archivo se generó correctamente
$testMetricsFile = "docs/reports/data/test-metrics.json"
if (-not (Test-Path $testMetricsFile)) {
    Write-Host "ERROR: No se encontró el archivo de métricas generado" -ForegroundColor Red
    exit 1
}

Write-Host "Archivo de métricas reales generado exitosamente: $testMetricsFile" -ForegroundColor Green

# Mostrar contenido del archivo
Write-Host "Contenido del archivo:" -ForegroundColor Cyan
Get-Content $testMetricsFile | Write-Host

# Preguntar si se quieren subir los cambios
Write-Host "`n¿Deseas subir estos cambios al repositorio? (s/n): " -ForegroundColor Yellow -NoNewline
$response = Read-Host

if ($response -eq "s" -or $response -eq "S" -or $response -eq "y" -or $response -eq "Y") {
    Write-Host "Subiendo cambios al repositorio..." -ForegroundColor Yellow
    
    # Agregar archivos
    git add $testMetricsFile
    
    # Commit
    $commitMessage = "feat: Actualizar métricas de testing REALES - $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    git commit -m $commitMessage
    
    # Push
    git push origin master
    
    Write-Host "Cambios subidos exitosamente!" -ForegroundColor Green
    Write-Host "Los datos de testing REALES se actualizarán en GitHub Pages en unos minutos." -ForegroundColor Cyan
    Write-Host "Los workflows de CI también se ejecutarán para generar datos adicionales." -ForegroundColor Cyan
} else {
    Write-Host "Cambios no subidos. Los datos reales están disponibles localmente en: $testMetricsFile" -ForegroundColor Yellow
}

Write-Host "=== Proceso completado ===" -ForegroundColor Green 