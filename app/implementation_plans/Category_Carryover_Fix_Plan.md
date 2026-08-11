# Implementation Plan: Category Carryover Navigation Fix

## Problem Description
When selecting a category (such as "Sports & Recreation") in the Search Category picker modal on the `SearchHomeScreen` and clicking "Search Events", the selected category filter is dropped and does not carry over to the `SearchResultsScreen`.

## Root Cause
1. `SearchHomeScreen.kt`: The `onSearchClicked` callback signature `(city: String, start: Long?, end: Long?) -> Unit` did not accept selected categories. When the "Search Events" button was clicked, `searchCriteria.selectedCategories` was ignored.
2. `MainActivity.kt`: Navigating from `SearchHomeScreen` built the route string without query parameters for category (`?category=...`), causing `SearchResultsScreen` to receive `initialCategory = null`.

## Proposed Changes

### 1. `SearchHomeScreen.kt`
- Update the `onSearchClicked` callback signature to accept `categories: Set<EventCategory>`:
  `onSearchClicked: (city: String, categories: Set<EventCategory>, start: Long?, end: Long?) -> Unit`
- Update the "Search Events" button `onClick` handler to pass `searchCriteria.selectedCategories` into `onSearchClicked`.

### 2. `MainActivity.kt`
- Update the `SearchHomeScreen` `onSearchClicked` lambda to construct query parameters including `category` when `categories` is not empty.
- Ensure proper URL encoding and query string formatting (`?start=...&end=...&category=...`).

### 3. `SearchResultsScreen.kt`
- Initialize `selectedCategoryFilter` using `remember(initialCategory)` to ensure the state immediately picks up the `initialCategory` value passed via navigation.

## Verification Plan

### Automated Tests
- Run existing unit and UI tests: `./gradlew test`

### Manual Verification
1. Launch app on emulator/device.
2. Select a city (e.g., Atlanta).
3. Open "Search Categories", select "Sports & Recreation", click "Apply Filter".
4. Click "Search Events".
5. Verify `SearchResultsScreen` opens with "Sports & Recreation" chip highlighted and event list filtered accordingly.
