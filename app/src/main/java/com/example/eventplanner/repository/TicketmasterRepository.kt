package com.example.eventplanner.repository

import android.util.Log
import com.example.eventplanner.model.Event
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.model.EventSource
import com.example.eventplanner.model.Location
import com.example.eventplanner.network.NetworkModule
import com.example.eventplanner.network.TicketmasterEvent
import java.text.SimpleDateFormat
import java.util.Locale

class TicketmasterRepository(private val apiKey: String) {

    suspend fun fetchEvents(city: String, startDateTime: String? = null, endDateTime: String? = null): List<Event> {
        return try {
            val response = NetworkModule.ticketmasterApi.searchEvents(
                apiKey = apiKey,
                city = city,
                startDateTime = startDateTime,
                endDateTime = endDateTime
            )
            val ticketmasterEvents = response._embedded?.events ?: emptyList()
            
            // Map the API result to our internal Event data model
            ticketmasterEvents.map { tmEvent ->
                mapToEvent(tmEvent, city)
            }
        } catch (e: Exception) {
            Log.e("TicketmasterRepo", "Error fetching events: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchEventById(id: String): Event? {
        return try {
            val tmEvent = NetworkModule.ticketmasterApi.getEventDetails(id, apiKey)
            mapToEvent(tmEvent, tmEvent._embedded?.venues?.firstOrNull()?.city?.name ?: "Atlanta")
        } catch (e: Exception) {
            Log.e("TicketmasterRepo", "Error fetching single event $id: ${e.message}")
            null
        }
    }

    private fun mapToEvent(tmEvent: TicketmasterEvent, city: String): Event {
        val imageUrls = tmEvent.images.map { it.url }
        
        // Parse date
        val localDate = tmEvent.dates?.start?.localDate
        val localTime = tmEvent.dates?.start?.localTime
        val startTimestamp = parseTmDate(localDate, localTime)

        // Parse venue details
        val venue = tmEvent._embedded?.venues?.firstOrNull()
        val venueName = venue?.name ?: "Unknown Venue"
        val addressLine = venue?.address?.line1 ?: "Check Ticketmaster link for address"
        val venueCity = venue?.city?.name ?: city
        val latitude = venue?.location?.latitude?.toDoubleOrNull() ?: 0.0
        val longitude = venue?.location?.longitude?.toDoubleOrNull() ?: 0.0

        // Parse Categories dynamically
        val segmentName = tmEvent.classifications?.firstOrNull()?.segment?.name ?: ""
        val mappedCategory = when {
            segmentName.contains("Music", ignoreCase = true) -> EventCategory.LIVE_MUSIC
            segmentName.contains("Sports", ignoreCase = true) -> EventCategory.SPORTS_RECREATION
            segmentName.contains("Arts", ignoreCase = true) || segmentName.contains("Theatre", ignoreCase = true) -> EventCategory.ARTS_CULTURE
            segmentName.contains("Film", ignoreCase = true) -> EventCategory.ARTS_CULTURE
            segmentName.contains("Miscellaneous", ignoreCase = true) -> EventCategory.COMMUNITY_FESTIVALS
            segmentName.contains("Family", ignoreCase = true) -> EventCategory.FAMILY
            else -> EventCategory.NIGHTLIFE // Fallback
        }

        return Event(
            id = tmEvent.id,
            title = tmEvent.name,
            description = tmEvent.description ?: "Description from Ticketmaster not available",
            source = EventSource.TICKETMASTER,
            sourceUrl = tmEvent.url,
            category = mappedCategory,
            startTimestamp = startTimestamp,
            endTimestamp = null,
            cost = tmEvent.priceRanges?.firstOrNull()?.min,
            imageUrls = imageUrls,
            location = Location(
                venueName = venueName,
                address = addressLine,
                city = venueCity,
                latitude = latitude,
                longitude = longitude
            ),
            tags = listOf(segmentName, "VibeCheck Choice").filter { it.isNotBlank() },
            organizerName = tmEvent._embedded?.venues?.firstOrNull()?.name ?: "Event Organizer"
        )
    }

    private fun parseTmDate(localDate: String?, localTime: String?): Long {
        if (localDate == null) return 0L
        val timeStr = localTime ?: "00:00:00"
        
        // Sometimes the time string has a Z at the end, or lacks seconds. We should handle variations.
        val cleanedTimeStr = if (timeStr.contains("Z")) timeStr.replace("Z", "") else timeStr
        val formattedTimeStr = if (cleanedTimeStr.length == 5) "$cleanedTimeStr:00" else cleanedTimeStr

        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            format.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = format.parse("$localDate $formattedTimeStr")
            date?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }
}