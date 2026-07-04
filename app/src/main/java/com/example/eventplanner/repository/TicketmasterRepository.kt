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

    suspend fun fetchEvents(city: String): List<Event> {
        return try {
            val response = NetworkModule.ticketmasterApi.searchEvents(apiKey, city)
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

        return Event(
            id = tmEvent.id,
            title = tmEvent.name,
            description = tmEvent.description ?: "Join us for an immersive experience! Check out the event website for more details about scheduling, speakers, and special features.",
            source = EventSource.TICKETMASTER,
            sourceUrl = tmEvent.url,
            category = EventCategory.LIVE_MUSIC, // Default for now
            startTimestamp = startTimestamp,
            endTimestamp = null,
            cost = null,
            imageUrls = imageUrls,
            location = Location(
                venueName = venueName,
                address = addressLine,
                city = venueCity,
                latitude = latitude,
                longitude = longitude
            ),
            tags = listOf("Concert", "Live Music", "VibeCheck Choice"),
            organizerName = tmEvent._embedded?.venues?.firstOrNull()?.name ?: "Event Organizer"
        )
    }

    private fun parseTmDate(localDate: String?, localTime: String?): Long {
        if (localDate == null) return 0L
        val timeStr = localTime ?: "00:00:00"
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val date = format.parse("$localDate $timeStr")
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}