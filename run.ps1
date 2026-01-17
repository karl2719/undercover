#!/usr/bin/env pwsh
# Run script for Undercover multiplayer game

if (-not (Test-Path "bin")) {
    Write-Host "[ERROR] Compiled classes not found!" -ForegroundColor Red
    Write-Host "Please run compile.ps1 first." -ForegroundColor Yellow
    exit 1
}

Write-Host "====================================" -ForegroundColor Cyan
Write-Host " Running Undercover Application" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

java -cp bin com.black.main.UndercoverApp
