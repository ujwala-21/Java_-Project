@echo off
setlocal enabledelayedexpansion
title BookLoop - Database Setup Helper
echo ===============================================================================
echo   BookLoop MySQL Database Setup Helper
echo ===============================================================================
echo.

set "MYSQL_CMD=mysql"
where mysql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
        set "MYSQL_CMD=\"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe\""
    ) else if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" (
        set "MYSQL_CMD=\"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe\""
    )
)

echo Found MySQL executable: %MYSQL_CMD%
echo.
echo This script will create 'bookloop_db' and import the schema and seed data.
echo.
set /p MYSQL_PASS="Enter your MySQL root password: "

echo.
echo [1/3] Creating database 'bookloop_db'...
%MYSQL_CMD% -u root -p"%MYSQL_PASS%" -e "CREATE DATABASE IF NOT EXISTS bookloop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo [2/3] Importing schema (tables, constraints, indexes)...
%MYSQL_CMD% -u root -p"%MYSQL_PASS%" bookloop_db < "bookloop\database\schema.sql"

echo [3/3] Importing seed data (students, books, listings, courses)...
%MYSQL_CMD% -u root -p"%MYSQL_PASS%" bookloop_db < "bookloop\database\seed_data.sql"

echo.
echo Updating db.properties with your credentials...
(
echo #BookLoop Database Configuration
echo db.username=root
echo db.password=%MYSQL_PASS%
echo db.pool.size=10
echo db.url=jdbc:mysql://localhost:3306/bookloop_db?useSSL=false^&serverTimezone=UTC^&allowPublicKeyRetrieval=true
) > db.properties

echo.
echo ===============================================================================
echo   Database setup completed successfully!
echo   You can now click "Retry Connection" on the BookLoop window on your screen!
echo ===============================================================================
echo.
pause
