@echo off
setlocal
cd /d "%~dp0"

if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-27" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-27"
    )
)

echo ========================================
echo       Starting StudyMate AI...
echo ========================================

call .\mvnw.cmd javafx:run

if %ERRORLEVEL% neq 0 (
    echo.
    echo An error occurred while launching StudyMate AI.
    pause
)
