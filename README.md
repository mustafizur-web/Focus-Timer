<div align="center">

# ⏱️ Focus Timer

**A unified Timer, Stopwatch, and Alarm utility — wrapped in a calm, neumorphic design.**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-24-blue)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](#license)

</div>

---

## ✨ Overview

**Focus Timer** brings together everything you need to manage time in one clean, distraction-free app. Built entirely with **Jetpack Compose** and a soft **neumorphic** design language, it combines a countdown timer, stopwatch, and alarm system with session history — all wrapped in a minimalist, calming interface designed to help you stay focused.

## 📱 Features

- ⏳ **Timer** — Set countdowns for focus sessions, breaks, or tasks
- ⏱️ **Stopwatch** — Track elapsed time with precision
- ⏰ **Alarm** — Schedule exact alarms with a dedicated full-screen ringing experience
- 🕓 **History** — Review past sessions and completed timers
- 🎨 **Soft Neumorphism UI** — A gentle, tactile design system built for calm focus
- 🌗 **Light & Dark Theme** — Adapts to your system appearance
- 🔔 **Reliable Notifications** — Exact alarms with vibration and full-screen alerts, even when the device is locked

## 🖼️ Screens

| Screen | Description |
|---|---|
| `TimerScreen` | Countdown timer with start / pause / reset controls |
| `StopwatchScreen` | Elapsed-time tracking with lap support |
| `AlarmScreen` | Create and manage scheduled alarms |
| `RingingScreen` | Full-screen alarm ringing view with snooze/dismiss |
| `HistoryScreen` | Log of past timer and stopwatch sessions |
| `SettingsScreen` | App preferences and configuration |

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Language | [Kotlin](https://kotlinlang.org) |
| UI Toolkit | [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material 3 |
| Architecture | MVVM (`ViewModel` + `StateFlow`) |
| Local Storage | [Room](https://developer.android.com/training/data-storage/room) |
| Networking | [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) |
| JSON | [Moshi](https://github.com/square/moshi) |
| Async | Kotlin Coroutines |
| Cloud AI | Firebase AI (Gemini API) |
| Navigation | Jetpack Navigation Compose |
| Testing | JUnit, Robolectric, Roborazzi (screenshot testing), Espresso |

## 📂 Project Structure

```
app/src/main/java/com/example/
├── data/
│   ├── alarm/          # Alarm entity, DAO, scheduler & broadcast receiver
│   ├── history/        # Session history entity & DAO
│   └── database/        # Room database setup
├── ui/
│   ├── components/      # Reusable neumorphic UI components
│   ├── screens/          # Timer, Stopwatch, Alarm, History, Settings, Ringing
│   ├── theme/            # Color, typography & theming
│   └── viewmodel/        # FocusViewModel (app state & business logic)
└── MainActivity.kt      # App entry point & navigation host
```

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable release)
- JDK 11+
- An Android device or emulator running **API 24 (Android 7.0)** or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/focus-timer.git
   cd focus-timer
   ```

2. **Open in Android Studio**
   Open the project folder and let Android Studio sync Gradle and resolve dependencies.

3. **Configure your API key**
   Create a `.env` file in the project root (see `.env.example`) and add your Gemini API key:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```

4. **Run the app**
   Select a device or emulator and hit **Run ▶** in Android Studio.

> **Note:** For a release build, replace the debug signing config in `app/build.gradle.kts` with your own signing configuration.

## 🔐 Permissions

| Permission | Purpose |
|---|---|
| `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` | Trigger alarms at precise times |
| `USE_FULL_SCREEN_INTENT` | Show the ringing screen over the lock screen |
| `POST_NOTIFICATIONS` | Display alarm and timer notifications |
| `VIBRATE` | Vibrate on alarm trigger |
| `SYSTEM_ALERT_WINDOW` | Display alerts above other apps |

## 🧪 Testing

The project includes unit tests, instrumented tests, and screenshot tests:

```bash
./gradlew test                # Unit tests (Robolectric + Roborazzi)
./gradlew connectedAndroidTest # Instrumented tests on device/emulator
```

## 🗺️ Roadmap

- [ ] Custom alarm sounds & tones
- [ ] Widget support for Timer & Stopwatch
- [ ] Focus statistics & weekly insights
- [ ] Wear OS companion app

## 🤝 Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ using Jetpack Compose

</div>
