# Implementation Plan: Migrate Google Sign-In to Credential Manager API

## Goal
Upgrade the Google Sign-In implementation in `LoginScreen.kt` from the deprecated `GoogleSignIn` SDK to Google's modern **Credential Manager API** (`androidx.credentials` & `googleid`), eliminating all deprecation warnings and future-proofing the auth flow.

---

## Proposed Changes

### 1. `gradle/libs.versions.toml`
Add Version Catalog aliases for Credential Manager dependencies:
- `androidx.credentials` (`androidx.credentials:credentials:1.3.0`)
- `androidx.credentials.play.services` (`androidx.credentials:credentials-play-services-auth:1.3.0`)
- `googleid` (`com.google.android.libraries.identity.googleid:googleid:1.1.1`)
- Move `retrofit-converter-kotlinx` hardcoded dependency to Version Catalog.

### 2. `app/build.gradle.kts`
Include credential manager dependencies in `app/build.gradle.kts`.

### 3. `LoginScreen.kt`
- Replace `GoogleSignIn` / `GoogleSignInOptions` / `rememberLauncherForActivityResult` with `CredentialManager.create(context).getCredential(...)` using `GetGoogleIdOption`.
- Extract `GoogleIdTokenCredential` from the returned result and pass `idToken` to `authViewModel.signInWithGoogle(idToken)`.

---

## Verification Plan
1. Perform Gradle Sync.
2. Run `analyze_file` on `LoginScreen.kt` to verify zero deprecation warnings.
3. Launch app and verify Google Sign-In bottom sheet triggers and logs in seamlessly.
