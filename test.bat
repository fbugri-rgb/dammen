@echo off
REM ---------------------------------------------------------------
REM  Voert alle testen van het damspel in een keer uit.
REM  Dubbelklik op dit bestand, of typ "test.bat" in een terminal.
REM  Alle resultaten verschijnen hieronder op het scherm.
REM ---------------------------------------------------------------
setlocal
cd /d "%~dp0"

echo Bezig met het uitvoeren van alle testen...
echo.

REM 1. Staat Maven gewoon in het PATH?
where mvn >nul 2>&1
if %errorlevel%==0 (
    call mvn test
    goto resultaat
)

REM 2. Zo niet: de Maven gebruiken die bij IntelliJ geleverd wordt.
for %%m in (
    "%LOCALAPPDATA%\Programs\IntelliJ IDEA Ultimate"
    "%LOCALAPPDATA%\Programs\IntelliJ IDEA Community Edition"
    "%ProgramFiles%\JetBrains\IntelliJ IDEA Ultimate"
    "%ProgramFiles%\JetBrains\IntelliJ IDEA Community Edition"
) do (
    if exist "%%~m\plugins\maven\lib\maven3\bin\mvn.cmd" (
        call "%%~m\plugins\maven\lib\maven3\bin\mvn.cmd" test
        goto resultaat
    )
)

echo.
echo Maven werd niet gevonden op deze computer.
echo Voer de testen uit vanuit IntelliJ: kies "Alle testen" bovenaan en klik op de groene pijl.
goto einde

:resultaat
set UITKOMST=%errorlevel%
echo.
echo ===============================================================
if "%UITKOMST%"=="0" (
    echo   ALLE TESTEN ZIJN GESLAAGD
) else (
    echo   ER ZIJN TESTEN MISLUKT - kijk hierboven bij [ERROR] welke
)
echo ===============================================================

:einde
echo.
pause
