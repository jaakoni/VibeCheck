<!-----



Conversion time: 2.214 seconds.


Using this Markdown file:

1. Paste this output into your source file.
2. See the notes and action items below regarding this conversion run.
3. Check the rendered output (headings, lists, code blocks, tables) for proper
   formatting and use a linkchecker before you publish this page.

Conversion notes:

* Docs™ to Markdown version 2.0β2
* Wed Apr 01 2026 21:51:16 GMT-0700 (Pacific Daylight Time)
* Source doc: Event Planner PRD
* Tables are currently converted to HTML tables.
----->



# **PRD: VibeCheck**

**Authors:** Gemini | **Status:** Draft


## **Overview (TL;DR)**

**VibeCheck** is an event aggregator platform that allows users to input a city and a date range (up to 7 days) to find public events across a wide cultural spectrum. The app acts as a central hub, scraping and indexing data from major platforms (Eventbrite, Posh, etc.) and presenting them in a searchable, filterable interface with dedicated event detail pages.


## **Outcome**

Users will experience a 3-step flow to discover and plan their outings:



1. **Global Search:** Input destination and timing.
2. **Discovery & Filtering:** A comprehensive list of events filterable by day and category.
3. **Deep Dive:** Dedicated event pages with full details, weather, and one-click navigation.


---


## **User Requirements - MVP (The UI Flow)**


<table>
  <tr>
   <td><strong>Step</strong>
   </td>
   <td><strong>Screen</strong>
   </td>
   <td><strong>User Action / Flow</strong>
   </td>
   <td><strong>MVP?</strong>
   </td>
  </tr>
  <tr>
   <td><strong>1</strong>
   </td>
   <td><strong>Search Home</strong>
   </td>
   <td>User enters a <strong>City</strong> and selects a <strong>Date Range</strong> (Max 7 days).
   </td>
   <td>Yes
   </td>
  </tr>
  <tr>
   <td><strong>2</strong>
   </td>
   <td><strong>Search Results</strong>
   </td>
   <td>User views a master list of all events. They can <strong>Filter</strong> by specific <strong>Day</strong> or any of the <strong>10 Categories</strong>.
   </td>
   <td>Yes
   </td>
  </tr>
  <tr>
   <td><strong>3</strong>
   </td>
   <td><strong>Event Detail Page</strong>
   </td>
   <td>User clicks an event to see: Name, Location, Cost, Description, Link, "Get Here" button, and Weather.
   </td>
   <td>Yes
   </td>
  </tr>
</table>



---


## **Technical Functional Requirements**


<table>
  <tr>
   <td><strong>Feature</strong>
   </td>
   <td><strong>Requirement</strong>
   </td>
   <td><strong>Technical Implementation</strong>
   </td>
  </tr>
  <tr>
   <td><strong>Data Aggregation</strong>
   </td>
   <td>Multi-Source Scraping and API connections
   </td>
   <td>System must scrape/API-fetch from Eventbrite, Posh, Ticketmaster, Luma, and Meetup based on city and date_range. All of the event data on the event detail page is taken from these sites. The sites must be scraped once every 12 hours.
<p>
Given that EventBrite, Ticketmaster, and Meetup have APIs that can easily be connected to, the MVP will focus on those sites.  This also ensures we can leverage Android Studio to directly connect to the sites without the need for a web scraper.  Order of priorities:
<ol>

<li>EventBrite</li>

<li>Ticketmaster</li>

<li>Meetup

<p>
Luma and Posh.vip will need a webscraper and this will be done post MVP. as a fast follow.  We will create placeholders for the data from these sites</li>
</ol>
   </td>
  </tr>
  <tr>
   <td><strong>Categorization</strong>
   </td>
   <td>Spectrum Tagging
   </td>
   <td>Logic must categorize every event into one of the 10 defined categories (Nightlife, Family, etc.) based on metadata.
   </td>
  </tr>
  <tr>
   <td><strong>Location Services</strong>
   </td>
   <td>Geocoding
   </td>
   <td>Convert user-entered City string into Lat/Long coordinates to power weather and map queries.
   </td>
  </tr>
  <tr>
   <td><strong>Weather Integration</strong>
   </td>
   <td>NWS API Support
   </td>
   <td>Fetch specific daily forecasts from the National Weather Service using geocoded coordinates for the Event Detail page.
   </td>
  </tr>
  <tr>
   <td><strong>Navigation</strong>
   </td>
   <td>Deep Linking
   </td>
   <td>The "Get Here" button must generate a universal Google Maps URL using the verified event address.
   </td>
  </tr>
  <tr>
   <td><strong>Data Integrity</strong>
   </td>
   <td>Event Verification
   </td>
   <td>System must validate that every event has a physical address and a valid source URL before displaying.
   </td>
  </tr>
  <tr>
   <td><strong>Performance</strong>
   </td>
   <td>Filtering Logic
   </td>
   <td>Search page must support real-time front-end filtering by Date and Category without re-scraping.
   </td>
  </tr>
</table>



---


## **Event Categories & Source Integration**

To ensure users find things across all spectrums, **VibeCheck** aggregates events into these 10 groups:



1. **Nightlife & Parties:** R&B, Reggae, Hip Hop, and Salsa parties.
2. **Family & Kids:** Park events, playgrounds, and kid-friendly festivals.
3. **Food & Drink:** Brunch, food truck rallies, and pop-up dining.
4. **Live Music & Concerts:** Jazz sets, local bands, and ticketed performances.
5. **Arts & Culture:** Gallery openings, theater, and spoken word.
6. **Health & Wellness:** Outdoor yoga, boot camps, and wellness meetups.
7. **Community & Festivals:** Street fairs, block parties, and urban culture.
8. **Workshops & Classes:** Sip and paint, cooking classes, and DIY workshops.
9. **Professional & Networking:** Tech mixers and business seminars.
10. **Sports & Recreation:** 5K runs, pick-up games, and sports screenings.

**Target Sources:** Eventbrite, Posh, Ticketmaster, Luma, Meetup, and local community calendars.


---


## **UI Components**



* **Search Results Page:** Includes a "Filter Bar" for toggling categories and specific dates within the 7-day range.
* **Event Detail Page:**
    * **Name & Description:** Detailed breakdown of the event.
    * **Weather Widget:** Specific NWS forecast for that event's date (requires city to lat/long conversion).
    * **"Get Here" Button:** Opens Google Maps with the event address as the destination.
    * **Original Link:** Clear CTA to view the event and purchase tickets on the source site (e.g., Posh).


## **Success Metrics**



* **Filter Engagement:** Percentage of users who utilize the day/category filters on the search page.
* **Detail Depth:** Average time spent on the Event Detail page.
* **Navigation Rate:** Number of users clicking the "Get Here" button per session.