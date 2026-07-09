# VibeCheck (Event Planner)

VibeCheck is an Android application designed to help users discover, filter, and track local events. By aggregating event data from multiple sources (like Ticketmaster, and soon Eventbrite, Luma, and Posh), VibeCheck serves as a centralized hub for finding the perfect event to match your vibe.

## ✨ Features

- **Live Event Search:** Search for events by city using the Ticketmaster API.
- **Smart Filtering:** Filter events in real-time by category (Music, Sports, Arts, etc.) and source platform.
- **Deep Event Details:** View comprehensive details for any event, including dates, times, and pricing.
- **Interactive Maps:** Built-in Google Maps integration to pinpoint the exact venue location and launch turn-by-turn directions.
- **Weather Outlook:** Integrated National Weather Service (NWS) API provides live weather forecasts for the event's location and date.
- **Modern UI:** Built entirely with Jetpack Compose, featuring a sleek, Figma-designed user interface.

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Networking:** Retrofit, OkHttp, Kotlinx Serialization
- **APIs Integrated:** 
  - Ticketmaster Discovery API
  - Google Maps SDK for Android
  - National Weather Service (NWS) API
- **Architecture:** MVVM (Model-View-ViewModel)
- **Image Loading:** Coil

## 🚀 Getting Started

To run this project locally, you will need to set up a few API keys:

1. **Clone the repository** and open it in Android Studio.
2. **Set up your API Keys** in your `local.properties` file:
   ```properties
   MAPS_API_KEY=your_google_maps_api_key_here
   TM_API_KEY=your_ticketmaster_api_key_here
   EB_API_KEY=your_eventbrite_api_key_here
   ```
3. **Firebase Setup:** Make sure you have a valid `google-services.json` file in the `app/` directory.
4. **Sync and Run:** Sync the Gradle project and press Play to launch the app on an emulator or physical device.

## 📈 Roadmap

This MVP is actively being developed. Current progress:
- ✅ **Phase 1-2:** Foundation, UI Skeleton, Data Models
- ✅ **Phase 3-4:** Ticketmaster Integration & Search Results
- ✅ **Phase 5:** Event Details, Maps, & Weather API
- ⏳ **Phase 6-7:** Quality Assurance & Play Store Launch
- ⏳ **Phase 8:** Custom Scrapers for Eventbrite, Luma, Posh
- ⏳ **Phase 9:** Multi-platform Expansion (Web & iOS)

---
*Built with ❤️ using Jetpack Compose.*