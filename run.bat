@echo off
set MAIN_CLASS=com.fittrack.Main
set SRC_DIR=src\main\java
set OUT_DIR=out
set LIB_DIR=lib
set SQLITE_JAR=%LIB_DIR%\sqlite-jdbc.jar
set SLF4J_API=%LIB_DIR%\slf4j-api.jar
set SLF4J_SIMPLE=%LIB_DIR%\slf4j-simple.jar
set CP=%OUT_DIR%;%SQLITE_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%

echo ====================================
echo   FitTrack CLI - Build Script
echo ====================================

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found. Install JDK 17+ from https://adoptium.net
    pause & exit /b 1
)

if not exist "%SQLITE_JAR%" (
    echo Downloading sqlite-jdbc.jar...
    curl -L -o "%SQLITE_JAR%" "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar"
)
if not exist "%SLF4J_API%" (
    echo Downloading slf4j-api.jar...
    curl -L -o "%SLF4J_API%" "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar"
)
if not exist "%SLF4J_SIMPLE%" (
    echo Downloading slf4j-simple.jar...
    curl -L -o "%SLF4J_SIMPLE%" "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar"
)

mkdir %OUT_DIR% 2>nul
dir /s /b %SRC_DIR%\*.java > sources.txt
javac -cp "%SQLITE_JAR%" -d %OUT_DIR% @sources.txt
del sources.txt

if errorlevel 1 ( echo BUILD FAILED. & pause & exit /b 1 )
echo Build successful!

mkdir reports 2>nul
java -cp "%CP%" %MAIN_CLASS%
pause
