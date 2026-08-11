# Fix Hardcoded Data Audit Findings

This plan addresses several areas in the app where fake or hardcoded data was being presented to the user. It extracts real pricing from Ticketmaster, provides appropriate fallback values for descriptions, utilizes dynamic fallback dates (current date or TBA) instead of static ones, and wires up the bookmark UI visually without adding functionality yet.

## User Review Required

- None at the moment. User has approved "Date TBA" / current date fallback logic.

## Proposed Changes

### Network & Data Models

#### [TicketmasterApiService.kt](file:///Users/beast/Vibe%20Coding/VibeCheck/AndroidStudioProjects/EventPlanner/app/src/main/java/com/example/eventplanner/network/TicketmasterApiService.kt)
- Add `priceRanges` to `TicketmasterEvent` data class to parse price ranges from the Ticketmaster API response.

```kotlin
@Serializable
data class TicketmasterEvent(
    // ... existing fields ...
    val priceRanges: List<TicketmasterPriceRange>? = null,
    // ...
)

@Serializable
data class TicketmasterPriceRange(
    val type: String? = null,
    val currency: String? = null,
    val min: Double? = null,
    val max: Double? = null
)
```

#### [TicketmasterRepository.kt](file:///Users/beast/Vibe%20Coding/VibeCheck/AndroidStudioProjects/EventPlanner/app/src/main/java/com/example/eventplanner/repository/TicketmasterRepository.kt)
- Change fallback description to `"Description from Ticketmaster not available"`.
- Calculate `cost` from `tmEvent.priceRanges` by looking for standard price ranges and grabbing the minimum price. If none exist, remain `null`.

---

### UI Components

#### [SearchResultsScreen.kt](file:///Users/beast/Vibe%20Coding/VibeCheck/AndroidStudioProjects/EventPlanner/app/src/main/java/com/example/eventplanner/ui/screens/SearchResultsScreen.kt)
- Update the Price tag logic. If `event.cost == null`, print `"Pricing data unavailable"` instead of `"Free"`. If `event.cost == 0.0`, print `"Free"`.
- Fix the Bookmark icon: Change `Icons.Default.DateRange` to `Icons.Default.FavoriteBorder` (or another appropriate icon from the default material icons).
- Make the Bookmark icon `clickable { }` (no operation for now, to be handled in Phase 6.4).

#### [EventDetailScreen.kt](file:///Users/beast/Vibe%20Coding/VibeCheck/AndroidStudioProjects/EventPlanner/app/src/main/java/com/example/eventplanner/ui/screens/EventDetailScreen.kt)
- Update the hardcoded `0L` timestamp fallback date of `"Sunday, September 18, 2024"`. If `startTimestamp` is `0L`, fall back to the current date or say "Date TBA".

---

## Verification Plan

### Automated Tests
- Run existing unit tests to ensure `Event` data class and repository behavior still functions appropriately.
  ```bash
  ./gradlew testDebugUnitTest
  ```

### Manual Verification
- Run the app and search for events.
- Check the event cards on `SearchResultsScreen`:
  - Ensure the price says "Pricing data unavailable" if there is no price range, or the actual price (e.g., "$25.00").
  - Click the Bookmark icon on a search result card and verify it visually reacts (ripple effect) but does nothing.
- Click into `EventDetailScreen`:
  - Check the event description to ensure fallback is "Description from Ticketmaster not available".
  - Check that no events display "September 18, 2024" arbitrarily.