# Script para probar el CardSelector corregido
Write-Host "🧪 Probando CardSelector corregido..." -ForegroundColor Green

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

# Mostrar información sobre las correcciones
Write-Host "🔧 Correcciones aplicadas:" -ForegroundColor Cyan
Write-Host "   - Cambiado StageStyle.UNDECORATED a StageStyle.DECORATED" -ForegroundColor White
Write-Host "   - Agregado método mostrarSimple() sin animaciones complejas" -ForegroundColor White
Write-Host "   - Configurado tamaño de ventana fijo (1000x700)" -ForegroundColor White
Write-Host "   - Simplificado animaciones para evitar problemas de renderizado" -ForegroundColor White

# Ejecutar la aplicación para probar el CardSelector
Write-Host "🚀 Ejecutando aplicación para probar CardSelector corregido..." -ForegroundColor Yellow
Write-Host "💡 Selecciona 'Nueva Sesión' en la aplicación para probar el CardSelector" -ForegroundColor Cyan
Write-Host "💡 Ahora debería aparecer una ventana normal con barra de título" -ForegroundColor Cyan

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.kursor.presentation.KursorApplication" -q

Write-Host "✅ Prueba completada" -ForegroundColor Green 