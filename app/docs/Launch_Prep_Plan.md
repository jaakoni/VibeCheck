# Phase 7: Launch Preparation Plan

This document details the checklist and steps required to load VibeCheck onto a physical Android device and prepare the application for submission to the Google Play Store.

## Step 7.1: Load App on Physical Device

To test physical touch latency, actual GPS coordinates, and real-world performance, perform the following steps on your Android device:

### 1. Enable Developer Options
- On your physical Android phone, open the **Settings** app.
- Scroll to the bottom and select **About Phone** (or **System > About Phone**).
- Locate the **Build Number** (usually at the very bottom).
- Tap **Build Number 7 times** rapidly. 
- You will see a toast notification saying *"You are now a developer!"* (You may need to enter your lock screen PIN).

### 2. Enable USB Debugging
- Go back to the main **Settings** page.
- Select **System > Developer Options** (or search "Developer Options" in settings search).
- Scroll down and toggle ON **USB Debugging**.
- Confirm the popup prompt.

### 3. Connect and Deploy
- Connect your phone to your computer via USB.
- On your phone, a popup will ask: *"Allow USB Debugging?"* Check "Always allow" and tap **Allow**.
- In Android Studio, look at the **Device Dropdown** next to the green Play button in the top toolbar.
- Select your physical phone (it should now appear there instead of your emulator).
- Click the green **Play (Run)** button to build and install VibeCheck directly onto your phone!

---

## Step 7.2: Prepare for Google Play Store

### 1. Build a Signed Release App Bundle (AAB)
To upload your app to Google, you must build a release-ready, digitally signed `.aab` file:
1. In the top menu of Android Studio, click **Build > Generate Signed Bundle / APK...**
2. Select **Android App Bundle** and click **Next**.
3. Create a new secure Keystore file (`.jks`) to sign your app. *Keep this key safe! If you lose it, you will never be able to update your app.*
4. Set build variant to **Release** and click **Create**.
5. Once built, find the signed bundle in `app/release/app-release.aab`.

### 2. Google Play Console Account
- Sign up at [play.google.com/console](https://play.google.com/console).
- Requires a standard Google Account.
- There is a one-time **$25 registration fee** charged by Google to publish unlimited apps.

### 3. Google Play Store Asset Checklist
Before submitting, prepare these marketing materials for your store page:
- [ ] **App Icon:** 512px x 512px (PNG, 32-bit with alpha channel).
- [ ] **Feature Graphic:** 1024px x 500px (PNG or JPEG).
- [ ] **Short Description:** Up to 80 characters.
- [ ] **Full Description:** Up to 4000 characters.
- [ ] **Phone Screenshots:** At least 2 screenshots of your app screens (vertical, 16:9 or 18:9 aspect ratio, e.g., 1080x1920).
- [ ] **Privacy Policy:** A webpage URL stating how you protect user data (Google requires this, we can generate a free template later).