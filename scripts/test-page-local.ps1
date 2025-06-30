# Script para probar la página de resultados de testing localmente
# Inicia un servidor web local para verificar que la página funciona correctamente

Write-Host "=== Probando página de resultados de testing localmente ===" -ForegroundColor Green

# Verificar que existe el archivo de métricas
$testMetricsFile = "docs/reports/data/test-metrics.json"
if (-not (Test-Path $testMetricsFile)) {
    Write-Host "ERROR: No se encontró el archivo de métricas: $testMetricsFile" -ForegroundColor Red
    Write-Host "Ejecutando generación de datos..." -ForegroundColor Yellow
    & "$PSScriptRoot\generate-test-data.ps1"
}

# Verificar que existe la página HTML
$htmlFile = "docs/resultados-pruebas.html"
if (-not (Test-Path $htmlFile)) {
    Write-Host "ERROR: No se encontró la página HTML: $htmlFile" -ForegroundColor Red
    exit 1
}

Write-Host "Archivos verificados correctamente" -ForegroundColor Green
Write-Host "  - Métricas: $testMetricsFile" -ForegroundColor Cyan
Write-Host "  - Página: $htmlFile" -ForegroundColor Cyan

# Cambiar al directorio docs
Push-Location docs

try {
    Write-Host "`nIniciando servidor web local..." -ForegroundColor Yellow
    Write-Host "La página estará disponible en: http://localhost:8000/resultados-pruebas.html" -ForegroundColor Green
    Write-Host "Presiona Ctrl+C para detener el servidor" -ForegroundColor Yellow
    
    # Intentar usar Python si está disponible
    if (Get-Command python -ErrorAction SilentlyContinue) {
        python -m http.server 8000
    } elseif (Get-Command python3 -ErrorAction SilentlyContinue) {
        python3 -m http.server 8000
    } else {
        Write-Host "ERROR: No se encontró Python instalado" -ForegroundColor Red
        Write-Host "Alternativas:" -ForegroundColor Yellow
        Write-Host "  1. Instalar Python desde https://python.org" -ForegroundColor Cyan
        Write-Host "  2. Usar otro servidor web como Node.js o PHP" -ForegroundColor Cyan
        Write-Host "  3. Abrir el archivo directamente en el navegador: $htmlFile" -ForegroundColor Cyan
    }
} finally {
    # Volver al directorio original
    Pop-Location
}

Write-Host "`n=== Prueba completada ===" -ForegroundColor Green 