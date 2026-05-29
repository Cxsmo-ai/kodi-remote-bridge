# Kodi Remote Bridge - Setup Guide

This guide will help you set up and build the Kodi Remote Bridge Android application.

## Prerequisites

### Required Software

1. **Java Development Kit (JDK)**
   - JDK 17 or higher
   - Download from: https://www.oracle.com/java/technologies/downloads/

2. **Android SDK**
   - Android Studio includes the Android SDK
   - Download from: https://developer.android.com/studio
   - Minimum SDK version: API 24 (Android 7.0)
   - Target SDK version: API 34 (Android 14)

3. **Git**
   - For cloning the repository
   - Download from: https://git-scm.com/downloads

### Optional Software

- **Android Studio**: For development and debugging
- **ADB**: For installing APK to connected devices

## Initial Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd kodi-remote-bridge
```

### 2. Configure Android SDK Path

Create or edit the `local.properties` file in the project root:

**Windows:**
```properties
sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

**Linux/Mac:**
```properties
sdk.dir=/home/yourname/Android/Sdk
```

**Finding your Android SDK path:**
- In Android Studio: File → Settings → Appearance & Behavior → System Settings → Android SDK
- Or check default locations:
  - Windows: `C:\Users\YourName\AppData\Local\Android\Sdk`
  - Mac: `~/Library/Android/sdk`
  - Linux: `~/Android/Sdk`

### 3. Verify Gradle Wrapper

The project includes Gradle wrapper files. If you need to regenerate them:

```bash
gradle wrapper --gradle-version 8.2
```

## Building the Project

### Using Build Scripts

**Windows:**
```bash
build.bat
```

**Linux/Mac:**
```bash
chmod +x build.sh
./build.sh
```

### Manual Build

```bash
# Give execute permission to gradlew (Linux/Mac only)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean
```

## Output Locations

After building, the APK files will be located at:

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`

## Installing the APK

### Using ADB

1. Enable USB debugging on your Android device
2. Connect device via USB
3. Install the APK:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Manual Installation

1. Copy the APK to your device
2. Enable "Unknown sources" in device settings
3. Open the APK file to install

## Kodi Configuration

### Enable JSON-RPC in Kodi

1. Open Kodi
2. Go to **Settings** → **Services** → **Remote control**
3. Enable **"Allow remote control via HTTP"**
4. Enable **"Allow remote control via WebSocket"**
5. Note the port (default: 9090)
6. Optionally set username/password for authentication

### Find Kodi IP Address

1. In Kodi: **Settings** → **System info** → **Network**
2. Note the IP address displayed

### Test Connection

You can test the connection using a web browser:

```
http://<kodi-ip>:<port>/jsonrpc
```

Example:
```
http://192.168.1.100:9090/jsonrpc
```

## App Configuration

### First Launch

1. Open the Kodi Remote Bridge app
2. Tap the connection button (disconnected)
3. Tap "Add" to create a new connection
4. Enter connection details:
   - **Name**: e.g., "Living Room Kodi"
   - **Host**: Kodi IP address (e.g., 192.168.1.100)
   - **Port**: 9090 (default)
   - **Username/Password**: Optional if Kodi requires authentication
5. Tap "Test Connection" to verify
6. Tap "Connect" to establish connection

### Multiple Connections

You can save multiple Kodi connections:
- Different rooms
- Different Kodi instances
- Test and production setups

## Troubleshooting

### Build Issues

**Gradle sync fails:**
- Verify Android SDK path in `local.properties`
- Check internet connection (Gradle needs to download dependencies)
- Try: `./gradlew clean build --refresh-dependencies`

**Compilation errors:**
- Ensure JDK 17 or higher is installed
- Check JAVA_HOME environment variable
- Update Android Studio to latest version

**Missing dependencies:**
- Run: `./gradlew build --refresh-dependencies`
- Check your internet connection

### Connection Issues

**Cannot connect to Kodi:**
- Verify Kodi is running
- Check network connectivity
- Ensure JSON-RPC is enabled in Kodi
- Verify correct IP address and port
- Check firewall settings on both devices
- Try pinging the Kodi IP from your device

**Connection drops:**
- Check network stability
- Ensure device doesn't go into deep sleep
- Check power saving settings
- Move closer to Wi-Fi router if using wireless

**Controls not responding:**
- Verify connection is active
- Check Kodi is not frozen
- Restart the app if needed
- Check Kodi logs for errors

### Device Issues

**App crashes on launch:**
- Check Android version (minimum 7.0)
- Clear app data and cache
- Reinstall the app
- Check device storage space

**APK won't install:**
- Enable "Unknown sources" in device settings
- Check if device has sufficient storage
- Verify Android version compatibility
- Try uninstalling previous version first

## Development Setup

### Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `kodi-remote-bridge` directory
4. Wait for Gradle sync to complete
5. Run the app on emulator or connected device

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Specific test class
./gradlew test --tests com.kodiremote.bridge.api.KodiApiTest
```

### Code Style

The project uses Kotlin coding conventions. Format code with:
- Android Studio: Code → Reformat Code
- Or use ktlint: `./gradlew ktlintFormat`

## Network Configuration

### Local Network

For best performance, ensure your Android device and Kodi are on the same local network.

### Firewall

If you have firewall software, ensure it allows:
- Outbound connections to Kodi IP
- Inbound connections from Kodi IP
- Port 9090 (or your configured port)

### Static IP

For reliable connections, consider setting a static IP for your Kodi device in your router settings.

## Advanced Configuration

### Custom Port

If you changed the default Kodi port:
1. Update the port in the app connection settings
2. Ensure your firewall allows the custom port

### Authentication

If Kodi requires authentication:
1. Set username/password in Kodi settings
2. Enter the same credentials in the app connection settings
3. Ensure HTTP authentication is enabled in Kodi

### SSL/TLS

For secure connections (advanced):
- Configure Kodi to use HTTPS
- Update the app to use secure WebSocket (wss://)
- Handle SSL certificates properly

## Support

For additional help:
- Check the main README.md
- Open an issue on GitHub
- Refer to Kodi documentation: https://kodi.wiki/view/JSON-RPC_API
