@echo off
setlocal

set FILE=src\main\scala\MutableDB.scala
set INC_OUT=%TEMP%\dottybug_inc.out

echo [repro] clean compile...
call sbtn.bat "clean; compile"
if errorlevel 1 (
    echo [repro] CLEAN COMPILE FAILED -- cannot test
    exit /b 2
)

REM Insert "// repro-trigger" as a second line in MutableDB.scala
powershell -NoProfile -Command "$f='%FILE%'; $c=Get-Content $f; @($c[0], '// repro-trigger') + $c[1..($c.Length-1)] | Set-Content $f"

echo [repro] incremental compile (expecting cyclic error)...
call sbtn.bat compile > "%INC_OUT%" 2>&1
type "%INC_OUT%"

REM Remove the trigger line
powershell -NoProfile -Command "(Get-Content '%FILE%') | Where-Object { $_ -ne '// repro-trigger' } | Set-Content '%FILE%'"

findstr /C:"Cyclic reference involving val <import>" "%INC_OUT%" >nul
if errorlevel 1 goto :fail
findstr /C:"stubs.scala" "%INC_OUT%" >nul
if errorlevel 1 goto :fail

del "%INC_OUT%"
echo [repro] OK -- reproduced cyclic error on incremental
exit /b 0

:fail
del "%INC_OUT%"
echo [repro] FAIL -- bug did not reproduce
exit /b 1
