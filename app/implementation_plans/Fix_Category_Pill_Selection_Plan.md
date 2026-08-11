# Implementation Plan: Fix Category Pill Selection & Multi-Category Support on Search Results

## Problem Description
1. When navigating to `SearchResultsScreen` with a selected category (e.g. `SPORTS_RECREATION`), the corresponding Category Filter chip/pill was not visually highlighted as selected.
   - Root Cause A: `CategoryFilterChip` styling for `else` categories used `Pair(Color.White, Color(0xFF05345C))`. When `isSelected` was true, `backgroundColor` was set to `chipColors.first` which evaluated to `Color.White`! This made unselected pills (`Color.White`) and selected pills (`Color.White`) look completely identical for 6 out of 10 categories (including `SPORTS_RECREATION`, `FAMILY`, `LIVE_MUSIC`, `WORKSHOPS`, `COMMUNITY_FESTIVALS`, `PROFESSIONAL_NETWORKING`).
2. Multi-category selection support: When users select multiple categories on `SearchHomeScreen` (e.g., Nightlife AND Sports), only the first category was passed in navigation. `SearchResultsScreen` only supported filtering by a single `EventCategory?`.

## Proposed Changes

### 1. `CategoryFilterChip` in `SearchResultsScreen.kt`
- Fix chip styling so ALL selected categories are visually distinct from unselected ones.
- Give every category a distinct background color when selected, or fallback to a primary active color (e.g., `Color(0xFFE5EEFF)` with `Color(0xFF5450C1)` text / primary container) when `isSelected = true`, rather than `Color.White`.

### 2. Multi-Category Support on `SearchResultsScreen.kt`
- Update `initialCategory` parameter or accept a comma-separated list of category names (e.g. `initialCategories: String?`).
- Change `selectedCategoryFilters` in `SearchResultsScreen` from `EventCategory?` to `Set<EventCategory>`.
- Allow toggling multiple category pills on the search results screen.
- Filter events if `selectedCategoryFilters.isEmpty() || selectedCategoryFilters.contains(event.category)`.

### 3. Navigation in `MainActivity.kt`
- Pass all selected categories as a comma-separated string in the `category` argument (e.g. `SPORTS_RECREATION,NIGHTLIFE`).
- Parse the comma-separated string in `SearchResultsScreen` into `Set<EventCategory>`.

## Verification Plan

### Manual Verification
1. Open the app and tap "Search Categories" on `SearchHomeScreen`.
2. Select "Sports & Recreation" (and optionally "Nightlife & Parties") and tap "Apply Filter".
3. Tap "Search Events".
4. On `SearchResultsScreen`, verify that the "Sports & Recreation" chip (and any other selected category) is visibly highlighted with a distinct background color.
5. Tap category chips on `SearchResultsScreen` to toggle them on/off and verify the filter updates in real time.
