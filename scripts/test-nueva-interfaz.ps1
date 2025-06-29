# Script para compilar y probar la nueva interfaz de Kursor
# Autor: Juan José Ruiz Pérez
# Fecha: 2025-06-29

Write-Host "🚀 Iniciando prueba de la nueva interfaz de Kursor..." -ForegroundColor Green

# Verificar que estamos en el directorio correcto
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ Error: No se encontró pom.xml. Ejecuta este script desde la raíz del proyecto." -ForegroundColor Red
    exit 1
}

# Limpiar compilación anterior
Write-Host "🧹 Limpiando compilación anterior..." -ForegroundColor Yellow
mvn clean

# Compilar el proyecto
Write-Host "🔨 Compilando proyecto..." -ForegroundColor Yellow
mvn compile

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error durante la compilación" -ForegroundColor Red
    exit 1
}

# Ejecutar la nueva aplicación
Write-Host "🎨 Ejecutando nueva interfaz de Kursor..." -ForegroundColor Green
Write-Host "📝 Para probar la interfaz anterior, ejecuta: mvn exec:java -Dexec.mainClass='com.kursor.ui.KursorApplication'" -ForegroundColor Cyan

# Ejecutar la nueva aplicación
mvn exec:java -Dexec.mainClass="com.kursor.ui.KursorApplicationNew"

Write-Host "✅ Prueba completada" -ForegroundColor Green 