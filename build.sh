#!/bin/bash

echo "Building Kodi Remote Bridge Debug APK..."
echo ""

# Check if Android SDK is configured
if [ ! -f local.properties ]; then
    echo "ERROR: local.properties not found"
    echo "Please configure your Android SDK path in local.properties"
    echo "Example: sdk.dir=/home/yourname/Android/Sdk"
    exit 1
fi

# Build debug APK
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful!"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo ""
    echo "Build failed. Please check the error messages above."
    exit 1
fi
