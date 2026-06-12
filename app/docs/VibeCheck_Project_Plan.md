# Project Plan: VibeCheck MVP (Android Application)

## Phase 1: Foundation & Requirements
*   **Step 1.1:** *Completed:* Finalize product goals (Relying on PRD guidance for specific rules/flows).
*   **Step 1.2:** Project Infrastructure Setup.
    *   *Completed:* Create a Firebase Project in the Firebase Console.
    *   *Completed:* Connect the Android Studio project to Firebase (adding `google-services.json` and dependencies).
    *   *Completed:* Set up Google Cloud Console to generate a Google Maps API Key.
    *   *Completed:* Register developer accounts and generate API Keys for Ticketmaster and Eventbrite.
    *   *Completed:* Configure `local.properties` or Secrets Gradle Plugin.
    *   *Completed:* Add Jetpack Compose dependencies.

## Phase 2: Data Models & Home Screen
*   **Step 2.1:** *Completed:* Create the backend data model (Kotlin Data Classes: `Event`, `Location`, `EventCategory`, `SearchCriteria`).
*   **Step 2.2:** *Completed:* Create the **Search Home** page UI skeleton. 
    *   *Completed:* Implemented Google Places API, Date Range Picker, and Multi-Select Category Bottom Sheet.

## Phase 3: Networking, Real Data Ingestion & Ticketmaster Integration
*   **Step 3.1 (Networking Setup):** *Completed:* Add Retrofit, OkHttp, and Kotlin Serialization dependencies.
*   **Step 3.2 (Manual API Test):** *Completed:* Verify Ticketmaster and Eventbrite API keys using cURL or Browser.
*   **Step 3.3 (Diagnostic Connection Test):** *Completed:* Implemented diagnostic function within `MainActivity` to verify Ticketmaster connectivity and Eventbrite authentication.
*   **Step 3.4 (Ticketmaster API Integration):** *Completed:* Built the `TicketmasterApiService` and `Repository` logic to fetch live events for cities.
*   **Step 3.5 (Data Mapping):** *Completed:* Implemented Repository mapping logic that transforms Ticketmaster API JSON into the unified `Event` data model.
*   **Step 3.6 (Ingestion Test):** *Completed:* Verified API connection and mapping by executing a hardcoded search and logging the normalized `Event` objects in Logcat.

## Phase 4: Search Results & Data Binding
*   **Step 4.1:** *Completed:* Create the **Search Results** page UI to display a list of events. (Implemented cards, category tags, price badges, and source badges).
*   **Step 4.2:** *Completed:* Connect the Search Home page inputs to query the Ticketmaster Repository. Implemented reactive category and source filtering logic.
*   **Step 4.3 (Live Sync):** *Completed:* Verified live data from Ticketmaster APIs correctly populate the UI and respond to filter changes.

## Phase 5: Event Details & Location
*   **Step 5.1:** *Pending:* Create the **Individual Event** page UI to show deep details.
*   **Step 5.2:** *Pending:* Integrate the Google Maps API.
*   **Step 5.3:** *Pending:* Integrate the NWS API for weather.

## Phase 6: Quality Assurance
*   **Step 6.1:** *Pending:* Execute test plan.

## Phase 7: Launch Preparation
*   **Step 7.1:** *Pending:* Load app on physical device.
*   **Step 7.2:** *Pending:* Prepare for Google Play Store.

## Phase 8: Scraper Integration (Eventbrite, Luma, Posh)
*   **Step 8.1:** Develop Python scraper scripts.
*   **Step 8.2:** Create `FirebaseEventRepository` and Developer Tooling.
*   **Step 8.3:** Integrate scraped Firebase data into the combined Repository.

## Phase 9: Multi-Platform Expansion (Firebase Web & iOS)
*   **Step 9.1:** Web App Implementation (Claude/Cursor/Vercel).
*   **Step 9.2:** iOS App Implementation (Xcode/Swift).
*   **Step 9.3:** Cross-Platform Synchronization & Firestore Security Rules.