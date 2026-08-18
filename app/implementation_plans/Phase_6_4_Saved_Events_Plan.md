# Implementation Plan: Phase 6.4 - Save / Bookmark Events to Profile

## 1. Goal
Implement bookmarking/saving events across the app:
1. When a logged-in user clicks the heart icon on any event card (or on the event details screen), the event is saved to Firestore under their user account (`users/{userId}/savedEvents/{eventId}`).
2. If an unauthenticated user clicks the heart icon, show an **Auth Prompt Dialog**:
   - Message: *"Sign in to save events to your profile and track your vibes."*
   - Two Actions:
     - **"Sign In"** $\rightarrow$ Navigates to the Login Screen.
     - **"Continue Exploring"** $\rightarrow$ Dismisses the dialog and remains on the current search results / event screen.
3. On the **Profile Screen** (and dedicated **Saved Events** view), display the live list of saved events matching the Figma design (`app/docs/Designs/savedEvents.html`).
4. Clicking an already-saved heart icon removes/unsaves the event.

---

## 2. Visual Architecture & User Flows

```
[ User Clicks Heart Icon on Event ]
               │
               ▼
      [ Is User Logged In? ]
      ├── NO ──> [ Show Login Prompt Dialog ]
      │               ├── "Sign In" ──────────> Navigate to LoginScreen
      │               └── "Continue Exploring" ─> Dismiss dialog & stay on page
      │
      └── YES ─> [ Save / Remove from Firestore: users/{uid}/savedEvents/{id} ]
                      │
                      ▼
               [ Update Heart Icon to Filled (Red / Indigo) ]
                      │
                      ▼
               [ Live Synced on Profile & Saved Events Tab ]
```

---

## 3. Component Details & Code Changes

### A. Data & Repository Layer (`SavedEventsRepository.kt` [NEW])
- Firestore collection path: `users/{userId}/savedEvents/{eventId}`.
- Functions:
  - `saveEvent(userId: String, event: Event)`
  - `removeEvent(userId: String, eventId: String)`
  - `getSavedEventsFlow(userId: String): Flow<List<Event>>`
  - `isEventSaved(userId: String, eventId: String): Boolean`

### B. ViewModel Layer (`SavedEventsViewModel.kt` [NEW])
- Exposes `savedEvents: StateFlow<List<Event>>` and `savedEventIds: StateFlow<Set<String>>`.
- Function `toggleSaveEvent(user: FirebaseUser?, event: Event, onRequireAuth: () -> Unit)`.

### C. UI Layer Updates
1. **`SearchResultsScreen.kt`**:
   - Connect the heart icon on `EventCard`:
     - If event is in `savedEventIds`: Show filled red/indigo heart (`Icons.Filled.Favorite`).
     - Else: Show outline heart (`Icons.Default.FavoriteBorder`).
     - Tapping heart triggers `toggleSaveEvent`.
   - Add **Auth Prompt Dialog** state when `onRequireAuth` is invoked.
2. **`EventDetailScreen.kt`**:
   - Add Heart action icon to top bar / floating action bar to toggle bookmark for current event.
3. **`ProfileScreen.kt` / `SavedEventsSection`**:
   - Replace the static placeholder with a scrollable list of saved event cards matching `savedEvents.html` (with floating date badges, categories, and remove buttons).
   - Empty state when no events are saved.

---

## 4. Verification Plan

### Automated Tests
- `SavedEventsViewModelTest.kt`: Verify state updates on save/remove and `savedEventIds` caching.

### Manual Verification
1. Open search results without logging in $\rightarrow$ click Heart $\rightarrow$ verify Dialog appears with "Sign In" and "Continue Exploring".
2. Click "Continue Exploring" $\rightarrow$ verify search screen stays active.
3. Click Heart $\rightarrow$ "Sign In" $\rightarrow$ Log in with Google $\rightarrow$ verify heart fills.
4. Open Profile $\rightarrow$ verify saved event displays in the list.
5. Click Heart again $\rightarrow$ verify event is removed from saved list.
