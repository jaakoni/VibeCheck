# Implementation Plan: Google OAuth Integration & Dedicated Login Screen

## Goal
Implement a seamless, modern Google OAuth Authentication flow using standard Google Sign-In / Credential Manager & Firebase Auth. Provide a clean, brand-matched Login Screen and wire navigation so users can sign in using their Google account to save events.

---

## 1. High-Level Architecture & Flow

```
[ Unauthenticated User ] ──> Taps Profile or Bookmark/Save ──> [ Login Screen ]
                                                                      │
                                                           Taps "Continue with Google"
                                                                      │
                                                           [ Google Sign-In Sheet ]
                                                                      │
                                                            Retrieves IdToken
                                                                      │
                                                      [ Firebase Auth Verification ]
                                                                      │
                                                      [ Authenticated State Flow ]
                                                                      │
                                                     [ Profile / Saved Events Access ]
```

---

## 2. Dependencies & Infrastructure Updates

1. **`gradle/libs.versions.toml`**:
   - Add `firebase-auth` library alias: `com.google.firebase:firebase-auth-ktx` (managed by `firebase-bom`).
   - Add `play-services-auth` library alias: `com.google.android.gms:play-services-auth:21.2.0` (or `credentials` & `googleid` for Credential Manager).
2. **`app/build.gradle.kts`**:
   - Add `implementation(libs.firebase.auth)` and `implementation(libs.play.services.auth)`.

---

## 3. Data & Business Logic Layer

1. **`AuthRepository.kt` [NEW]**:
   - Manages Firebase Auth state (`StateFlow<FirebaseUser?>`).
   - Function `signInWithGoogle(idToken: String)` using `GoogleAuthProvider.getCredential(idToken, null)`.
   - Function `signOut()`.
   - Utility property `currentUser` and `isLoggedIn: Boolean`.
2. **`AuthViewModel.kt` [NEW]**:
   - Exposes `uiState` (`Idle`, `Loading`, `Success(FirebaseUser)`, `Error(String)`).
   - Exposes `currentUser` and `isLoggedIn`.
   - Handles Google ID token authentication result and updates UI state.

---

## 4. UI Layer Implementation

1. **`LoginScreen.kt` [NEW]**:
   - Figma-matched brand aesthetic ("VibeCheck", "Discover Your Scene").
   - Clean hero illustration / brand logo header.
   - Subtitle: *"Sign in to save events, track your favorite vibes, and sync across devices."*
   - Single primary call-to-action button: **"Continue with Google"** (with Google logo icon).
   - "Skip for now" / Back button to return to exploring without forced login.
2. **`ProfileScreen.kt` [NEW / UPDATE]**:
   - Displays user profile (Google display name, avatar via `AsyncImage`, email).
   - Includes a "Sign Out" button which returns the user to unauthenticated state.
   - Shows empty/saved events section placeholder for Phase 6.4.

---

## 5. Navigation & Navigation Bar Wiring (`MainActivity.kt` & Top/Bottom Bars)

1. **Add `login` and `profile` Composable Destinations in `MainActivity.kt`**:
   - `composable("login")`
   - `composable("profile")`
2. **Bottom Navigation & Top Bar Triggers**:
   - Clicking the Profile tab or Top Bar Avatar:
     - If logged in ──> Navigate to `profile`.
     - If not logged in ──> Navigate to `login`.
   - Clicking a Bookmark / Save Event icon:
     - If not logged in ──> Prompt to navigate to `login`.

---

## 6. Verification Plan

### Automated Tests
1. **`AuthViewModelTest.kt` [NEW]**:
   - Unit test initial state (`isLoggedIn = false`).
   - Unit test successful sign-in state transition.
   - Unit test sign-out state transition.

### Manual Verification
1. Launch app on emulator/device.
2. Tap the Profile icon in top/bottom bar -> verify `LoginScreen` opens.
3. Tap "Continue with Google" -> verify Google OAuth launcher opens.
4. Select Google account -> verify success and redirect to `ProfileScreen` with user's name, email, and profile photo.
5. Tap "Sign Out" -> verify session clears and returns to unauthenticated state.
