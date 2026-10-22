@echo off
REM ---------------------------------------------------------------
REM  Start het damspel.
REM  Dubbelklik op dit bestand, of typ "spelen.bat" in een terminal.
REM  Enkel Java 21 is nodig: Maven en JavaFX worden de eerste keer
REM  vanzelf gedownload (dat duurt even).
REM ---------------------------------------------------------------
setlocal
cd /d "%~dp0"

where java >nul 2>&1
if not %errorlevel%==0 if not defined JAVA_HOME (
    echo Java werd niet gevonden op deze computer.
    echo Installeer Java 21 of hoger, bijvoorbeeld van https://adoptium.net
    echo.
    pause
    exit /b 1
)

echo Het damspel wordt opgestart...
call mvnw.cmd -q javafx:run
if not %errorlevel%==0 (
    echo.
    echo Het spel kon niet gestart worden. Kijk hierboven naar de foutmelding.
    pause
)
