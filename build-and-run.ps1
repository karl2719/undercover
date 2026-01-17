#!/usr/bin/env pwsh
# Build and run script for Undercover multiplayer game

& ".\compile.ps1"
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Starting application..." -ForegroundColor Green
Write-Host ""

java -cp bin com.black.main.UndercoverApp
