package com.example.eventplanner.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
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
    val images: List<TicketmasterImage>
)

@Serializable
data class TicketmasterImage(
    val url: String
)

interface TicketmasterApiService {
    @GET("discovery/v2/events.json")
    suspend fun searchEvents(
        @Query("apikey") apiKey: String,
        @Query("city") city: String,
        @Query("size") size: Int = 20
    ): TicketmasterResponse
}