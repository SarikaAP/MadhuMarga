# MadhuMarga

MadhuMarga is an Android application built using modern Android development tools and practices. The app leverages Jetpack Compose for its UI, Room for local data persistence, and Firebase for cloud-based services.

## Features

- **Modern UI**: Built entirely with Jetpack Compose.
- **Local Persistence**: Uses Room Database for efficient local data storage.
- **Cloud Integration**: 
    - Firebase Realtime Database and Firestore for data synchronization.
    - Firebase Analytics for usage insights.
- **Navigation**: Implements Navigation Compose for seamless screen transitions.
- **Reactive Programming**: Utilizes Kotlin Coroutines and Flow.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: 
    - Local: Room
    - Cloud: Firebase Realtime Database & Cloud Firestore
- **Dependency Injection**: (Standard Android practices)
- **Build System**: Gradle Kotlin DSL

## Getting Started

### Prerequisites

- Android Studio Koala or newer.
- Android SDK 35 (Compile SDK).
- A Firebase project to obtain `google-services.json`.

### Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   ```

2. **Add Firebase Configuration**:
   - Place your `google-services.json` file in the `app/` directory.

3. **Build the project**:
   - Open the project in Android Studio.
   - Sync the project with Gradle files.
   - Run the app on an emulator or a physical device.

## Project Structure

- `app/src/main/java`: Contains the Kotlin source code.
- `app/src/main/res`: Contains Android resource files.
- `app/build.gradle.kts`: Module-level build configuration.
- `build.gradle.kts`: Project-level build configuration.

## License

This project is for educational/development purposes.
