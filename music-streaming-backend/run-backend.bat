@echo off
cls
echo ========================================
echo  Music Streaming Backend Launcher
echo ========================================
echo.

REM Check Java installation
echo Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)
echo [OK] Java is installed
echo.

REM Check for Maven in PATH
echo Checking for Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [WARN] Maven not found in PATH
    echo.
    goto :TryWrapper
) else (
    echo [OK] Maven found
    echo.
    echo Starting application with Maven...
    mvn spring-boot:run
    goto :End
)

:TryWrapper
echo Trying Maven Wrapper...
if exist "mvnw.cmd" (
    echo [OK] Maven wrapper found
    echo.
    echo Attempting to run with Maven wrapper...
    echo (This requires PowerShell to be available)
    echo.
    mvnw.cmd spring-boot:run
    if errorlevel 1 (
        echo.
        echo [ERROR] Maven wrapper failed
        goto :ShowInstructions
    )
) else (
    echo [WARN] Maven wrapper not found
    goto :ShowInstructions
)
goto :End

:ShowInstructions
echo.
echo ========================================
echo  CANNOT RUN - PLEASE USE AN IDE
echo ========================================
echo.
echo Maven is required to run this Spring Boot application.
echo Since Maven is not available, please use one of these methods:
echo.
echo METHOD 1: Using Spring Tool Suite (STS) - RECOMMENDED
echo ---------------------------------------------------
echo 1. Open STS from: D:\reqsofts\Java\sts-4.30.0.RELEASE
echo 2. Import project: File ^> Import ^> Maven ^> Existing Maven Projects
echo 3. Browse to: %CD%
echo 4. Right-click project ^> Run As ^> Spring Boot App
echo.
echo METHOD 2: Install Maven
echo ---------------------------------------------------
echo 1. Download from: https://maven.apache.org/download.cgi
echo 2. Extract and add bin folder to PATH
echo 3. Restart command prompt and run this script again
echo.
echo METHOD 3: Enable PowerShell (for Maven Wrapper)
echo ---------------------------------------------------
echo 1. PowerShell should be installed on Windows 10/11
echo 2. If disabled, enable it through Windows Features
echo 3. Run: mvnw.cmd spring-boot:run
echo.
echo For more details, see README_RUN.md
echo.
pause
exit /b 1

:End
echo.
echo Application stopped.
pause
