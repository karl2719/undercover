@echo off
REM Compilation script for Undercover multiplayer game

echo ====================================
echo  Undercover - Compilation Script
echo ====================================
echo.

REM Clean bin directory
if exist bin (
    echo Cleaning bin directory...
    rmdir /s /q bin
)

REM Create bin directory
echo Creating bin directory...
mkdir bin

REM Compile all Java files
echo Compiling Java sources...
javac -d bin -sourcepath src src\com\black\main\*.java src\com\black\gui\*.java src\com\black\client\*.java src\com\black\server\*.java src\com\black\model\*.java src\com\black\utils\*.java src\com\black\enums\*.java src\com\black\interfaces\*.java src\com\black\listeners\*.java src\com\black\service\*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo.
echo [SUCCESS] Compilation completed successfully!
echo.
echo To run the application, use:
echo   java -cp bin com.black.main.UndercoverApp
echo.
echo Or run:
echo   run.bat
echo.
pause
