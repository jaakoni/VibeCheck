# Project Plan: VibeCheck MVP (Android Application)

## Phase 1: Foundation & Requirements
*   **Step 1.1:** *Completed:* Finalize product goals (Relying on PRD guidance for specific rules/flows).
*   **Step 1.2:** Project Infrastructure Setup.
    *   *Completed:* Create a Firebase Project in the Firebase Console.
    *   *Completed:* Connect the Android Studio project to Firebase (adding `google-services.json` and dependencies).
    *   *Completed:* Set up Google Cloud Console to generate a Google Maps API Key.
    *   *Completed:* Register developer accounts and generate API Keys for Ticketmaster and Eventbrite. (Meetup API key is no longer needed for MVP).
    *   *Completed:* Configure `local.properties` or Secrets Gradle Plugin to securely store API keys.
    *   *Completed:* Add Jetpack Compose dependencies (Navigation, ViewModels, Coil for images).

## Phase 2: Data Models & Home Screen
*   **Step 2.1:** *Completed:* Create the backend data model (Kotlin Data Classes: `Event`, `Location`, `EventCategory`, `SearchCriteria`) to establish the strict "contract" for all data entering the app (whether from APIs or scrapers).
*   **Step 2.2:** *Completed:* Create the **Search Home** page UI skeleton matching the Figma design. 
    *   *Completed:* Implemented Google Places API for real-time City Autocomplete.
    *   *Completed:* Implemented Material 3 Date Range Picker.
    *   *Completed:* Implemented Multi-Select Category Bottom Sheet Modal.

## Phase 3: Networking, Real Data Ingestion & Ticketmaster Integration
*   **Step 3.1 (Networking Setup):** *Completed:* Add Retrofit, OkHttp, and Kotlin Serialization dependencies.
*   **Step 3.2 (Manual API Test):** *Completed:* Verify Ticketmaster and Eventbrite API keys using cURL or Browser.
*   **Step 3.3 (Diagnostic Connection Test):** *Completed:* Implemented diagnostic function within `MainActivity` to verify Ticketmaster Discovery API connectivity and Eventbrite authentication.
*   **Step 3.4 (Ticketmaster API Integration):** *Pending:* Build the `TicketmasterApiService` and `Repository` logic to fetch live events for cities.
*   **Step 3.5 (Data Mapping):** *Pending:* Write Repository logic that maps the API JSON responses from Ticketmaster into our unified `Event` data model.
*   **Step 3.6 (Ingestion Test):** *Pending:* Verify API connections by executing a hardcoded search and logging the normalized `Event` objects.

## Phase 4: Search Results & Data Binding
*   **Step 4.1:** *Pending:* Create the **Search Results** page UI to display a list/grid of events.
*   **Step 4.2:** *Pending:* Connect the Search Home page inputs to query the Ticketmaster Repository (Step 3.5). Perfect the 10-category filtering logic.
*   **Step 4.3 (Live Sync):** *Pending:* Verify live data from Ticketmaster APIs correctly populate the UI.

## Phase 5: Event Details & Location
*   **Step 5.1:** *Pending:* Create the **Individual Event** page UI to show deep details (description, time, tickets, original source link) using the data object passed from the Search Results.
*   **Step 5.2:** *Pending:* Integrate the Google Maps API. Add a "Get Here" button that opens Google Maps using the event's geocoded address.
*   **Step 5.3:** *Pending:* Integrate the NWS (National Weather Service) API to fetch and display the weather forecast for the event's date and coordinates.

## Phase 6: Quality Assurance
*   **Step 6.1:** *Pending:* Create and execute a test plan:
    *   Verify UI responsiveness on different screen sizes.
    *   Test front-end filtering logic (Date and Category).
    *   Test Google Maps and NWS weather integrations.

## Phase 7: Launch Preparation
*   **Step 7.1:** *Pending:* Load the app onto a physical Android device to test real-world performance.
*   **Step 7.2:** *Pending:* Prepare for the Google Play Store (App Bundle, icons, store listing).

## Phase 8: Scraper Integration (Eventbrite, Luma, Posh)
*   **Step 8.1:** Develop Python scraper scripts for Eventbrite, Luma, and Posh to extract real Organization IDs/event lists and push them to Firebase.
*   **Step 8.2:** Create a `FirebaseEventRepository`. Implement a Developer Tool Button in the app to push scraped data directly to Firebase.
*   **Step 8.3:** Integrate scraper data (from Firebase) into the combined Repository to supplement Ticketmaster events.

## Phase 9: Multi-Platform Expansion (Firebase Web & iOS Setup)
*   **Step 9.1: Web App Implementation (Claude/Cursor/Vercel):**
    *   Register **Web App** in Firebase Console; initialize SDK using `firebaseConfig`.
    *   Initialize project in **Cursor**; use **Claude** to generate React/Next.js components.
    *   Deploy repository to **Vercel** via GitHub integration for continuous deployment.
    *   Configure Vercel Environment Variables to securely store `firebaseConfig` keys.
*   **Step 9.2: iOS App Implementation (Xcode):**
    *   Register **iOS App** in Firebase Console; download `GoogleService-Info.plist`.
    *   Integrate Firebase SDK via Swift Package Manager.
*   **Step 9.3: Cross-Platform Synchronization:**
    *   Ensure all platforms point to the same Project ID.
    *   Configure shared Firestore Security Rules for consistent data access across all platforms.