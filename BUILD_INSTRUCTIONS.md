# Build Instructions for Kodi Remote Bridge

## Prerequisites

Before building the APK, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/

2. **Android SDK**
   - Install Android Studio: https://developer.android.com/studio
   - Or install Android SDK separately

3. **Gradle** (optional - the project includes Gradle wrapper)
   - Download from: https://gradle.org/install/

## Quick Build with Android Studio

1. **Open Project**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `kodi-remote-bridge` directory
   - Wait for Gradle sync to complete

2. **Build APK**
   - Go to Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Wait for build to complete
   - Click "locate" in the notification to find the APK

3. **Output Location**
   - Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
   - Release APK: `app/build/outputs/apk/release/app-release.apk`

## Command Line Build

### Using Gradle Wrapper (Recommended)

The project includes Gradle wrapper scripts. First, you need to initialize the wrapper:

```bash
cd kodi-remote-bridge

# If you have Gradle installed globally
gradle wrapper

# Or use Android Studio's Gradle
# Open the project in Android Studio first, then:
./gradlew assembleDebug  # Linux/Mac
gradlew.bat assembleDebug  # Windows
```

### Using System Gradle

If you have Gradle installed:

```bash
cd kodi-remote-bridge
gradle assembleDebug
```

## Manual Gradle Wrapper Setup

If the Gradle wrapper is not properly set up:

1. **Install Gradle** (if not already installed)
2. **Navigate to project directory**
   ```bash
   cd kodi-remote-bridge
   ```
3. **Generate wrapper**
   ```bash
   gradle wrapper --gradle-version 8.2
   ```
4. **Build APK**
   ```bash
   ./gradlew assembleDebug  # Linux/Mac
   gradlew.bat assembleDebug  # Windows
   ```

## Build Variants

### Debug APK
```bash
./gradlew assembleDebug
```
- Unsigned and unoptimized
- Includes debug information
- Larger file size
- For testing only

### Release APK
```bash
./gradlew assembleRelease
```
- Optimized and minified
- Smaller file size
- Requires signing configuration
- For distribution

## Troubleshooting

### Gradle wrapper not found
- Generate wrapper: `gradle wrapper --gradle-version 8.2`
- Or install Gradle globally and use `gradle` commands

### Android SDK not found
- Create/edit `local.properties`:
  ```properties
  sdk.dir=/path/to/your/Android/Sdk
  ```

### Build fails with dependency errors
- Clean build: `./gradlew clean`
- Refresh dependencies: `./gradlew build --refresh-dependencies`

### Java version mismatch
- Ensure JDK 17 or higher is installed
- Set JAVA_HOME environment variable
- Check with: `java -version`

## Installing the APK

### Using ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Manual Installation
1. Copy APK to your Android device
2. Enable "Unknown sources" in device settings
3. Open the APK file to install

## System Requirements

- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Compile SDK**: 34
- **Java Version**: 17
- **Gradle Version**: 8.2
