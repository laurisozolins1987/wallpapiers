# Wallpapiers

Android wallpaper browsing app built with Kotlin, Retrofit, Glide, Navigation, and ViewBinding.

## What it does

- Shows a fast local curated fallback catalog so the app is never empty.
- Pulls extra wallpapers from Pixabay and, when configured, Pexels.
- Supports category browsing, search, detail view, downloads, and setting a wallpaper.
- Remembers lightweight personalization signals for ranking.

## Setup

1. Open the project in Android Studio.
2. Add your API keys to `local.properties`:

```properties
PIXABAY_API_KEY=your_pixabay_key
PEXELS_API_KEY=your_pexels_key
```

`PIXABAY_API_KEY` is enough for a usable build. Without API keys the app still shows the local curated catalog.

## Build

```bash
./gradlew :app:assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
