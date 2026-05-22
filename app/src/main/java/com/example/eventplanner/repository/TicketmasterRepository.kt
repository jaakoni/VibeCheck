package com.example.eventplanner.repository

import android.util.Log
import com.example.eventplanner.model.Event
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.model.EventSource
import com.example.eventplanner.model.Location
import com.example.eventplanner.network.NetworkModule
import com.example.eventplanner.network.TicketmasterEvent

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

    private fun mapToEvent(tmEvent: TicketmasterEvent, city: String): Event {
        // Find the best image (usually the first one in the list)
        val imageUrls = tmEvent.images.map { it.url }

        return Event(
            id = tmEvent.id,
            title = tmEvent.name,
            description = "No description provided", // Ticketmaster API often requires a separate call for full details
            source = EventSource.TICKETMASTER,
            sourceUrl = tmEvent.url,
            category = EventCategory.LIVE_MUSIC, // Default for now, to be refined in Phase 4
            startTimestamp = 0L, // Will be parsed once we add date fields to TicketmasterResponse
            endTimestamp = null,
            cost = null,
            imageUrls = imageUrls,
            location = Location(
                venueName = "Unknown Venue",
                address = "Check Ticketmaster link for address",
                city = city,
                latitude = 0.0,
                longitude = 0.0
            ),
            tags = emptyList(),
            organizerName = null
        )
    }
}