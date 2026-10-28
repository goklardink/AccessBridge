# AccessBridge

A Supportive Information App for Disadvantaged Individuals

Android app that gathers health, legal rights, transport, and education information for disadvantaged individuals in one place, with English/Turkish support and accessibility modes. Java, Android Studio, Retrofit.

## Team

Ayla Bilici             . 2365261
Helin Alp               . 2366821
Gokdeniz Ayberk Turkmen . 2365267
Mohammad Ammar Hawwari  . 2250664

---

## What the app does

The home screen has four category cards. Each one opens a section:

- **Health** : uses the device's location to find nearby hospitals and pharmacies via OpenStreetMap. Results open in Google Maps. Users can also type a district name to search manually.
- **Legal Rights** : six fundamental rights, each with a detail screen. When simple-language mode is on, the text rewrites itself in plain wording.
- **Transport** : loads real Istanbul bus-stop data from the iBB open-data portal and lets users search by stop name or filter by direction.
- **Education** : six resources (free courses, scholarships, adult education, vocational training) each with a detail screen.

On launch, a welcome notification introduces the four sections. A broadcast receiver watches for connectivity changes and shows a red alert when the internet drops and a green one when it comes back.

---

## Accessibility settings

All settings persist between sessions and apply app-wide through `BaseActivity`:

- Font size: 1.0x / 1.3x / 1.5x
- High-contrast theme
- Simple-language mode (plain wording in Legal content)
- Voice reader; text-to-speech set to Turkish locale, falls back to English if Turkish is unavailable
- Full English and Turkish string resources (values and values-tr)

---

## Tech stack

- Java, Android Studio
- Min SDK 24 (Android 7.0), Target/Compile SDK 35, Java 11
- Retrofit 2.9.0 + Gson 2.10.1 transport data
- OkHttp 4.12.0 health facility queries
- Material Design, CardView, RecyclerView, ConstraintLayout
- Notifications (channel-based), BroadcastReceiver, TextToSpeech, SharedPreferences

---

## Project structure

---
app/src/main/java/com/example/accessbridgeproject/

  activities/
    BaseActivity.java         # theme + font scale for all screens
    SplashActivity.java
    MainActivity.java         # dashboard, notification channel, welcome notification
    HealthActivity.java       # OkHttp -> Nominatim + Overpass
    LegalActivity.java
    TransportActivity.java    # Retrofit -> IBB GeoJSON
    EducationActivity.java
    DetailActivity.java       # reusable detail screen
    SettingsActivity.java

  adapters/
    InfoAdapter.java          # shared RecyclerView adapter + OnItemClickListener

  models/
    InfoItem.java
    DurakResponse.java        # GeoJSON FeatureCollection
    DurakFeature.java
    DurakGeometry.java
    DurakProperties.java

  network/
    RetrofitClient.java       # Retrofit singleton (IBB base URL)
    IettApiService.java

  utils/
    SettingsManager.java      # SharedPreferences wrapper
    TTSManager.java           # TTS wrapper (Turkish locale)
    NetworkReceiver.java      # connectivity BroadcastReceiver

---

## Getting started

**Requirements:** Android Studio (recent stable), JDK 11, Android SDK 35.

```bash
git clone <repo-url>
cd AccessBridgeProject
./gradlew assembleDebug
# APK -> app/build/outputs/apk/debug/app-debug.apk
```

\\Or just open the project in Android Studio and press Run. Use an API 24+ device or emulator (API 33+ recommended for testing the runtime notification permission)\\

**Quick demo path:**

1. Launch : splash screen -> welcome notification -> dashboard
2. Health : grant location -> nearby hospitals/pharmacies -> tap to open in Maps
3. Transport : search a stop by name, then filter by direction (e.g. `KARTAL`)
4. Legal : open a right -> enable simple-language mode in Settings -> come back and see the change
5. Settings : raise font size + high-contrast; long-press a card to hear the voice reader
6. Turn off Wi-Fi -> red alert -> turn it back on -> green alert

---

## Permissions

| Permission                            | Reason                                                |
|---------------------------------------|------------------------------------------------------ |
| 'INTERNET'                            | Fetch bus-stop, geocoding, and facility data          |
| 'ACCESS_NETWORK_STATE'                | Detect connectivity changes                           |
| 'POST_NOTIFICATIONS'                  | Welcome notification (runtime request on Android 13+) |

\\Location access is requested at runtime in the Health screen by the system 'LocationManager'\\

---

## Data sources

All network calls are asynchronous. The app surfaces clear error messages if a service is slow or unreachable rather than freezing or crashing.

- **iBB open-data portal** : 'https://data.ibb.gov.tr/.../iett-otobus-duraklar..geojson' (Retrofit + Gson)
- **Nominatim** - 'https://nominatim.openstreetmap.org/search' - district geocoding (OkHttp)
- **Overpass API** - 'https://overpass-api.de/api/interpreter' - nearby hospitals and pharmacies (OkHttp)

\\All three are public endpoints that require no API key\\

---

## Known limitations

- Legal and Education content is bundled locally rather than fetched from the web.
- Transport only covers istanbul (iBB) data.
- Overpass can occasionally time out; the app handles this gracefully but the user may need to retry.
