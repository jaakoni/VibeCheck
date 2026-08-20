# Implementation Plan: Phase 7.2 - Google Play Store Preparation

## 1. Goal
Prepare the **VibeCheck** Android application for Google Play Store release:
1. Production App Icon & Branding Assets configuration.
2. Production Release Signing Keystore & Gradle configuration.
3. Building a verified, release-ready Android App Bundle (`.aab`).
4. Google Play Console listing checklist & metadata preparation (descriptions, categories, data safety declaration, privacy policy).

---

## 2. Technical Steps & Architecture

### Step 1: App Identity & Production Launcher Icon
- Verify `android:label` and package details in `AndroidManifest.xml` / `build.gradle.kts`.
- Configure Adaptive App Icon (`ic_launcher` & `ic_launcher_round`) with VibeCheck branding in `res/mipmap-*/`.

### Step 2: Release Signing Keystore Configuration
- Guide generation of a production keystore file (`vibecheck-release-key.jks`) using `keytool` or Android Studio's Keystore wizard.
- Securely configure `signingConfigs` in `app/build.gradle.kts` using `local.properties` or environment variables so passwords are never committed to version control.
- Ensure release Proguard / R8 rules (`proguard-rules.pro`) are configured cleanly to prevent stripping necessary serialization / Firebase models.

### Step 3: Build & Verification of Android App Bundle (`.aab`)
- Build release bundle: `./gradlew :app:bundleRelease`.
- Verify bundle size, package integrity, and signing certificates.
- Add release SHA-1 and SHA-256 fingerprints to Firebase Console to ensure Google OAuth works seamlessly in production builds.

### Step 4: Play Store Listing & Compliance Package
- **App Metadata Document (`app/docs/Play_Store_Listing.md` [NEW])**:
  - App Name: `VibeCheck - Find Your Scene` (max 30 chars)
  - Short Description: (max 80 chars)
  - Full Description: (max 4000 chars)
  - Categorization: Events / Lifestyle
  - Content Rating questionnaire guidance.
  - Data Safety Declaration guidelines (Location, Account info, Photos/Media).
  - Privacy Policy requirements.

---

## 3. Verification Plan
1. Successful compilation and generation of `app/build/outputs/bundle/release/app-release.aab`.
2. Static inspection on production build configs to verify no sensitive credentials or keys are exposed in git.
3. Verification that production SHA-1 / SHA-256 are mapped in Firebase Console.
