# VibeCheck Backend Data Engine: Web Scraping Implementation Guide

## Overview
This document outlines the architecture and step-by-step implementation plan for the VibeCheck backend web scraping engine. The engine’s primary responsibility is to automatically extract event data from platforms that do not offer public developer APIs, normalize that data into a unified schema, and push it to Firebase Firestore for the Android client to consume.

**Execution Frequency:** Every 12 hours.
**Target Sources (Scraping Only):** Posh.vip, Luma (lu.ma), and any local community calendars without API access.

---

## 1. Architecture & Tech Stack

*   **Language:** Python 3.10+ (Chosen for superior headless browser control and HTML parsing).
*   **Infrastructure:** Google Cloud Run (Containerized execution is required because Cloud Functions has limitations with headless browsers).
*   **Scheduling:** Google Cloud Scheduler (Cron job trigger).
*   **Database:** Firebase Firestore (Using the `firebase-admin` Python SDK).
*   **Key Libraries:**
    *   `playwright` or `selenium` (Headless Browser): **Strictly required** for scraping dynamic, JavaScript-heavy sites like Posh.vip and Luma.
    *   `beautifulsoup4`: For parsing static HTML fragments after the page is rendered by the headless browser.
    *   `googlemaps` (Optional): For precise geocoding if the scraped event page only provides a venue name or partial address.

---

## 2. The Unified Data Schema (The "Contract")

Every scraper **must** transform its raw, unstructured web data into this exact JSON structure before saving it to the `events` collection in Firestore. This ensures the Android app (VibeCheck) never breaks.

```json
{
  "id": "string (Unique identifier, e.g., 'posh_event_12345')",
  "source": "string ('Posh', 'Luma', 'LocalCalendar')",
  "sourceUrl": "string (The original scraped link to buy tickets/RSVP)",
  "title": "string (The scraped name of the event)",
  "description": "string (Scraped detailed breakdown of the event)",
  "category": "string (MUST BE CATEGORIZED INTO ONE OF THE 10 VIBECHECK CATEGORIES)",
  "startTimestamp": "number (Unix timestamp in milliseconds for when the event starts)",
  "endTimestamp": "number (Unix timestamp in milliseconds for when the event ends, optional)",
  "cost": "number (Scraped price of entry. 0.0 if free. Null if unknown/hidden)",
  "imageUrl": "string (URL to the scraped event header image, optional)",
  "location": {
    "venueName": "string (e.g., 'The Underground Club')",
    "address": "string (Full physical street address scraped from the page)",
    "city": "string (The normalized city name, e.g., 'Atlanta')",
    "latitude": "number (Required for Maps/Weather)",
    "longitude": "number (Required for Maps/Weather)"
  },
  "tags": ["string", "string"] // Optional array of scraped keywords
}
```

---

## 3. Implementation Steps

### Step 3.1: Project Setup & Authentication
1.  **Initialize Project:** Create a new Python virtual environment (`python -m venv venv`).
2.  **Install Dependencies:** `pip install firebase-admin playwright bs4 googlemaps`.
3.  **Install Browser Binaries:** Run `playwright install` to download the Chromium browser needed to render JavaScript.
4.  **Firebase Service Account:** 
    *   Go to Firebase Console > Project Settings > Service Accounts.
    *   Generate a new private key (`.json` file).
    *   Save this securely in your backend project (DO NOT commit to GitHub).
5.  **Initialize Firebase in Python:** Write a setup script using `firebase_admin.credentials.Certificate(path_to_json)`.

### Step 3.2: Web Scraping Implementation (The Core Logic)
Build modular, robust functions for the target platforms. Since these rely on the DOM structure, they are fragile and **must** include extensive `try/except` blocks to handle missing elements or layout changes.

*   **Posh.vip Scraper (`scrape_posh.py`):**
    1.  Use Playwright to navigate to a target city URL (e.g., `https://posh.vip/c/atlanta`).
    2.  Wait for the dynamic event grid to load (`page.wait_for_selector('.event-card-class-name')`).
    3.  Extract the event Title, Date, Venue Name, and the URL to the specific event detail page.
    4.  *(Optional but recommended):* Navigate to each individual event URL to scrape the full Description, Cost, and exact Address.
*   **Luma Scraper (`scrape_luma.py`):**
    1.  Navigate to Luma's discovery pages (e.g., `lu.ma/explore`).
    2.  Luma heavily obfuscates CSS class names. You will likely need to rely on `XPath` or select elements based on their text content or parent containers.
    3.  Extract details (Title, Time, Location, URL).

### Step 3.3: The Categorization Engine (`categorize.py`)
This step is critical for VibeCheck. You must map the unstructured text scraped from Posh and Luma into your strict 10 categories.
*   **Strategy 1 (Keyword Mapping):** Create a dictionary. If the scraped title or description contains ["club", "dj", "bottle service", "techno"], assign `category = "Nightlife & Parties"`. If it contains ["networking", "founder", "startup"], assign `category = "Professional & Networking"`.
*   **Strategy 2 (LLM API - Recommended for Accuracy):** Pass the scraped description to an API like OpenAI `gpt-4o-mini` with a prompt: *"Categorize this event into exactly one of these 10 categories: [List your 10 categories]. Only return the category name."*

### Step 3.4: Geocoding & Data Integrity
Before saving the scraped event to Firebase, you must validate it against the PRD requirements.
*   **Requirement:** "System must validate that every event has a physical address and a valid source URL."
*   If the scraper only found a `venueName` (e.g., "Terminal West") but no address or coordinates, you must use the Google Maps Geocoding API. Pass `venueName + city` to get the `latitude` and `longitude`. 
*   If the API fails to return a valid location, **discard the event**.

### Step 3.5: Database Upload (Firestore)
*   Write an `upload_to_firestore(events_list)` function.
*   Use `db.collection('events').document(event_id).set(event_data, merge=True)`.
*   **Crucial:** Using `merge=True` ensures that if your scraper runs every 12 hours and sees the same event, it updates the data (like a price change) instead of creating duplicate entries.

---

## 4. Deployment & Automation

Because headless browsers (Playwright) require system dependencies (like specific Linux fonts and graphics libraries), standard Google Cloud Functions often fail. You must use **Google Cloud Run**.

1.  **Dockerize the Scraper:**
    *   Create a `Dockerfile` based on the official Playwright Python image (`mcr.microsoft.com/playwright/python:v1.40.0-jammy`).
    *   Copy your scripts, `requirements.txt`, and (securely injected) Firebase credentials into the container.
2.  **Deploy to Google Cloud Run:**
    *   Build and push the container to Google Artifact Registry.
    *   Deploy the container as a Cloud Run service (ensure it has at least 1GB of memory to handle the browser).
3.  **Schedule the Execution:**
    *   Create a new job in **Google Cloud Scheduler**.
    *   Set the frequency to `0 */12 * * *` (Every 12 hours).
    *   Set the target to trigger your Cloud Run service's HTTP endpoint.