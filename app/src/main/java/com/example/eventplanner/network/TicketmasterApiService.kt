package com.example.eventplanner.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Ticketmaster Models for Serialization
@Serializable
data class TicketmasterResponse(
    val _embedded: EmbeddedEvents? = null
)

@Serializable
data class EmbeddedEvents(
    val events: List<TicketmasterEvent>
)

@Serializable
data class TicketmasterEvent(
    val id: String,
    val name: String,
    val url: String,
    val images: List<TicketmasterImage> = emptyList(),
    val description: String? = null,
    val dates: TicketmasterDates? = null,
    val _embedded: TicketmasterEventEmbedded? = null
)

@Serializable
data class TicketmasterImage(
    val url: String
)

@Serializable
data class TicketmasterDates(
    val start: TicketmasterStartDates? = null
)

@Serializable
data class TicketmasterStartDates(
    val localDate: String? = null,
    val localTime: String? = null
)

@Serializable
data class TicketmasterEventEmbedded(
    val venues: List<TicketmasterVenue>? = null
)

@Serializable
data class TicketmasterVenue(
    val name: String? = null,
    val address: TicketmasterAddress? = null,
    val city: TicketmasterCity? = null,
    val location: TicketmasterCoords? = null
)

@Serializable
data class TicketmasterAddress(
    val line1: String? = null
)

@Serializable
data class TicketmasterCity(
    val name: String? = null
)

@Serializable
data class TicketmasterCoords(
    val latitude: String? = null,
    val longitude: String? = null
)

interface TicketmasterApiService {
    @GET("discovery/v2/events.json")
    suspend fun searchEvents(
        @Query("apikey") apiKey: String,
        @Query("city") city: String,
        @Query("size") size: Int = 20
    ): TicketmasterResponse

    @GET("discovery/v2/events/{id}.json")
    suspend fun getEventDetails(
        @Path("id") id: String,
        @Query("apikey") apiKey: String
    ): TicketmasterEvent
}