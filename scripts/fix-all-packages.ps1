# Script completo para corregir todos los package declarations

Write-Host " Corrigiendo TODOS los package declarations..." -ForegroundColor Green

$javaFiles = Get-ChildItem -Path "kursor-core/src/main/java" -Recurse -Include "*.java"

foreach ($file in $javaFiles) {
    $relativePath = $file.FullName.Replace((Get-Location).Path + "\", "")
    $expectedPackage = $relativePath.Replace("\", ".").Replace(".java", "")
    $expectedPackage = $expectedPackage.Replace("kursor-core\src\main\java\", "")
    
    $content = Get-Content $file.FullName -Raw
    $lines = $content -split "`n"
    
    $packageLine = $lines | Where-Object { $_ -match "^package\s+" } | Select-Object -First 1
    
    if ($packageLine) {
        $currentPackage = ($packageLine -replace "^package\s+", "").TrimEnd(";").Trim()
        
        if ($currentPackage -ne $expectedPackage) {
            Write-Host " Corrigiendo: $($file.Name)" -ForegroundColor Yellow
            Write-Host "   Actual: $currentPackage" -ForegroundColor Red
            Write-Host "   Esperado: $expectedPackage" -ForegroundColor Green
            
            # Reemplazar la línea del package
            $newContent = $content -replace "^package\s+[^;]+;", "package $expectedPackage;"
            Set-Content $file.FullName $newContent -NoNewline
        }
    }
}

Write-Host " TODOS los package declarations corregidos" -ForegroundColor Green
