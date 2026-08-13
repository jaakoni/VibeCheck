# Implementation Plan: Profile Title Color & Bottom Navigation Wiring

## Goal
1. Change the "Your Profile" top bar title color on `ProfileScreen.kt` to dark navy (`Color(0xFF05345C)`).
2. Wire `BottomNavigationPlaceholder` on all screens (`SearchResultsScreen.kt`, `ProfileScreen.kt`, etc.) to pass `onProfileClick`, so tapping "Profile" or "Saved" anywhere routes to `ProfileScreen` (or `LoginScreen` if unauthenticated).

## Proposed Changes

### 1. `ProfileScreen.kt`
- Update `TopAppBar` title `Text("Your Profile")` color to `Color(0xFF05345C)`.
- Pass `onProfileClick = onBackClick` or accept `onProfileClick: () -> Unit` in `BottomNavigationPlaceholder`.

### 2. `SearchResultsScreen.kt`
- Add `onProfileClick: () -> Unit = {}` to `SearchResultsScreen` and pass it to `BottomNavigationPlaceholder(onProfileClick = onProfileClick)`.
- Update `BottomNavigationPlaceholder` in `SearchResultsScreen.kt` so "Profile" and "Saved" items call `onProfileClick()`.

### 3. `MainActivity.kt`
- Pass `onProfileClick` callback to `SearchResultsScreen`.

## Verification Plan
1. Analyze all modified files for warnings and syntax issues.
2. Launch app and verify:
   - "Your Profile" title on `ProfileScreen` is dark navy (`Color(0xFF05345C)`).
   - Tapping "Profile" or "Saved" on the search results bottom bar navigates to `ProfileScreen` / `LoginScreen`.
