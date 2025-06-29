# Script para corregir package declarations específicos

Write-Host " Corrigiendo package declarations específicos..." -ForegroundColor Green

# KursorApplication
$kursorAppFile = "kursor-core/src/main/java/com/kursor/presentation/KursorApplication.java"
if (Test-Path $kursorAppFile) {
    $content = Get-Content $kursorAppFile -Raw
    $content = $content -replace "package com\.kursor\.ui;", "package com.kursor.presentation;"
    $content = $content -replace "import com\.kursor\.ui\.main\.MainController;", "import com.kursor.presentation.controllers.MainController;"
    Set-Content $kursorAppFile $content -NoNewline
    Write-Host " Corregido: KursorApplication.java" -ForegroundColor Green
}

# MainController
$mainControllerFile = "kursor-core/src/main/java/com/kursor/presentation/controllers/MainController.java"
if (Test-Path $mainControllerFile) {
    $content = Get-Content $mainControllerFile -Raw
    $content = $content -replace "package com\.kursor\.ui\.main;", "package com.kursor.presentation.controllers;"
    Set-Content $mainControllerFile $content -NoNewline
    Write-Host " Corregido: MainController.java" -ForegroundColor Green
}

# MainView
$mainViewFile = "kursor-core/src/main/java/com/kursor/presentation/views/MainView.java"
if (Test-Path $mainViewFile) {
    $content = Get-Content $mainViewFile -Raw
    $content = $content -replace "package com\.kursor\.ui\.main;", "package com.kursor.presentation.views;"
    Set-Content $mainViewFile $content -NoNewline
    Write-Host " Corregido: MainView.java" -ForegroundColor Green
}

# MainViewModel
$mainViewModelFile = "kursor-core/src/main/java/com/kursor/presentation/viewmodels/MainViewModel.java"
if (Test-Path $mainViewModelFile) {
    $content = Get-Content $mainViewModelFile -Raw
    $content = $content -replace "package com\.kursor\.ui\.main;", "package com.kursor.presentation.viewmodels;"
    Set-Content $mainViewModelFile $content -NoNewline
    Write-Host " Corregido: MainViewModel.java" -ForegroundColor Green
}

Write-Host " Package declarations específicos corregidos" -ForegroundColor Green
