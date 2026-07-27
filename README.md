# GT LIVE Esports - Android Studio Project (Kotlin + Jetpack Compose)

GT LIVE is a full-featured, high-performance Android mobile application engineered for live streaming mobile esports gameplay and camera feeds directly to **YouTube Live** via RTMP and YouTube Live Streaming API v3.

## Features & Architecture:
- **Jetpack Compose UI**: Modern declarative UI with reactive state management.
- **CameraX API**: Ultra-low latency camera capture with dual-facing camera toggle, torch, and 1080p60 encoding.
- **MediaProjection Service**: Foreground Service capturing full mobile screen during high-FPS game streams (PUBG, Free Fire, Mobile Legends, Valorant Mobile).
- **RTMP Stream Engine**: H.264 video + AAC audio compression pipeline with adaptive bit-rate adjustment (3000 Kbps - 12000 Kbps).
- **YouTube Live API v3**: Automatic live broadcast creation, stream key ingestion, privacy setting toggles, and live viewer metrics synchronization.
- **Firebase Auth & Firestore**: Google OAuth sign-in and real-time live chat room synchronization.

## How to Build in Android Studio:
1. Clone this repository or download the source code.
2. Launch **Android Studio** (2023.3+ / Iguana, Jellyfish, Koala).
3. Select **File -> Open** and navigate to this folder.
4. Android Studio will automatically recognize `build.gradle.kts` and `settings.gradle.kts` and start Gradle sync.
5. Connect an Android phone via USB (with USB Debugging enabled) or start an Android Emulator (API 26+ / Android 8.0+).
6. Click **Run 'app'** (`Shift + F10`).
