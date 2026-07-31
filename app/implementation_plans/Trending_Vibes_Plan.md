# Dynamic Trending Vibes Feature

The goal is to replace the hardcoded "Trending Vibes" section on the Home Screen with dynamic data based on the selected city. It will analyze events happening in the next 72 hours for that city, find the categories with the most events, and display them. Clicking a trending category will navigate to the Search Results screen with that category pre-selected and the dates automatically filtered to that 72-hour window. If no city is entered, a zero-state view will prompt the user to enter a location.

## Proposed Changes

### Search Home Component

#### `SearchHomeViewModel.kt`
- Inject `TicketmasterRepository` into the ViewModel (or create an instance).
- Create a sealed class `TrendingVibesState` (e.g., `RequiresCity`, `Loading`, `Success(categories)`, `Empty`).
- Add a `StateFlow<TrendingVibesState>` to hold the current trending status.
- Observe updates to the `city` field. 
  - If `city` is empty, emit `TrendingVibesState.RequiresCity`.
  - If `city` has a value, trigger a fetch for events from `now` to `now + 72 hours` for that city.
- Aggregate the fetched events by `EventCategory`, sort by count descending, and expose the top 2 categories in the `Success` state.

#### `SearchHomeScreen.kt`
- Observe the `TrendingVibesState` from the ViewModel.
- Render the UI based on the state:
  - `RequiresCity`: Show a zero-state view (e.g., a simple card prompting "Enter a location above to see what's trending near you.").
  - `Loading`: Show a subtle loading indicator.
  - `Empty`: Show a message like "No trending vibes for the next 72 hours."
  - `Success`: Render dynamic `TrendingCard`s.
- Add an `onClick` parameter to `TrendingCard` and make it clickable.
- Update the `SearchHomeScreen` parameters to accept an `onTrendingCategoryClicked(city: String, category: EventCategory, startMillis: Long, endMillis: Long)` callback.

### Navigation & Search Results

#### `MainActivity.kt`
- Update the `search_results` route definition to accept an optional `category` parameter (e.g., `?start={start}&end={end}&category={category}`).
- Update `SearchHomeScreen` invocation to handle `onTrendingCategoryClicked` by navigating to the `search_results` route using the provided city, the calculated 72-hour start/end timestamps, and the selected category name.

#### `SearchResultsScreen.kt`
- Update the composable signature to accept an `initialCategory: String?` parameter.
- Initialize `selectedCategoryFilter` based on `initialCategory` (mapping the string back to the `EventCategory` enum).
- **Date Filtering:** Ensure the `startDate` and `endDate` parameters (which receive the 72-hour timestamps from `MainActivity`) are actively passed into `viewModel.searchEvents(city, startDate, endDate)` and applied to the local UI filtering logic, perfectly matching the 72-hour window.

## Verification Plan

### Automated Tests
- Run Gradle tests `./gradlew test` to ensure ViewModel and Navigation logic remain intact.

### Manual Verification
- **Zero-State**: Launch the app (with an empty city input) and verify the Trending Vibes section shows the "Enter a location..." prompt.
- **Dynamic Fetch**: Type a city, select it, and verify the Trending Vibes section populates with categories.
- **Navigation & Date Filter**: Click a trending card and verify it navigates to the Search Results screen. Confirm that the category is pre-selected **AND** that the events displayed strictly fall within the next 72 hours.