@echo off
REM Run script for Undercover multiplayer game

if not exist bin (
    echo [ERROR] Compiled classes not found!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

echo ====================================
echo  Running Undercover Application
echo ====================================
echo.

java -cp bin com.black.main.UndercoverApp
