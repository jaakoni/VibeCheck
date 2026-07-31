# Fix Trending Vibe Category Selection & Parse Real Categories

The category chip isn't correctly highlighting when navigating from a Trending Vibe card. This is due to `remember` not reacting to changes if the composable is preserved by the navigation backstack. Additionally, the Ticketmaster Repository is currently hardcoding every single event to `EventCategory.LIVE_MUSIC`, making the "Trending Vibes" technically useless as it always only shows Live Music. 

## Proposed Changes

### Search Results Component

#### `SearchResultsScreen.kt`
- Change `selectedCategoryFilter` to initialize to `null` by default.
- Add a `LaunchedEffect(initialCategory)` that maps the `initialCategory` string to the `EventCategory` enum and updates `selectedCategoryFilter`. This ensures the UI properly updates the chip state even if the screen is reused by Jetpack Navigation.

### Network & Data Parsing

#### `TicketmasterApiService.kt`
- Add `classifications: List<TicketmasterClassification>?` to `TicketmasterEvent`.
- Define `TicketmasterClassification`, `TicketmasterSegment`, and `TicketmasterGenre` data classes to deserialize the category metadata coming from Ticketmaster.

#### `TicketmasterRepository.kt`
- Inside `mapToEvent`, extract the `segment.name` and `genre.name` from the `classifications` array.
- Create a `when` block to map these raw Ticketmaster strings (e.g., "Sports", "Arts & Theatre") to our internal `EventCategory` enum (e.g., `EventCategory.SPORTS_RECREATION`, `EventCategory.ARTS_CULTURE`).
- Remove the hardcoded `EventCategory.LIVE_MUSIC` default.

## Verification Plan
- Build the app and verify compilation.
- Ensure the Trending Vibes section now dynamically lists actual different categories (e.g., Sports, Arts) instead of just Live Music.
- Click a card and verify the Search Results screen correctly highlights the appropriate category chip.