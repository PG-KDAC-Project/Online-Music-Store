@echo off
REM This script adds PowerShell to PATH temporarily and runs the Maven wrapper

echo Adding PowerShell to PATH...
set PATH=%SystemRoot%\System32\WindowsPowerShell\v1.0;%PATH%

echo Starting Music Streaming Backend with Maven Wrapper...
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause
