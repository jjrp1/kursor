# Script para probar el CardSelector
Write-Host "🧪 Probando CardSelector..." -ForegroundColor Green

# Compilar el proyecto
Write-Host "📦 Compilando proyecto..." -ForegroundColor Yellow
mvn clean compile -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al compilar el proyecto" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Proyecto compilado correctamente" -ForegroundColor Green

# Ejecutar tests
Write-Host "🧪 Ejecutando tests..." -ForegroundColor Yellow
mvn test -Dtest=CardSelectorModalTest -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error en los tests" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Tests ejecutados correctamente" -ForegroundColor Green

# Ejecutar la aplicación para probar el CardSelector
Write-Host "🚀 Ejecutando aplicación para probar CardSelector..." -ForegroundColor Yellow
Write-Host "💡 Selecciona 'Nueva Sesión' en la aplicación para probar el CardSelector" -ForegroundColor Cyan

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.kursor.presentation.KursorApplication" -q

Write-Host "✅ Prueba completada" -ForegroundColor Green 