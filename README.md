# Safety Companion App (Disguised as Calculator) �️�

> **A stealthy, comprehensive personal safety application designed to protect users in emergency situations while maintaining complete privacy.**

---

## � Overview

The **Safety Companion App** operates under the guise of a fully functional calculator to ensure the user's safety tools remain undetected by potential aggressors. Beneath the surface, it powers a robust suite of emergency features including real-time journey monitoring, automated SOS triggers, audio evidence collection, and a realistic fake call system.

The app uses intelligent algorithms to detect anomalies during travel (unexpected stops, route deviations) and proactively alerts emergency contacts if the user fails to confirm their safety.

---

## ✨ Key Features

### 1. **Stealth Interface** 🕵️‍♀️
- **Disguise**: App Name and Icon mimick a standard "Calculator".
- **Privacy First**: The safety dashboard is hidden behind the utility interface.

### 2. **Smart Journey Monitoring** 📍
- **Live Route Tracking**: Monitors travel from origin to destination using Google Maps API.
- **Traffic Analysis**: Accounts for real-time traffic delays to prevent false alarms.
- **Soft Alert System**:
    - Detects route deviations or significant delays.
    - Triggers a local "Are you safe?" check-in.
    - Sends a preliminary SMS to contacts: *"User is delayed. Checking status..."*
- **Hard Escalation**: Automatically upgrades to a full SOS if the user does not respond within **30 seconds**.

### 3. **Emergency SOS System** 🚨
- **Triggers**:
    - **Hardware**: Volume Up -> Down -> Up sequence (works when screen is locked).
    - **Shake**: Vigorously shake the phone.
    - **In-App**: Dedicated SOS button.
- **Actions**:
    - Sends immediate SMS with live location link to all emergency contacts.
    - Initiates background audio recording.
    - Triggers a fake incoming call (optional distraction).

### 4. **Audio Evidence Manager** 🎙️
- **Auto-Recording**: Captures audio during any SOS event.
- **Secure Storage**: Recordings are saved locally in the app's private cache.
- **Share**: One-tap sharing of evidence via secure file providers (WhatsApp, Email, etc.).

### 5. **Realistic Fake Call** 📞
- **Immersive UI**: Simulates a native Android incoming call screen (User: "Mom", "Police").
- **Interactive**: Answer to start a timer or Decline to exit.
- **Ringtone**: Plays the device's default ringtone.

---

## 🛠️ Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Persistence Library (SQLite)
- **Concurrency**: Kotlin Coroutines & Flow
- **Background Processing**: Foreground Services & BroadcastReceivers
- **APIs**: Google Maps SDK, Directions API
- **Hardware**: SensorManager (Shake), AudioManager (Volume Trigger)

---

## 📂 Project Structure

```text
com.safety.app
├── data                 # Data Layer
│   ├── api              # Retrofit Services (Directions API)
│   ├── db               # Room Database & Entities
│   │   ├── entities     # User, EmergencyContact, LocationLog
│   │   └── AppDatabase  # DB Configuration
│   └── repository       # Single Source of Truth
├── logic                # Core Business Logic
│   ├── JourneyMonitoringService.kt  # Background Service for Tracking & Alerts
│   └── RouteMonitor.kt              # Deviation Detection Logic
├── ui                   # User Interface
│   ├── DashboardActivity.kt         # Main Hub
│   ├── SafeJourneyActivity.kt       # Map & Journey Input
│   ├── FakeCallActivity.kt          # Realistic Call Screen
│   ├── AudioRecordingsActivity.kt   # file Manager
│   └── adapters/                    # RecyclerView Adapters
└── utils                # Utility Classes
    ├── LocationHelper.kt            # GPS Management
    └── NetworkUtils.kt              # Connectivity Checks
```

---

## � Getting Started

### Prerequisites
- **Android Studio**: Iguana or later.
- **Android SDK**: Min SDK 24 (Android 7.0).
- **Google Maps API Key**: Enabled for *Maps SDK for Android* and *Directions API*.

### Installation & Local Run
1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/your-repo/safety-app.git
    ```
2.  **Configure API Key**:
    - Open `local.properties` (or `AndroidManifest.xml`).
    - Add your key: `MAPS_API_KEY=AIzaSy...`
    - Update `API_KEY` in `SafeJourneyActivity.kt` and `JourneyMonitoringService.kt`.
3.  **Build Project**:
    - Sync Gradle files to download dependencies.
4.  **Run on Device**:
    - Connect a physical Android device (Recommended for Location/Sensor testing).
    - Run the `app` configuration.

### ⚠️ Test Mode Configuration
> **Note**: The app is currently set to **DEV MODE** for rapid testing.

- **Forced Alert**: A simulated "Delay" alert triggers **10 seconds** after starting ANY journey.
- **Escalation**: SOS auto-triggers **20 seconds** after the warning.
- **Disable Test Mode**:
    - Go to `JourneyMonitoringService.kt`.
    - Remove the `TEST MODE` block inside `startMonitoring()`.

---

## � Future Scope

- [ ] **Cloud Sync**: Backup recordings and logs to a secure cloud server.
- [ ] **Geofencing**: Automatic alerts when entering known high-risk zones.
- [ ] **Voice Activation**: "Help" keyword detection using Speech Recognition.
- [ ] **Live Streaming**: Stream video/audio directly to contacts in SOS mode.
- [ ] **WearOS Integration**: Trigger SOS from a smartwatch.

---

## � License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
