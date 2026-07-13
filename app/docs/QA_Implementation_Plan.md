# Phase 6: Quality Assurance Implementation Plan

The goal is to establish a solid automated testing foundation for the VibeCheck MVP to ensure stability before launch. We will add Kotlin-friendly testing dependencies, write Unit Tests for our core business logic (ViewModels and Repositories), and create basic UI Tests for Jetpack Compose.

## Proposed Changes

### 1. Build Configuration
**`app/build.gradle.kts`**
- Add testing dependencies: `kotlinx-coroutines-test`, `mockk` (for mocking API responses), and `turbine` (for testing Kotlin StateFlows).

### 2. Unit Tests (Business Logic)
**`TicketmasterRepositoryTest.kt`**
- Test the mapping logic that transforms the raw Ticketmaster JSON response into our clean `Event` data model.
- Verify behavior when the API returns an empty list or an error.

**`SearchResultsViewModelTest.kt`**
- **Test Requirement 1 & 2 Logic:** Verify that updating active filters (category, date, source) correctly filters the locally cached list of events and emits the updated list to the UI state.

**`EventDetailViewModelTest.kt`**
- Test the `loadEvent()` function.
- Test the fallback logic: ensuring the NWS Weather API is skipped gracefully if event coordinates are `0.0`.

### 3. UI Tests (Jetpack Compose)
**`SearchHomeScreenTest.kt`**
- Verify core UI components render (Date Picker, Categories).
- Verify that clicking the "Search" button passes the correct city text to the navigation callback.

**`SearchResultsScreenTest.kt`**
- **Test Requirement 1:** Verify that initial category and date parameters accurately reflect the listed categories and dates on the Search Results page.
- **Test Requirement 2:** Make sure the interactive pills/chips for date, category, and event source update the displayed results list when toggled by the user.

**`EventDetailScreenTest.kt`**
- **Test Requirement 3:** Inject a mock `Event` and verify that the correct information (Title, Date, Time, Venue, Description) accurately displays on the Event Detail page UI.

## Verification Plan
- Run unit tests: `./gradlew testDebugUnitTest`
- Run UI tests: `./gradlew connectedDebugAndroidTest`