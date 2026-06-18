# Humanity — Android Mobile Application

> An Android app supporting **SDG 1: No Poverty** by combining **Donation**, **Jobs**, and **Volunteer** activities into a single platform.

**Author:** Muhammad Aniq Haikal Bin Mustazar
**Matric No:** A210615
**Course:** TTTM2213 — Mobile Application Development
**Stack:** Kotlin · Jetpack Compose · Room · Firebase · Pledge.to API · Google Maps

---

## Table of Contents
1. [About the App](#about-the-app)
2. [Features](#features)
3. [Screens (7-Screen Flow)](#screens-7-screen-flow)
4. [Sensors & APIs Used](#sensors--apis-used)
5. [Persistence (Local + Cloud)](#persistence-local--cloud)
6. [Project Architecture](#project-architecture)
7. [Project Structure](#project-structure)
8. [Setup — How to Run This App](#setup--how-to-run-this-app)
9. [Configuration Files You Must Create](#configuration-files-you-must-create)
10. [Build & Run](#build--run)
11. [Dependencies](#dependencies)
12. [Troubleshooting](#troubleshooting)

---

## About the App

Poverty remains a serious social issue in Malaysia and globally. People affected by unemployment, disasters, or lack of community support struggle to rebuild their lives. At the same time, many people **want** to help but don't know **where** to donate, **how** to find trusted organizations, or **how** to join volunteer activities.

**Humanity** addresses this by giving users one platform to:
- 💖 **Donate** to trusted, verified organizations (live data from the Pledge.to API)
- 💼 **Find or post Jobs** — so users can earn income to support their daily life
- 🙌 **Join Volunteer activities** posted by the community
- 📍 **See nearby opportunities** sorted by GPS distance

---

## Features

| Feature | What it does |
|---------|--------------|
| 🔐 **Authentication** | Sign Up / Login with email + password (Firebase Auth + local Room cache for offline access). |
| 🏠 **Home / Welcome** | Personalized greeting and quick access to Donate / Jobs / Volunteer. |
| 💖 **Donate (Pledge API)** | Live list of NGOs fetched from `api-staging.pledge.to`. Shows logo, mission, and active status. |
| 📍 **Location Picker** | Google Maps screen. Use GPS, search by Places autocomplete, or drag pin. Shows place photos via Places API. |
| 📏 **Proximity Sort** | Haversine distance formula sorts donations / jobs / volunteer activities by how close they are to your location. |
| 💳 **Demo Donation** | Fill donation form → Summary → Support Message → Thank You. No real money — for demo only. |
| 💬 **Community Support Messages** | Send a public message after donating. Stored in Firestore so all users can see it on the campaign page. |
| 💼 **Jobs Board** | Add, edit, and browse job listings stored locally (Room) and synced to Firestore. |
| 🙌 **Volunteer Activities** | Add and browse community volunteer activities with location info. |
| 👤 **Profile** | View donation history (Room) and your support messages (Firestore). Edit profile picture and name. |
| 📡 **Offline Support** | Sign-in works offline using Room-cached credentials; user data persists across sessions. |

---

## Screens (7-Screen Flow)

1. **Sign Up / Login Screen** — auth entry point. Skips to Main if already signed in.
2. **Home Screen** — personalized welcome + navigation to features.
3. **Donate Screen** — list of live NGOs from Pledge API, sorted by your location.
4. **Location Picker Screen** — Google Maps + GPS sensor + Places search.
5. **Donation Form / Summary Screen** — fill amount, see summary before paying.
6. **Support Message + Thank You Screen** — leave a public message, confirmation.
7. **Profile Screen** — donation history (local Room) + support messages (Firestore cloud).

Plus: Jobs, Volunteer, and Edit Profile screens accessible via bottom navigation.

---

## Sensors & APIs Used

### 📡 Hardware Sensor — GPS (Location Sensor)
- Uses Android's **`FusedLocationProviderClient`** (Google Play Services Location).
- File: `repository/LocationRepository.kt` → `getCurrentLocation()`.
- Permissions: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`.
- Used to: pin user position on map, sort donations/jobs/volunteer by proximity.

### 🌐 Live Internet APIs
| API | Purpose | File |
|-----|---------|------|
| **Pledge.to API** | Fetch live list of donation organizations (GET request). Uses an **API key** (`Bearer` token). | `data/remote/PledgeApiService.kt` |
| **Pledge.to Widget** | Embedded donation form that processes giving. Uses a **partner key** (`data-partner-key`). | `screens/donation/PledgeDonationScreen.kt` |
| **Google Maps SDK** | Render the interactive map. | `screens/location/LocationPickerScreen.kt` |
| **Google Places API** | Autocomplete search + place photos + details. | `screens/location/LocationPickerScreen.kt` |
| **Geocoding API** | Reverse-geocode coordinates → human-readable location names. | `repository/LocationRepository.kt` |
| **Firebase Auth API** | User sign-up / sign-in / session. | `repository/UserRepository.kt` |
| **Firebase Firestore** | Cloud storage for messages, jobs, volunteer activities. | `data/remote/FirestoreService.kt` |
| **Firebase Realtime DB** | Real-time community message stream. | `data/remote/RealtimeDbService.kt` |

---

## Persistence (Local + Cloud)

### 🗄️ Local — Room Database (`data/local/`)
| Entity | Purpose |
|--------|---------|
| `UserEntity` | Cached user credentials for offline sign-in. |
| `DonationRecordEntity` | History of every donation made by the user. |
| `JobEntity` | Local copy of job listings. |

### ☁️ Cloud — Firebase
| Service | Stored Data |
|---------|-------------|
| **Firestore** | User profiles, support messages (per campaign), volunteer activities, jobs sync. |
| **Realtime DB** | Live community message stream on each campaign. |
| **Auth** | Email + password authentication, session token. |

---

## Project Architecture

The app follows the **MVVM (Model-View-ViewModel)** pattern with a Repository layer:

```
   ┌──────────────────────────┐
   │  UI (Compose Screens)    │
   └──────────────┬───────────┘
                  │  observe state
   ┌──────────────▼───────────┐
   │  ViewModel               │
   │  (AppViewModel,          │
   │   FormLocationViewModel) │
   └──────────────┬───────────┘
                  │  calls
   ┌──────────────▼───────────┐
   │  Repositories            │
   │  (User, Donation, Job,   │
   │   Location, Message,     │
   │   Volunteer)             │
   └──┬────────────────────┬──┘
      │                    │
   ┌──▼────────┐   ┌───────▼────────────────────┐
   │ Room DB   │   │ Remote (Firestore, Pledge, │
   │ (local)   │   │  Realtime DB, Maps)        │
   └───────────┘   └────────────────────────────┘
```

---

## Project Structure

```
com.example.a210615_aniq_drnelson_project2/
├── MainActivity.kt              → Entry point, permission handling, Places SDK init
├── components/                  → Reusable UI (header, bottom navigation)
├── data/
│   ├── UserData.kt              → In-memory user state model
│   ├── local/                   → Room (UserEntity, JobEntity, DonationRecordEntity, DAOs)
│   ├── model/                   → Data classes (Campaign, DonationRecord, SupportMessage, ...)
│   └── remote/                  → FirestoreService, RealtimeDbService, PledgeApiService
├── navigation/                  → AppScreen enum + NavGraph
├── repository/                  → User, Donation, Job, Location, Message, Volunteer
├── screens/
│   ├── auth/                    → Login, SignUp, CustomTextField
│   ├── donation/                → Demo, Detail, Pledge, Summary, SupportMessage, ThankYou
│   ├── editprofile/             → EditProfileScreen
│   ├── jobs/                    → AddJobScreen, EditJobScreen
│   ├── location/                → LocationPickerScreen (Maps + GPS + Places)
│   ├── main/                    → Home, Donate, Jobs, Volunteer, Profile, Welcome
│   └── volunteer/               → AddVolunteerScreen
├── ui/theme/                    → Material 3 theme (Color, Type, Theme)
├── util/                        → DistanceUtils (Haversine), validation, formatting
└── viewmodel/                   → AppViewModel, FormLocationViewModel
```

---

## Setup — How to Run This App

If you cloned this repo and want to run the app, follow these steps **in order**:

### 1) Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- An emulator or physical device running **Android 7.0 (API 24)** or higher
- A **Google account** (for Firebase + Google Cloud Console)

### 2) Clone the repo
```bash
git clone https://github.com/a210615UKM/a210615_aniq_drnelson_project2.git
cd a210615_aniq_drnelson_project2
```

### 3) Create your `local.properties` file
This file is **NOT** in the repo (it's in `.gitignore` because it contains a secret API key).

A template is provided: **`local.properties.example`**

- Copy `local.properties.example` to `local.properties`
- Fill in:
  - `sdk.dir` → path to your Android SDK
  - `MAPS_API_KEY` → your Google Maps API key

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
MAPS_API_KEY=AIzaSy...your_real_key_here
```

> ⚠️ **The Maps key is required.** Without it, the map screen will be blank and Places autocomplete will fail.

### 4) Get a Google Maps API key
1. Go to [Google Cloud Console](https://console.cloud.google.com/).
2. Create a project (or pick an existing one).
3. Go to **APIs & Services → Library** and **enable** these three APIs:
   - **Maps SDK for Android**
   - **Places API (New)**
   - **Geocoding API**
4. Go to **APIs & Services → Credentials → Create Credentials → API key**.
5. Copy the key into `local.properties` as `MAPS_API_KEY=...`.
6. (Recommended) Restrict the key to your Android package name `com.example.a210615_aniq_drnelson_project2` + your debug SHA-1 fingerprint.

### 5) Set up Firebase
The repo includes the project's `app/google-services.json`. If you want to use **your own** Firebase project instead:

1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a new Firebase project.
3. Add an **Android app** with package name `com.example.a210615_aniq_drnelson_project2`.
4. Download the generated `google-services.json` and place it in the **`app/`** folder (replace the existing one).
5. In the Firebase console, enable:
   - **Authentication → Email/Password**
   - **Cloud Firestore** (start in test mode for development)
   - **Realtime Database** (start in test mode for development)

### 6) Get your Pledge.to keys (optional)

Pledge.to uses **two separate keys** in this app, for two different jobs. Both ship with working demo (staging) values, so the app runs out of the box — but if you want to use your **own** Pledge account, replace them as described below.

You can find both keys in your dashboard at [pledge.to](https://www.pledge.to/) → **Settings / Developers**.

#### a) API Key — used to fetch the live list of NGOs
- **What it does:** Authenticates the REST API call (`GET /v1/organizations`) that loads the live donation organizations shown on the Donate screen.
- **How it's sent:** As an HTTP header → `Authorization: Bearer <API_KEY>`.
- **Where to put it:** In `app/src/main/java/.../data/remote/PledgeApiService.kt`, replace the value of the `apiKey` constant:
  ```kotlin
  // PledgeApiService.kt
  private val apiKey = "YOUR_API_KEY"   // ← paste your Pledge API key here
  ```

#### b) Partner Key — used by the embedded donation widget
- **What it does:** Identifies your Pledge account to the embeddable JavaScript donation **widget** (`embed/widget.js`). Required for the widget that actually processes donations.
- **How it's sent:** As an HTML attribute on the widget → `data-partner-key="<PARTNER_KEY>"`.
- **Where to put it:** In `app/src/main/java/.../screens/donation/PledgeDonationScreen.kt`, replace the value of the `partnerKey` variable:
  ```kotlin
  // PledgeDonationScreen.kt
  val partnerKey = "YOUR_PARTNER_KEY"   // ← paste your Pledge partner key here
  ```

> ⚠️ **Staging vs Production:** This app currently points to Pledge's **staging** environment
> (`api-staging.pledge.to` and `staging.pledge.to`). Staging and production use **different** keys.
> If you switch to your live production keys, also change the URLs:
> `api-staging.pledge.to` → `api.pledge.to` (in `PledgeApiService.kt`) and
> `staging.pledge.to` → `pledge.to` (in `PledgeDonationScreen.kt`).

| Key | Variable | File | Used for |
|-----|----------|------|----------|
| **API Key** | `apiKey` | `data/remote/PledgeApiService.kt` | `Bearer` auth on the REST API that fetches the NGO list |
| **Partner Key** | `partnerKey` | `screens/donation/PledgeDonationScreen.kt` | `data-partner-key` on the embedded donation widget |

### 7) Sync & Run
1. Open the project in Android Studio.
2. Wait for Gradle sync to finish.
3. Run on an emulator or device (the app requests Location permission on first launch — tap **Allow**).
4. On the emulator, **set a location** via the `...` (Extended Controls) → **Location** panel before testing the GPS feature.

---

## Configuration Files You Must Create

| File | Where | What goes in it | Why it isn't in the repo |
|------|-------|-----------------|--------------------------|
| **`local.properties`** | Project root | `sdk.dir` and `MAPS_API_KEY` | Contains secret API key — listed in `.gitignore`. A template `local.properties.example` is provided. |
| **`app/google-services.json`** | `app/` folder | Firebase project config (auto-generated by Firebase Console) | It's currently committed for course-grading purposes. Replace with your own if you fork the project. |

---

## Build & Run

| Setting | Value |
|---------|-------|
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Compile SDK | 36 |
| Kotlin | 2.0+ |
| Gradle | 8.x (Android Gradle Plugin 8.x) |

**Commands:**
```bash
# Build a debug APK
./gradlew assembleDebug

# Install onto a connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test
```

Or just press the green **Run ▶** button in Android Studio.

---

## Dependencies

| Library | Purpose |
|---------|---------|
| Jetpack Compose + Material 3 | UI framework |
| Navigation Compose | Screen navigation |
| Room | Local SQLite database |
| Firebase Firestore | Cloud document storage |
| Firebase Realtime Database | Real-time community messages |
| Firebase Auth | Email/password authentication |
| OkHttp | HTTP client for Pledge.to REST API |
| Google Maps Compose | Interactive map |
| Google Places SDK | Location search, autocomplete, photos |
| Google Play Services Location | FusedLocationProviderClient (GPS) |
| Coil | Async image loading (NGO logos, place photos) |
| Google Fonts | Custom typography |

---

## Troubleshooting

**The map is blank / "For development only" watermark**
→ Your `MAPS_API_KEY` in `local.properties` is missing, wrong, or restricted incorrectly. Check the API is enabled in Google Cloud Console.

**"Current Location" button does nothing on the emulator**
→ The emulator has no GPS fix yet. Open the emulator's `...` (Extended Controls) → **Location** → search a place → **Set Location**. Also confirm the app has Location permission granted.

**Donation list is empty**
→ Your device has no internet, or Pledge.to is unreachable. Check your network or see logs filtered by `PledgeApiService`.

**"Places error 9011/9012"**
→ The Places API key is invalid or the Places API isn't enabled. Enable **Places API (New)** in Google Cloud Console for the same key.

**Firebase auth fails**
→ Make sure `app/google-services.json` matches your Firebase project's package name (`com.example.a210615_aniq_drnelson_project2`) and Email/Password sign-in is enabled in Firebase Console.

**Gradle sync fails on Maps key warning**
→ The build prints a warning if `MAPS_API_KEY` is missing in `local.properties`. Add it (see step 3 of Setup) and re-sync.

---

## License & Acknowledgements

This is a university project for **TTTM2213 — Mobile Application Development** at Universiti Kebangsaan Malaysia (UKM). NGO data is provided by [Pledge.to](https://www.pledge.to/). Maps and Places by Google.
