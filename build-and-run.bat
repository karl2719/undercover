@echo off
REM Build and run script for Undercover multiplayer game

call compile.bat
if %errorlevel% neq 0 exit /b 1

echo.
echo Starting application...
echo.

java -cp bin com.black.main.UndercoverApp
