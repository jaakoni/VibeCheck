package com.example.eventplanner.network

import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterApiService {
    // Ticketmaster Discovery API: Search events by city
    // Example: /discovery/v2/events.json?apikey={key}&city={city}
    @GET("discovery/v2/events.json")
    suspend fun searchEvents(
        @Query("apikey") apiKey: String,
        @Query("city") city: String,
        @Query("size") size: Int = 20
    ): String // Returning String temporarily to inspect raw JSON in logs
}