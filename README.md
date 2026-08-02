# 🏍️ MotoBook — AI-Powered Smart Motorcycle Management & Tracker

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Gemini AI](https://img.shields.io/badge/AI-Gemini%20Flash-8E75FF.svg?style=flat&logo=google&logoColor=white)](https://ai.google.dev/)
[![Google Drive](https://img.shields.io/badge/Cloud-Google%20Drive%20Sync-4285F4.svg?style=flat&logo=googledrive&logoColor=white)](https://www.google.com/drive/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**MotoBook** is the ultimate, modern motorcycle companion app for riders who care about peak performance, effortless maintenance tracking, and smart vehicle management. 

Powered by **Gemini AI** and seamless **Google Drive Cloud Sync**, MotoBook eliminates tedious manual data entry. Simply pick your bike's brand and model, and MotoBook automatically fetches official factory specifications, engine details, fuel tank capacities, tire pressure specs, and manufacturer service notes online!

---

## ✨ Key Features

### ⚡ 1. AI Instant Bike Spec Lookup (Powered by Gemini AI)
- **Instant Auto-Fill:** Select or type your motorcycle's brand and model (e.g. *Yamaha R15 V4*, *Honda CBR 150R*, *KTM Duke 200*, *Royal Enfield Hunter 350*) to auto-populate complete factory specifications in seconds!
- **Complete Passport Data:** Automatically retrieves fuel tank capacity, reserve fuel levels, engine displacement (cc), max power, factory color variants, recommended engine oil grades, and recommended tyre pressures (PSI).
- **Service Schedule Notes:** Automatically imports official manufacturer maintenance intervals and care instructions.

### ☁️ 2. Cloud Backup & Sync via Google Drive
- **Secure Cloud Backup:** Effortlessly sync your bike profiles, refuel logs, service history, and expense records directly to your personal Google Drive account.
- **Cross-Device Restore:** Switch to a new phone and restore your complete motorcycle history with a single tap.

### ⛽ 3. Smart Fuel & Mileage Tracker
- Log refuels with fuel quantity, price, odometer readings, and date.
- Real-time fuel efficiency calculations (km/L or L/100km).
- Visual fuel cost analytics and distance trends.

### 🛠️ 4. Comprehensive Maintenance & Care Log
- **Service History:** Track engine oil changes, brake pad replacements, air filter cleanings, and major overhauls with receipt tracking.
- **Chain Maintenance:** Dedicated log for chain cleaning, lubrication, and tension adjustments.
- **Tyre Pressure Tracker:** Monitor front and rear PSI readings to ensure maximum grip and tire longevity.
- **Wash & Detailing Log:** Keep track of bike washes and protective wax treatments.

### 🔔 5. Smart Maintenance Reminders
- Set mileage-based or time-based alerts for upcoming oil changes, chain lubes, and periodic servicing.
- Receive local system notifications when maintenance thresholds are reached.

### 🎨 6. Modern Jetpack Compose UI
- Glassmorphic design with high-contrast, modern themes.
- Custom adaptive launcher icon and responsive layouts across phones and foldables.
- Multi-language support for international riders.

---

## 🛠️ Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material 3 (M3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture
- **Local Persistence:** Room Database (SQLite)
- **AI Integration:** Google Gemini REST API (`gemini-3.5-flash`)
- **Cloud Integration:** Google Drive REST API & OAuth 2.0 Integration
- **Asynchronous Operations:** Kotlin Coroutines & StateFlow
- **Image Loading:** Coil Compose

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17+
- Android SDK 26 (Android 8.0) or higher

### Build & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/motobook.git
   cd motobook
   ```

2. **Configure API Keys (Optional for Online AI Lookup):**
   Copy `.env.example` to `.env` or configure `GEMINI_API_KEY` in your environment:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **Build the APK:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on device:**
   ```bash
   ./gradlew installDebug
   ```

---

## 📱 Screenshots & Overview

| Digital Passport | AI Spec Auto-Fill | Maintenance Tracker |
| :---: | :---: | :---: |
| Bike Specs & Factory Notes | Instant Spec Lookup via Gemini | Chain, Tyre, Service & Refuels |

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p center>Crafted with ❤️ for motorcycle enthusiasts worldwide.</p>
