# Implementation Plan: Fix Organizer & Attraction/Performer Mapping from Ticketmaster

## 1. Problem Description
Currently in `TicketmasterRepository.kt`:
```kotlin
organizerName = tmEvent._embedded?.venues?.firstOrNull()?.name ?: "Event Organizer"
```
Both `venueName` and `organizerName` are extracted from `venues.firstOrNull()?.name`. This results in the "Hosted By" section displaying the venue name (e.g. "Fox Theatre" or "State Farm Arena") rather than the performer, artist, team, or event promoter. Additionally, the Ticketmaster API payload's `attractions` (performers/artists) and `promoters` are currently not deserialized or mapped into our `Event` model.

---

## 2. Proposed Changes

### A. `TicketmasterApiService.kt`
Update `TicketmasterEventEmbedded` and `TicketmasterEvent` data models to parse attractions and promoter info from the Ticketmaster Discovery API:
```kotlin
@Serializable
data class TicketmasterEventEmbedded(
    val venues: List<TicketmasterVenue>? = null,
    val attractions: List<TicketmasterAttraction>? = null
)

@Serializable
data class TicketmasterAttraction(
    val id: String? = null,
    val name: String? = null,
    val url: String? = null
)

@Serializable
data class TicketmasterPromoter(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null
)
```
Add `val promoters: List<TicketmasterPromoter>? = null` to `TicketmasterEvent`.

### B. `TicketmasterRepository.kt`
Update `organizerName` mapping logic to prioritize:
1. **Primary Attraction / Performer / Artist:** `tmEvent._embedded?.attractions?.firstOrNull()?.name` (e.g. "Olivia Dean", "Atlanta Braves", "Childish Gambino").
2. **Promoter / Producer:** `tmEvent.promoters?.firstOrNull()?.name` (e.g. "Live Nation").
3. **Fallback:** `tmEvent._embedded?.venues?.firstOrNull()?.name` or `"Event Organizer"`.

Also append attraction names to `tags` so they can be searched or filtered by performer/artist.

### C. `TicketmasterRepositoryTest.kt`
Update mock unit tests with mock `attractions` and verify `organizerName` resolves to the primary attraction name.

---

## 3. Verification Plan
1. Run `./gradlew test` to ensure JSON mapping and unit tests pass.
2. Launch app and inspect Event Details for any live Ticketmaster event $\rightarrow$ verify "Hosted By" displays the performer/artist/team name rather than repeating the venue name.
