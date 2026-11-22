# ZOLVS V1 — Android Non-Root starter

## Requirements
- Android Studio
- JDK 17
- Android SDK Platform 35
- Android NDK and CMake 3.22.1

## Build
1. Open this folder in Android Studio.
2. Allow Gradle sync to finish.
3. Install the requested SDK/NDK components if Android Studio prompts.
4. Select `app` and run on an Android 10+ device, or use Build > Build APK(s).

## Included
- Kotlin launcher activity
- User-approved Android document picker for an ELF file
- JNI + C++ ELF64 header inspection
- ARM64 ABI filter
- User-enabled floating overlay with draggable panel and local FOV/Range demo sliders

## Notes
- The overlay sliders are UI-only local demo values. They do not modify a game.
- The analyzer reads only the file the user explicitly selects through Android's Storage Access Framework.
- This starter does not attach to, read, or modify another app's process memory.
- `compileSdk 35` is used for broad compatibility; Android 16 can run apps targeting API 35, subject to platform compatibility behavior.
