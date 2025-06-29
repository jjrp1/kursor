# Script para cargar datos de prueba en la base de datos de Kursor
# Autor: Juan Jose Ruiz Perez <jjrp1@um.es>
# Version: 1.0.0

param(
    [string]$Accion = "cargar",
    [switch]$Ayuda
)

# Funcion para mostrar ayuda
function Show-Help {
    Write-Host "=== CARGADOR DE DATOS DE PRUEBA - KURSOR ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Uso: .\cargar-datos-prueba.ps1 [comando]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Comandos disponibles:" -ForegroundColor Green
    Write-Host "  cargar       - Cargar datos de prueba en la base de datos" -ForegroundColor White
    Write-Host "  limpiar      - Limpiar todos los datos de la base de datos" -ForegroundColor White
    Write-Host "  estadisticas - Mostrar estadisticas de la base de datos" -ForegroundColor White
    Write-Host ""
    Write-Host "Ejemplos:" -ForegroundColor Green
    Write-Host "  .\cargar-datos-prueba.ps1 cargar" -ForegroundColor White
    Write-Host "  .\cargar-datos-prueba.ps1 limpiar" -ForegroundColor White
    Write-Host "  .\cargar-datos-prueba.ps1 estadisticas" -ForegroundColor White
    Write-Host ""
    Write-Host "Parametros:" -ForegroundColor Green
    Write-Host "  -Ayuda       - Mostrar esta ayuda" -ForegroundColor White
    Write-Host ""
}

# Funcion para verificar que estamos en el directorio correcto
function Test-WorkingDirectory {
    if (-not (Test-Path "kursor-core")) {
        Write-Host "ERROR: Este script debe ejecutarse desde el directorio raiz del proyecto" -ForegroundColor Red
        Write-Host "   Directorio actual: $(Get-Location)" -ForegroundColor Yellow
        Write-Host "   Directorio esperado: ...\kursor" -ForegroundColor Yellow
        exit 1
    }
}

# Funcion para compilar el proyecto
function Build-Project {
    Write-Host "Compilando proyecto..." -ForegroundColor Blue
    
    try {
        Set-Location "kursor-core"
        mvn compile
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "OK: Proyecto compilado correctamente" -ForegroundColor Green
        } else {
            Write-Host "ERROR: Error al compilar el proyecto" -ForegroundColor Red
            exit 1
        }
    }
    catch {
        Write-Host "ERROR: Error durante la compilacion: $_" -ForegroundColor Red
        exit 1
    }
}

# Funcion para ejecutar el cargador de datos
function Invoke-DataLoader {
    param([string]$Comando)
    
    Write-Host "Ejecutando cargador de datos con comando: $Comando" -ForegroundColor Blue
    
    try {
        # Ya estamos en kursor-core, ejecutar directamente
        Write-Host "Ejecutando desde: $(Get-Location)" -ForegroundColor Gray
        
        # Usar Start-Process para ejecutar Maven con argumentos correctos
        $processArgs = @(
            "exec:java",
            "-Dexec.mainClass=com.kursor.util.TestDataLoader",
            "-Dexec.args=$Comando"
        )
        
        Write-Host "Comando Maven: mvn $processArgs" -ForegroundColor Gray
        
        & mvn @processArgs
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "OK: Comando ejecutado correctamente" -ForegroundColor Green
        } else {
            Write-Host "ERROR: Error al ejecutar el comando" -ForegroundColor Red
        }
    }
    catch {
        Write-Host "ERROR: Error durante la ejecucion: $_" -ForegroundColor Red
    }
}

# Funcion para mostrar informacion de la base de datos
function Show-DatabaseInfo {
    Write-Host "Informacion de la base de datos:" -ForegroundColor Cyan
    
    $dbPath = "kursor-core\data\kursor.db"
    if (Test-Path $dbPath) {
        $fileInfo = Get-Item $dbPath
        Write-Host "   Ubicacion: $dbPath" -ForegroundColor White
        Write-Host "   Tamano: $([math]::Round($fileInfo.Length / 1KB, 2)) KB" -ForegroundColor White
        Write-Host "   Ultima modificacion: $($fileInfo.LastWriteTime)" -ForegroundColor White
    } else {
        Write-Host "   ADVERTENCIA: Base de datos no encontrada en: $dbPath" -ForegroundColor Yellow
    }
    
    Write-Host ""
}

# Funcion para cargar datos
function Load-TestData {
    Write-Host "Cargando datos de prueba..." -ForegroundColor Blue
    Invoke-DataLoader -Comando "cargar"
}

# Funcion para limpiar datos
function Clear-AllData {
    Write-Host "Limpiando todos los datos..." -ForegroundColor Yellow
    Invoke-DataLoader -Comando "limpiar"
}

# Funcion para mostrar estadisticas
function Show-Statistics {
    Write-Host "Mostrando estadisticas..." -ForegroundColor Blue
    Invoke-DataLoader -Comando "estadisticas"
}

# Funcion principal
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
    
    # Mostrar informacion de la base de datos
    Show-DatabaseInfo
    
    # Validar comando
    $comandosValidos = @("cargar", "limpiar", "estadisticas")
    if ($comandosValidos -notcontains $Accion) {
        Write-Host "ERROR: Comando no valido: $Accion" -ForegroundColor Red
        Write-Host ""
        Show-Help
        return
    }
    
    # Compilar proyecto
    Build-Project
    
    # Ejecutar comando especifico
    switch ($Accion) {
        "cargar" { Load-TestData }
        "limpiar" { Clear-AllData }
        "estadisticas" { Show-Statistics }
    }
    
    # Volver al directorio raiz
    Set-Location ".."
    
    Write-Host ""
    Write-Host "=== FIN DEL PROCESO ===" -ForegroundColor Cyan
}

# Ejecutar funcion principal
Main 