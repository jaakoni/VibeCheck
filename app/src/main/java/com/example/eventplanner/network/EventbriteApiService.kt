package com.example.eventplanner.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface EventbriteApiService {
    // Eventbrite Organization API: Pull events for specific org ID
    // Example: /v3/organizations/{org_id}/events/
    @GET("v3/organizations/{org_id}/events/")
    suspend fun getOrganizationEvents(
        @Header("Authorization") bearerToken: String,
        @Path("org_id") organizationId: String
    ): String // Returning String temporarily to inspect raw JSON in logs
}