@echo off
setlocal enabledelayedexpansion
title BookLoop - Smart Student Book Exchange

echo ===============================================================================
echo   Starting BookLoop Desktop Application...
echo   "Your old books. Someone else's next semester."
echo ===============================================================================
echo.

cd /d "%~dp0"

REM 1. Find a working Java runtime (prioritize direct JDK installs over broken Oracle PATH stubs)
set "JAVA_CMD="

if exist "C:\Program Files\Java\jdk-26.0.2\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Java\jdk-26.0.2\bin\java.exe"
    goto :found_java
)

for /d %%D in ("C:\Program Files\Java\jdk*" "C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Java\jre*" "C:\Program Files\Amazon Corretto\jdk*") do (
    if exist "%%D\bin\java.exe" (
        set "JAVA_CMD=%%D\bin\java.exe"
        goto :found_java
    )
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        goto :found_java
    )
)

REM Fallback to PATH java if nothing else was found
where java >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "JAVA_CMD=java"
    goto :found_java
)

echo [ERROR] Java 17+ is not installed on this system!
echo Please install Java from https://adoptium.net/
echo.
pause
exit /b 1

:found_java
REM 2. Locate the JAR file
set "JAR_PATH=bookloop-v3.jar"
if not exist "%JAR_PATH%" (
    if exist "bookloop\target\bookloop-v3.jar" (
        set "JAR_PATH=bookloop\target\bookloop-v3.jar"
    ) else if exist "bookloop\target\bookloop-release.jar" (
        set "JAR_PATH=bookloop\target\bookloop-release.jar"
    ) else (
        echo [ERROR] Could not find bookloop-v3.jar!
        echo Please make sure bookloop-v3.jar is in this folder.
        echo.
        pause
        exit /b 1
    )
)

echo [OK] Java: "%JAVA_CMD%"
echo [OK] App:  "%JAR_PATH%"
echo.
echo Launching BookLoop...

"%JAVA_CMD%" --enable-native-access=ALL-UNNAMED -jar "%JAR_PATH%"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application closed with code %ERRORLEVEL%.
    pause
)
