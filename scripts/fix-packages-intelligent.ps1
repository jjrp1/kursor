# Script inteligente para corregir package declarations

Write-Host "Corrigiendo package declarations de manera inteligente..." -ForegroundColor Green

# Mapeo de rutas a packages
$packageMapping = @{
    "kursor-core/src/main/java/com/kursor/presentation/KursorApplication.java" = "com.kursor.presentation"
    "kursor-core/src/main/java/com/kursor/presentation/controllers/MainController.java" = "com.kursor.presentation.controllers"
    "kursor-core/src/main/java/com/kursor/presentation/controllers/CursoInterfaceController.java" = "com.kursor.presentation.controllers"
    "kursor-core/src/main/java/com/kursor/presentation/controllers/CursoSessionManager.java" = "com.kursor.presentation.controllers"
    "kursor-core/src/main/java/com/kursor/presentation/controllers/PreguntaEventListener.java" = "com.kursor.presentation.controllers"
    "kursor-core/src/main/java/com/kursor/presentation/controllers/PreguntaResponseExtractor.java" = "com.kursor.presentation.controllers"
    "kursor-core/src/main/java/com/kursor/presentation/controllers/SessionController.java" = "com.kursor.presentation.controllers"
    "kursor-core/src/main/java/com/kursor/presentation/dialogs/AboutDialog.java" = "com.kursor.presentation.dialogs"
    "kursor-core/src/main/java/com/kursor/presentation/dialogs/EstadisticasDialog.java" = "com.kursor.presentation.dialogs"
    "kursor-core/src/main/java/com/kursor/presentation/dialogs/EstrategiaSelectionModal.java" = "com.kursor.presentation.dialogs"
    "kursor-core/src/main/java/com/kursor/presentation/viewmodels/MainViewModel.java" = "com.kursor.presentation.viewmodels"
    "kursor-core/src/main/java/com/kursor/presentation/viewmodels/SessionViewModel.java" = "com.kursor.presentation.viewmodels"
    "kursor-core/src/main/java/com/kursor/presentation/views/CursoInterfaceView.java" = "com.kursor.presentation.views"
    "kursor-core/src/main/java/com/kursor/presentation/views/InspeccionarCurso.java" = "com.kursor.presentation.views"
    "kursor-core/src/main/java/com/kursor/presentation/views/MainView.java" = "com.kursor.presentation.views"
    "kursor-core/src/main/java/com/kursor/presentation/views/MenuBarExample.java" = "com.kursor.presentation.views"
    "kursor-core/src/main/java/com/kursor/presentation/views/SessionTableView.java" = "com.kursor.presentation.views"
}

foreach ($file in $packageMapping.Keys) {
    if (Test-Path $file) {
        $expectedPackage = $packageMapping[$file]
        $content = Get-Content $file -Raw
        
        # Buscar el package actual
        if ($content -match "package\s+([^;]+);") {
            $currentPackage = $matches[1].Trim()
            
            if ($currentPackage -ne $expectedPackage) {
                Write-Host "Corrigiendo: $(Split-Path $file -Leaf)" -ForegroundColor Yellow
                Write-Host "   Actual: $currentPackage" -ForegroundColor Red
                Write-Host "   Esperado: $expectedPackage" -ForegroundColor Green
                
                # Reemplazar el package
                $newContent = $content -replace "package\s+[^;]+;", "package $expectedPackage;"
                Set-Content $file $newContent -NoNewline
            }
        }
    }
}

Write-Host "Package declarations corregidos inteligentemente" -ForegroundColor Green
