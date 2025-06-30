# Script de prueba simplificado para CardSelector
Write-Host "🧪 Iniciando prueba simplificada del CardSelector..." -ForegroundColor Cyan

# Limpiar logs anteriores
if (Test-Path "kursor-core/log/error.log") {
    Remove-Item "kursor-core/log/error.log" -Force
    Write-Host "🗑️ Log de errores limpiado" -ForegroundColor Yellow
}

if (Test-Path "kursor-core/log/kursor.log") {
    Remove-Item "kursor-core/log/kursor.log" -Force
    Write-Host "🗑️ Log principal limpiado" -ForegroundColor Yellow
}

# Compilar solo el módulo core
Write-Host "🔨 Compilando kursor-core..." -ForegroundColor Green
cd kursor-core
mvn clean compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al compilar kursor-core" -ForegroundColor Red
    exit 1
}
Write-Host "✅ kursor-core compilado exitosamente" -ForegroundColor Green

# Ejecutar test específico del CardSelector
Write-Host "🧪 Ejecutando test del CardSelector..." -ForegroundColor Green
mvn test -Dtest=CardSelectorModalTest -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error en el test del CardSelector" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Test del CardSelector completado" -ForegroundColor Green

# Ejecutar la aplicación
Write-Host "🚀 Ejecutando aplicación..." -ForegroundColor Green
Start-Process -FilePath "mvn" -ArgumentList "exec:java", "-Dexec.mainClass=com.kursor.presentation.KursorApplication", "-q" -WindowStyle Normal

# Esperar un poco para que la aplicación se inicie
Start-Sleep -Seconds 5

# Verificar si hay errores en el log
if (Test-Path "log/error.log") {
    $errorLog = Get-Content "log/error.log" -Tail 10
    if ($errorLog -match "ERROR") {
        Write-Host "❌ Se encontraron errores en el log:" -ForegroundColor Red
        $errorLog | ForEach-Object { Write-Host "   $_" -ForegroundColor Red }
    } else {
        Write-Host "✅ No se encontraron errores críticos en el log" -ForegroundColor Green
    }
}

Write-Host "📋 Instrucciones para probar:" -ForegroundColor Cyan
Write-Host "   1. Selecciona un curso de la lista" -ForegroundColor White
Write-Host "   2. Haz clic en 'Nueva Sesión'" -ForegroundColor White
Write-Host "   3. Selecciona una estrategia" -ForegroundColor White
Write-Host "   4. El CardSelector debería aparecer con barra de título" -ForegroundColor White
Write-Host "   5. Selecciona un bloque y haz clic en 'Comenzar'" -ForegroundColor White

Write-Host "⏳ La aplicación se ejecutará en una ventana separada..." -ForegroundColor Yellow
Write-Host "🛑 Cierra la ventana de la aplicación cuando termines de probar" -ForegroundColor Yellow

cd ..
Write-Host "✅ Prueba completada" -ForegroundColor Green 