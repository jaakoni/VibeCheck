# Project Plan: VibeCheck MVP (Android Application)

## Phase 1: Foundation & Requirements
*   **Step 1.1:** Finalize product goals (Relying on PRD guidance for specific rules/flows).
*   **Step 1.2:** Project Infrastructure Setup.
    *   *Completed:* Create a Firebase Project in the Firebase Console.
    *   *Completed:* Connect the Android Studio project to Firebase (adding `google-services.json` and dependencies).
    *   *Pending:* Set up Google Cloud Console to generate a Google Maps API Key.
    *   *Pending:* Register developer accounts and generate API Keys for Ticketmaster and Eventbrite. (Meetup API key is no longer needed for MVP).
    *   *Pending:* Configure `local.properties` or Secrets Gradle Plugin to securely store API keys.
    *   *Pending:* Add Jetpack Compose dependencies (Navigation, ViewModels, Coil for images).

## Phase 2: Data Models & Home Screen
*   **Step 2.1:** Create the backend data model (Kotlin Data Classes: `Event`, `Location`, `EventCategory`, `SearchCriteria`) to establish the strict "contract" for all data entering the app (whether from APIs or scrapers).
*   **Step 2.2:** Create the **Search Home** page UI skeleton matching the Figma design. Implement the necessary placeholders (profile pic, calendar, trending vibes). Set up the ViewModel to handle the user's search inputs (City, Dates).

## Phase 3: Networking, Real Data Ingestion & Scraper/Fast-Follow Placeholders
*   **Step 3.1 (Networking Setup):** Add Retrofit, OkHttp, and Kotlin Serialization dependencies.
*   **Step 3.2 (API Connections):** Create the API client interfaces for Ticketmaster and Eventbrite only.
*   **Step 3.3 (Data Mapping):** Write Repository logic that maps the vastly different API JSON responses from Ticketmaster/Eventbrite into our single, unified `Event` data model.
*   **Step 3.4 (Ingestion Test):** Verify API connections by executing a hardcoded search and logging the normalized `Event` objects.
*   **Step 3.5 (The Mock Repository & Placeholders):** Create a `MockEventRepository`. Crucially, we will hardcode realistic placeholder events representing Luma (Tech/Startup), Posh.vip (Nightlife/Parties), and Meetup (Community/Wellness) into this mock data. This ensures our UI and category filters work perfectly for these specific vibes, even without live ingestion.
*   **Step 3.6 (Firebase Scraper Listener):** Create a `FirebaseEventRepository` connecting to the Firestore `events` collection. We will manually type a few fake Luma/Posh/Meetup events directly into the Firebase Console to prove the app can successfully ingest scraped/fast-follow data.

## Phase 4: Search Results & Data Binding
*   **Step 4.1:** Create the **Search Results** page UI to display a list/grid of events.
*   **Step 4.2:** Connect the Search Home page inputs to query the **Mock Repository** (Step 3.5). Use this safe environment to perfect the UI and the 10-category filtering logic (ensuring our Meetup/Luma/Posh mocks filter correctly).
*   **Step 4.3 (The "Live Swap"):** Swap the Mock Repository for a combined Repository. The app will now pull live data from the APIs (Ticketmaster/Eventbrite) AND read from Firebase (where the Luma/Posh scraped data and Meetup fast-follow data will eventually live).

## Phase 5: Event Details & Location
*   **Step 5.1:** Create the **Individual Event** page UI to show deep details (description, time, tickets, original source link) using the data object passed from the Search Results.
*   **Step 5.2:** Integrate the Google Maps API. Add a "Get Here" button that opens Google Maps using the event's geocoded address.
*   **Step 5.3:** Integrate the NWS (National Weather Service) API to fetch and display the weather forecast for the event's date and coordinates.

## Phase 6: Quality Assurance
*   **Step 6.1:** Create and execute a test plan:
    *   Verify UI responsiveness on different screen sizes.
    *   Test front-end filtering logic (Date and Category).
    *   Test Google Maps and NWS weather integrations.

## Phase 7: Launch Preparation
*   **Step 7.1:** Load the app onto a physical Android device to test real-world performance.
*   **Step 7.2:** Prepare for the Google Play Store (App Bundle, icons, store listing).