@echo off
set JAVA_HOME_BEFORE=%JAVA_HOME%
for /D %%F in (*) do (
    if exist %%F\bin\java.exe (
        set JAVA_HOME=%CD%\%%F
    )
)
SET ABAKUS_ERROR=0
if "%JAVA_HOME%"=="" (
    echo FEHLER: Konnte kein Java finden.
    SET ABAKUS_ERROR=1
)
if not exist ".\bin\AbakusFx.bat" (
    echo FEHLER: Konnte Abakus-Starter nicht finden.
    SET ABAKUS_ERROR=1
)
if %ABAKUS_ERROR% gtr 0 (
    exit /b 1
)
echo Benutze Java in %JAVA_HOME%
.\bin\AbakusFx.bat
set JAVA_HOME=%JAVA_HOME_BEFORE%
