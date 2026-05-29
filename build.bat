@echo off
echo Building Kodi Remote Bridge Debug APK...
echo.

REM Check if Android SDK is configured
if not exist local.properties (
    echo ERROR: local.properties not found
    echo Please configure your Android SDK path in local.properties
    echo Example: sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
    pause
    exit /b 1
)

REM Build debug APK
call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful!
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo Build failed. Please check the error messages above.
)

pause
