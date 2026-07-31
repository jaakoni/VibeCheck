# Fix Hardcoded Dates in EventCard

The `EventCard` composable in `SearchResultsScreen.kt` currently uses hardcoded string values ("Sun, Sep 18" and "09:00 AM") for displaying the event's date and time. This means regardless of the event's actual data or filtering, it will always show these hardcoded text values.

## Proposed Changes

### Search Results Component

#### `SearchResultsScreen.kt`

- Update the `EventCard` composable to use a `SimpleDateFormat` to parse `event.startTimestamp`.
- Replace the hardcoded `Text("Sun, Sep 18")` and `Text("09:00 AM")` elements with the dynamically formatted date and time.
- E.g., formatting `startTimestamp` into `"EEE, MMM d"` for the date and `"hh:mm a"` for the time.

### Documentation

#### `Daily_Logs.md`
- **Prepend Rules:** Add the AI Instructions (the 3 strict rules about logging and implementation plans) to the very top of the file so every future agent reads them first.
- Add a new entry for today's date documenting the fix applied to `SearchResultsScreen.kt` regarding the hardcoded dates on the `EventCard`.
- **Crucially:** Explicitly reference and link to the newly created `Fix_Hardcoded_Dates_Plan.md` file within this log entry so future agents can find the full context.

#### `Fix_Hardcoded_Dates_Plan.md` (This file)
- Save a copy of this implementation plan and walkthrough into the `app/docs/` directory so it persists in the project repository, protecting against AI assistant cache resets.

## Verification Plan

### Automated Tests
- `./gradlew test` to ensure unit tests continue passing.

### Manual Verification
- Deploy the app (or run a preview) to visually verify that Event Cards are now displaying realistic and varied dates corresponding to their underlying models.