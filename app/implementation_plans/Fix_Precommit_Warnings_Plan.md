# Implementation Plan: Fix Pre-commit Code Inspection Warnings

## Goal
Clean up IDE / Git pre-commit inspection warnings across `EventDetailScreen.kt`, `SearchResultsScreen.kt`, `SearchHomeScreen.kt`, `MainActivity.kt`, `SearchHomeViewModel.kt`, and `TicketmasterRepository.kt`.

## Proposed Changes

### 1. `EventDetailScreen.kt`
- Remove unused import `import androidx.compose.foundation.clickable`.
- Replace `Uri.parse(event.sourceUrl)` with `event.sourceUrl.toUri()` (import `androidx.core.net.toUri`).
- Add missing trailing commas in parameter signatures and arguments.

### 2. `SearchHomeScreen.kt`
- Replace deprecated `Modifier.menuAnchor()` with `Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)`.
- Add parameter name for boolean literal `mutableStateOf(value = false)`.
- Add missing trailing commas in parameters and argument lists.

### 3. `SearchResultsScreen.kt`
- Replace unused loop variable `i` in `for (i in 0 until 7)` with `repeat(7)`.
- Omit unused exception variable in `catch (_: Exception)`.
- Simplify `mutableStateOf<Set<EventCategory>>` to `mutableStateOf(...)`.
- Update `when` expression for `event.cost` to use `event.cost` as subject.
- Add clarifying parentheses and missing trailing commas.

### 4. `SearchHomeViewModel.kt` & `TicketmasterRepository.kt`
- Omit unused exception variables in `catch (_: Exception)`.
- Add missing trailing commas in parameters and arguments.

## Verification Plan
1. Run `analyze_file` on all modified files to ensure 0 warnings remain.
2. Compile and build the debug app to ensure zero regression.
