#!/usr/bin/env pwsh
# Compilation script for Undercover multiplayer game

Write-Host "====================================" -ForegroundColor Cyan
Write-Host " Undercover - Compilation Script" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Clean bin directory
if (Test-Path "bin") {
    Write-Host "Cleaning bin directory..." -ForegroundColor Yellow
    Remove-Item -Recurse -Force "bin"
}

# Create bin directory
Write-Host "Creating bin directory..." -ForegroundColor Yellow
New-Item -ItemType Directory -Path "bin" | Out-Null

# Compile all Java files
Write-Host "Compiling Java sources..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path src -Filter *.java -Recurse | Select-Object -ExpandProperty FullName
javac -d bin -sourcepath src $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[SUCCESS] Compilation completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "To run the application, use:" -ForegroundColor Cyan
Write-Host "  java -cp bin com.black.main.UndercoverApp" -ForegroundColor White
Write-Host ""
Write-Host "Or run:" -ForegroundColor Cyan
Write-Host "  .\run.ps1" -ForegroundColor White
Write-Host ""
