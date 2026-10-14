# CloudStream Android Replica

A clean-room, open-source Android media streaming client scaffold inspired by CloudStream 3.

## Architecture & Tech Stack

- **UI & Architecture:** Jetpack Compose (Material 3) with Unidirectional Data Flow / MVI.
- **Navigation:** Type-Safe Compose Navigation (`kotlinx.serialization`).
- **Media Playback:** AndroidX Media3 (`MediaSessionService` + ExoPlayer).
- **Background Tasks:** Android `WorkManager` for chunked, resilient downloads.
- **Database:** Room persistence library for watch history, cached metadata, and download tracking.
- **Network:** OkHttp 4.x for streaming extraction and range requests.

## Project Structure

```text
├── .github/workflows/android.yml     # Automated Gradle build & test workflow
├── app/
│   ├── build.gradle.kts              # Module dependencies & Compose setup
│   └── src/main/
│       ├── AndroidManifest.xml       # Foreground services, permissions & declarations
│       ├── java/com/replica/
│       │   ├── core/
│       │   │   ├── database/         # Room entities & DAOs (History, Downloads, Providers)
│       │   │   ├── download/         # WorkManager DownloadSegmentWorker
│       │   │   └── navigation/       # Type-safe Route contracts
│       │   ├── feature/
│       │   │   ├── details/          # Media details UI, ViewModel (MVI), State contracts
│       │   │   └── feed/             # Catalog discovery screen
│       │   ├── playback/             # Media3 MediaSessionService implementation
│       │   ├── MainActivity.kt       # Root activity & Compose entrypoint
│       │   └── ReplicaApp.kt         # Application base class
│       └── res/                      # Values, themes, strings
├── replica/
│   ├── recon.md                      # Reverse-engineering recon map & system design
│   └── features.csv                  # MoSCoW feature prioritization matrix
├── build.gradle.kts                  # Root project build configuration
└── settings.gradle.kts               # Gradle settings & plugin repositories
```

## Getting Started

1. Unzip the archive.
2. Open the `cloudstream-replica` directory in **Android Studio Hedgehog / Ladybug** or newer.
3. Allow Gradle to sync.
4. Run on an emulator or physical device running Android 8.0 (API 26) or higher.
