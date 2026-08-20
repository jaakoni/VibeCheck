# Implementation Plan: Refactor Application ID & Package Name to `com.vibecheck.events`

## 1. Goal
Migrate the Android project from `com.example.eventplanner` to `com.vibecheck.events` to satisfy Google Play Store submission requirements and establish the permanent production Application ID.

---

## 2. Step-by-Step Changes

### Step 1: Update Build Configurations & Manifest
- **`app/build.gradle.kts`**:
  - Update `namespace = "com.vibecheck.events"`
  - Update `applicationId = "com.vibecheck.events"`

### Step 2: Refactor Kotlin Source Package Directories & Statements
- Rename/move folder structure from:
  - `app/src/main/java/com/example/eventplanner` $\rightarrow$ `app/src/main/java/com/vibecheck/events`
  - `app/src/test/java/com/example/eventplanner` $\rightarrow$ `app/src/test/java/com/vibecheck/events`
  - `app/src/androidTest/java/com/example/eventplanner` $\rightarrow$ `app/src/androidTest/java/com/vibecheck/events`
- Update all `package com.example.eventplanner.*` declarations and `import com.example.eventplanner.*` statements to `com.vibecheck.events.*`.

### Step 3: Firebase & Google Cloud Console Registration (User Action)
- Register `com.vibecheck.events` as a new Android App in Firebase Console (`vibecheck-d5415`).
- Add Debug and Release SHA-1 fingerprints to the `com.vibecheck.events` app in Firebase.
- Download the updated `google-services.json` and replace `app/google-services.json`.

---

## 3. Verification Plan
1. Perform Gradle Sync.
2. Run code inspection across all source files to verify 0 unresolved reference errors.
3. Run unit tests (`./gradlew test`) to verify test suite passes under new package.
4. Deploy to Pixel device to verify launch, maps, and Google OAuth under `com.vibecheck.events`.
