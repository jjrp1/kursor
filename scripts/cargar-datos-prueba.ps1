# Script para cargar datos de prueba en la base de datos de Kursor
# Autor: Juan José Ruiz Pérez <jjrp1@um.es>
# Versión: 1.0.0

param(
    [string]$Accion = "cargar",
    [switch]$Ayuda
)

# Función para mostrar ayuda
function Show-Help {
    Write-Host "=== CARGADOR DE DATOS DE PRUEBA - KURSOR ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Uso: .\cargar-datos-prueba.ps1 [comando]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Comandos disponibles:" -ForegroundColor Green
    Write-Host "  cargar       - Cargar datos de prueba en la base de datos" -ForegroundColor White
    Write-Host "  limpiar      - Limpiar todos los datos de la base de datos" -ForegroundColor White
    Write-Host "  estadisticas - Mostrar estadísticas de la base de datos" -ForegroundColor White
    Write-Host ""
    Write-Host "Ejemplos:" -ForegroundColor Green
    Write-Host "  .\cargar-datos-prueba.ps1 cargar" -ForegroundColor White
    Write-Host "  .\cargar-datos-prueba.ps1 limpiar" -ForegroundColor White
    Write-Host "  .\cargar-datos-prueba.ps1 estadisticas" -ForegroundColor White
    Write-Host ""
    Write-Host "Parámetros:" -ForegroundColor Green
    Write-Host "  -Ayuda       - Mostrar esta ayuda" -ForegroundColor White
    Write-Host ""
}

# Función para verificar que estamos en el directorio correcto
function Test-WorkingDirectory {
    if (-not (Test-Path "kursor-core")) {
        Write-Host "? Error: Este script debe ejecutarse desde el directorio raíz del proyecto" -ForegroundColor Red
        Write-Host "   Directorio actual: $(Get-Location)" -ForegroundColor Yellow
        Write-Host "   Directorio esperado: ...\kursor" -ForegroundColor Yellow
        exit 1
    }
}

# Función para compilar el proyecto
function Build-Project {
    Write-Host "?? Compilando proyecto..." -ForegroundColor Blue
    
    try {
        Set-Location "kursor-core"
        mvn compile
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "? Proyecto compilado correctamente" -ForegroundColor Green
        } else {
            Write-Host "? Error al compilar el proyecto" -ForegroundColor Red
            exit 1
        }
    }
    catch {
        Write-Host "? Error durante la compilación: $_" -ForegroundColor Red
        exit 1
    }
}

# Función para ejecutar el cargador de datos
function Invoke-DataLoader {
    param([string]$Comando)
    
    Write-Host "🚀 Ejecutando cargador de datos con comando: $Comando" -ForegroundColor Blue
    
    try {
        # Ejecutar la clase TestDataLoader usando el plugin exec sin comillas
        mvn exec:java -Dexec.mainClass=com.kursor.util.TestDataLoader -Dexec.args=$Comando
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Comando ejecutado correctamente" -ForegroundColor Green
        } else {
            Write-Host "❌ Error al ejecutar el comando" -ForegroundColor Red
        }
    }
    catch {
        Write-Host "❌ Error durante la ejecución: $_" -ForegroundColor Red
    }
}

# Función para mostrar información de la base de datos
function Show-DatabaseInfo {
    Write-Host "?? Información de la base de datos:" -ForegroundColor Cyan
    
    $dbPath = "kursor-core\data\kursor.db"
    if (Test-Path $dbPath) {
        $fileInfo = Get-Item $dbPath
        Write-Host "   Ubicación: $dbPath" -ForegroundColor White
        Write-Host "   Tamaño: $([math]::Round($fileInfo.Length / 1KB, 2)) KB" -ForegroundColor White
        Write-Host "   Última modificación: $($fileInfo.LastWriteTime)" -ForegroundColor White
    } else {
        Write-Host "   ??  Base de datos no encontrada en: $dbPath" -ForegroundColor Yellow
    }
    
    Write-Host ""
}

# Función para cargar datos
function Load-TestData {
    Write-Host "?? Cargando datos de prueba..." -ForegroundColor Blue
    Invoke-DataLoader -Comando "cargar"
}

# Función para limpiar datos
function Clear-AllData {
    Write-Host "??? Limpiando todos los datos..." -ForegroundColor Yellow // 
    Invoke-DataLoader -Comando "limpiar"
}

# Función para mostrar estadísticas
function Show-Statistics {
    Write-Host "?? Mostrando estadísticas..." -ForegroundColor Blue
    Invoke-DataLoader -Comando "estadisticas"
}

# Función principal
function Main {
    # Mostrar ayuda si se solicita
    if ($Ayuda) {
        Show-Help
        return
    }
    
    Write-Host "=== CARGADOR DE DATOS DE PRUEBA - KURSOR ===" -ForegroundColor Cyan
    Write-Host "Fecha: $(Get-Date)" -ForegroundColor Gray
    Write-Host ""
    
    # Verificar directorio de trabajo
    Test-WorkingDirectory
    
    # Mostrar información de la base de datos
    Show-DatabaseInfo
    
    # Validar comando
    $comandosValidos = @("cargar", "limpiar", "estadisticas")
    if ($comandosValidos -notcontains $Accion) {
        Write-Host "? Comando no válido: $Accion" -ForegroundColor Red
        Write-Host ""
        Show-Help
        return
    }
    
    # Compilar proyecto
    Build-Project
    
    # Ejecutar comando específico
    switch ($Accion) {
        "cargar" { Load-TestData }
        "limpiar" { Clear-AllData }
        "estadisticas" { Show-Statistics }
    }
    
    Write-Host ""
    Write-Host "=== FIN DEL PROCESO ===" -ForegroundColor Cyan
}

# Ejecutar función principal
Main 