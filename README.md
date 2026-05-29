# Kodi Remote Bridge

A fully-featured Android remote control application for Kodi media center, enabling you to control your Kodi instance from your Android device.

## Features

### Core Functionality
- **WebSocket Connection**: Real-time bidirectional communication with Kodi via JSON-RPC
- **Remote Control**: Custom D-pad with gesture support for navigation
- **Playback Controls**: Play, pause, stop, rewind, forward, and seek functionality
- **Volume Control**: Volume slider with mute toggle
- **Media Info**: Display currently playing media with progress tracking
- **Library Browsing**: Browse movies and TV shows from your Kodi library
- **System Controls**: Power menu with shutdown, reboot, suspend options
- **Connection Management**: Save and manage multiple Kodi connections
- **Background Service**: Maintain connection even when app is in background

### Technical Features
- **Kotlin**: Modern Android development with Kotlin
- **Hilt**: Dependency injection for clean architecture
- **Coroutines**: Asynchronous operations for smooth UI
- **Material Design**: Beautiful dark theme UI
- **Custom Views**: Custom D-pad and volume slider components
- **WebSocket**: Real-time communication with Kodi
- **JSON-RPC**: Full Kodi API implementation

## Requirements

- Android 7.0 (API 24) or higher
- Kodi with JSON-RPC enabled (default port 9090)
- Network connection to Kodi instance

## Installation

### Building from Source

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd kodi-remote-bridge
   ```

2. **Configure Android SDK**
   
   Create or edit `local.properties`:
   ```properties
   sdk.dir=/path/to/your/Android/Sdk
   ```
   
   On Windows:
   ```properties
   sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
   ```

3. **Build the debug APK**
   
   On Windows:
   ```bash
   build.bat
   ```
   
   On Linux/Mac:
   ```bash
   chmod +x build.sh
   ./build.sh
   ```

   Or manually:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install the APK**
   
   The debug APK will be located at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```
   
   Install via ADB:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Using the Pre-built APK

A pre-built debug APK is included in the repository for quick testing:

1. Download `app-debug.apk` from the repository
2. Enable "Unknown sources" in your Android device settings
3. Install the APK

## Configuration

### Kodi Setup

1. **Enable JSON-RPC in Kodi**
   - Go to Settings → Services → Remote control
   - Enable "Allow remote control via HTTP"
   - Enable "Allow remote control via WebSocket"
   - Note the port (default: 9090)

2. **Find Kodi IP Address**
   - In Kodi: Settings → System info → Network
   - Note the IP address

### App Configuration

1. **Add Connection**
   - Open the app
   - Tap the connection button
   - Enter connection details:
     - Name: e.g., "Living Room Kodi"
     - Host: Kodi IP address
     - Port: 9090 (default)
     - Username/Password: Optional if Kodi requires authentication

2. **Test Connection**
   - Use the "Test Connection" button to verify connectivity
   - The app will attempt to connect to your Kodi instance

3. **Connect**
   - Select your connection and tap "Connect"
   - The app will establish a WebSocket connection

## Usage

### Remote Control

- **D-Pad**: Use the on-screen D-pad for navigation
- **Center Button**: Select (short press), Menu (long press)
- **Playback Controls**: Play/Pause, Stop, Rewind, Forward
- **Volume Slider**: Drag to adjust volume, tap mute button
- **Navigation Buttons**: Back, Home, Info, Menu

### Media Control

- **Now Playing**: View current media information
- **Progress Bar**: Seek to different positions
- **Time Display**: Current time and total duration

### Library Browsing

- **Movies**: Browse your movie library
- **TV Shows**: Browse your TV show collection
- **Playback**: Start playback directly from the app

### System Controls

- **Power Menu**: Access system power options
- **Shutdown**: Safely shutdown Kodi system
- **Reboot**: Reboot the Kodi system
- **Suspend**: Put system to sleep

## Architecture

### Project Structure

```
app/src/main/java/com/kodiremote/bridge/
├── api/                    # Kodi API communication
│   ├── KodiJsonRpcClient.kt    # WebSocket client
│   └── KodiApi.kt               # High-level API methods
├── data/                   # Data models
│   ├── KodiConnection.kt       # Connection configuration
│   ├── MediaItem.kt            # Media information
│   └── PlayerState.kt          # Playback state
├── di/                     # Dependency injection
│   └── AppModule.kt            # Hilt modules
├── service/                # Background services
│   └── KodiConnectionService.kt # Foreground service
├── ui/                     # UI components
│   ├── MainActivity.kt          # Main activity
│   ├── ConnectionDialog.kt     # Connection management
│   ├── RemoteControlView.kt    # Custom D-pad
│   └── VolumeSliderView.kt     # Custom volume slider
├── utils/                  # Utilities
└── viewmodel/             # ViewModels
    └── MainViewModel.kt        # Main screen logic
```

### Key Components

- **KodiJsonRpcClient**: Handles WebSocket communication with Kodi
- **KodiApi**: Provides high-level methods for Kodi operations
- **MainViewModel**: Manages UI state and business logic
- **Custom Views**: D-pad and volume slider for better UX
- **Foreground Service**: Maintains connection in background

## Dependencies

- **AndroidX**: Core Android components
- **Material Design**: UI components
- **Hilt**: Dependency injection
- **Coroutines**: Asynchronous programming
- **Gson**: JSON parsing
- **OkHttp**: HTTP client
- **Java-WebSocket**: WebSocket client
- **Glide**: Image loading

## Troubleshooting

### Connection Issues

1. **Cannot connect to Kodi**
   - Verify Kodi is running
   - Check network connectivity
   - Ensure JSON-RPC is enabled in Kodi
   - Verify correct IP address and port
   - Check firewall settings

2. **Connection drops frequently**
   - Check network stability
   - Ensure device doesn't go into deep sleep
   - Check power saving settings

3. **Controls not responding**
   - Verify connection is active
   - Check Kodi is not frozen
   - Restart the app if needed

### Build Issues

1. **Gradle sync fails**
   - Check Android SDK path in local.properties
   - Ensure correct Gradle version
   - Update Android Studio if needed

2. **Build fails**
   - Clean build: `./gradlew clean`
   - Check for dependency conflicts
   - Ensure sufficient disk space

## Development

### Building Release APK

```bash
./gradlew assembleRelease
```

The release APK will be signed with your debug keystore by default. For production, configure signing in `app/build.gradle`.

### Running Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Code Style

The project follows Kotlin coding conventions. Use Android Studio's built-in formatter.

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Kodi JSON-RPC API

This app uses the Kodi JSON-RPC API. For more information:
- [Kodi JSON-RPC API Documentation](https://kodi.wiki/view/JSON-RPC_API)
- [Kodi WebSocket API](https://kodi.wiki/view/JSON-RPC_API/WebSocket)

## Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Check existing issues for solutions
- Refer to Kodi documentation for API questions

## Changelog

### Version 1.0.0
- Initial release
- Basic remote control functionality
- WebSocket connection
- Playback controls
- Volume control
- Library browsing
- System controls
- Connection management
- Background service

## Acknowledgments

- Kodi development team for the excellent JSON-RPC API
- Material Design team for UI guidelines
- Open source community for various libraries
