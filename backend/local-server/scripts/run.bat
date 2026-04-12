@echo off
setlocal

set "APP=local-server.exe"
set "UPDATES_DIR=updates"

:loop
"%APP%"
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="80" goto :done

echo Update detected, applying...

for %%F in (%UPDATES_DIR%\local-server-*) do (
    move /Y "%%F" "%APP%"
    goto :applied
)

echo No update binary found in %UPDATES_DIR%.
goto :done

:applied
echo Update applied, restarting...
goto :loop

:done
echo local-server exited with code %EXIT_CODE%.
exit /b %EXIT_CODE%
