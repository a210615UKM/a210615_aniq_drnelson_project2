# Humanity - Android Mobile Application

A charity and community engagement app built with Kotlin and Jetpack Compose. Users can donate to campaigns, find jobs, join volunteer activities, and discover opportunities near them using location-based sorting.

## Architecture

The app follows the MVVM (Model-View-ViewModel) pattern with a Repository layer:

```
UI (Screens/Composables)
    ↓
ViewModel (AppViewModel, FormLocationViewModel)
    ↓
Repositories (UserRepository, DonationRepository, JobRepository, etc.)
    ↓
Data Sources (Room DB, Firebase Firestore, Firebase Realtime DB, Pledge.to API, Google Maps)
```

## Project Structure

```
com.example.a210615_aniq_drnelson_project2/
├── MainActivity.kt              → Entry point, permission handling, Places SDK init
├── components/                  → Reusable UI composables (header, bottom nav)
├── data/
│   ├── UserData.kt              → In-memory user state model
│   ├── local/                   → Room Database (entities, DAOs, AppDatabase)
│   ├── model/                   → Data classes (Campaign, DonationRecord, etc.)
│   └── remote/                  → Firebase & API service classes
├── navigation/                  → Route definitions (AppScreen enum) and NavGraph
├── repository/                  → Business logic layer between ViewModel and data sources
├── screens/                     → All UI screens grouped by feature
│   ├── auth/                    → Login, SignUp, CustomTextField
│   ├── donation/                → Donation flow screens
│   ├── editprofile/             → Edit profile screen
│   ├── jobs/                    → Add/Edit job screens
│   ├── location/                → Location picker with Google Maps
│   ├── main/                    → Home, Donate, Jobs, Volunteer, Profile screens
│   └── volunteer/               → Add volunteer activity screen
├── ui/theme/                    → Material 3 theme (colors, typography, shapes)
├── util/                        → Utility classes (validation, formatting, distance calc)
└── viewmodel/                   → AppViewModel and FormLocationViewModel
```

## Key Technical Decisions

### Dual Location Modes
The `LocationPickerScreen` supports two modes via a navigation argument:
- `"main"` mode: Updates the global navigation location (shown in the header). Affects proximity sorting across all screens.
- `"form"` mode: Updates only the form-scoped location state (in `FormLocationViewModel`). Used when filling in job or volunteer forms without affecting the main navigation.

### State Management
- `AppViewModel` (AndroidViewModel): Holds all shared app state using Compose `mutableStateOf`. Survives configuration changes.
- `FormLocationViewModel`: Isolated state for form-based location selection, preventing interference with the main location.

### Data Storage
- **Room Database**: Local persistence for user credentials, job listings, and donation records. Allows offline access and fast loading.
- **Firebase Firestore**: Cloud storage for user profiles, volunteer activities, and support messages.
- **Firebase Realtime Database**: Used for real-time community support messages on donation campaigns.

### Proximity Sorting
Uses the Haversine formula (`DistanceUtils.kt`) to calculate distance between GPS coordinates. Items without valid coordinates are placed at the end of sorted lists.

### Donation Flow
1. Browse campaigns from the Pledge.to API
2. View campaign details and community messages
3. Donate via the embedded Pledge widget OR use Demo Donation (no real charges)
4. Optionally leave a support message (stored in Firestore with retry logic)
5. View donation summary on the Thank You screen

## Setup

1. Add your Google Maps API key to `local.properties`:
   ```
   MAPS_API_KEY=your_api_key_here
   ```
2. Place your `google-services.json` in the `app/` directory (for Firebase).
3. Ensure the following APIs are enabled in Google Cloud Console:
   - Places API (New)
   - Maps SDK for Android
   - Geocoding API

## Build & Run

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- Build with Android Studio or `./gradlew assembleDebug`

## Dependencies

| Library | Purpose |
|---------|---------|
| Jetpack Compose + Material 3 | UI framework |
| Navigation Compose | Screen navigation |
| Room | Local SQLite database |
| Firebase Firestore | Cloud data storage |
| Firebase Realtime Database | Real-time messages |
| Firebase Auth | Session persistence |
| OkHttp | HTTP client for Pledge.to API |
| Google Maps Compose | Map display |
| Google Places SDK | Location search & autocomplete |
| Coil | Async image loading |
| Google Fonts | Custom typography |
