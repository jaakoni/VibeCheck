# Implementation Plan: Fix Claude Code Audit Issues (P1 - P3)

## Summary of Issues to Fix

### 1. [P1] Ticketmaster Local Times Parsed as UTC
- **File:** `network/TicketmasterApiService.kt`, `repository/TicketmasterRepository.kt`
- **Fix:** Add `timezone: String? = null` to `TicketmasterVenue`. Update `parseTmDate(localDate, localTime, timezone)` to use the venue's timezone `TimeZone.getTimeZone(tzId)` or fallback to device default timezone (`TimeZone.getDefault()`), instead of hardcoding UTC.

### 2. [P1] Network Errors Swallowed in TicketmasterRepository
- **File:** `repository/TicketmasterRepository.kt`
- **Fix:** Remove try/catch blocks in `fetchEvents` and `fetchEventById` that return `emptyList()` / `null`, letting exceptions propagate to `SearchResultsViewModel`, `EventDetailViewModel`, and `SearchHomeViewModel` where error UI states are handled properly.

### 3. [P1] Missing Coordinates Fallback to San Francisco
- **File:** `ui/screens/EventDetailScreen.kt`
- **Fix:** Remove hardcoded San Francisco coordinates (`37.7749`, `-122.4194`). When coordinates are `0.0, 0.0`, render a "Map unavailable for this venue" placeholder and hide the "Get Directions" button.

### 4. [P1] Unclassified Events Default to Nightlife
- **File:** `repository/TicketmasterRepository.kt`
- **Fix:** Change fallback from `EventCategory.NIGHTLIFE` to `EventCategory.COMMUNITY_FESTIVALS` (the neutral bucket).

### 5. [P2] Hardcoded Fake Event Details in AboutSection
- **File:** `ui/screens/EventDetailScreen.kt`
- **Fix:** 
  - Delete `bulletItems` block (the fake VIP gift bag / open bar / Q&A text).
  - Update description fallback to `"Full event details are available on ${event.source.displayName}."` instead of fake conference copy.

### 6. [P3] Day Filter Compares Locale-Formatted Strings
- **File:** `ui/screens/SearchResultsScreen.kt`
- **Fix:** Store day start millisecond timestamps or use `Calendar` day-of-year comparison rather than string comparison (`"Fri 22" == "Fri 22"`).

### 7. [P3] Dead Code `onSearchClicked()` Logging in SearchHomeViewModel
- **File:** `viewmodel/SearchHomeViewModel.kt`, `ui/screens/SearchHomeScreen.kt`
- **Fix:** Remove `onSearchClicked()` from `SearchHomeViewModel` and remove its invocation from `SearchHomeScreen.kt`.

---

## Proposed Changes by File

1. `network/TicketmasterApiService.kt`
   - Add `timezone: String? = null` to `TicketmasterVenue`.
2. `repository/TicketmasterRepository.kt`
   - Update `parseTmDate` to accept `tzId: String?`.
   - Remove try/catch swallowing in `fetchEvents` and `fetchEventById`.
   - Change category fallback to `COMMUNITY_FESTIVALS`.
3. `ui/screens/EventDetailScreen.kt`
   - Check `hasCoords`: show map & directions only if valid coordinates; otherwise show fallback card.
   - In `AboutSection`, remove `bulletItems` and fix description fallback.
4. `ui/screens/SearchResultsScreen.kt`
   - Refactor day filter to compare timestamps/calendar days instead of formatted strings.
5. `viewmodel/SearchHomeViewModel.kt` & `ui/screens/SearchHomeScreen.kt`
   - Remove `onSearchClicked()` dead code.
6. `repository/TicketmasterRepositoryTest.kt`
   - Update tests to match non-swallowing repository and updated time parsing.

---

## Verification Plan
1. Run `./gradlew test` to verify unit test suite.
2. Analyze all modified files for warnings and errors.
3. Test search and event details flow on device.
