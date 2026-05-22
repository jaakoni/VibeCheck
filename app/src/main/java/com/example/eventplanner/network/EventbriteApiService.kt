package com.example.eventplanner.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Header

@Serializable
data class EventbriteUser(
    val id: String,
    val name: String
)

interface EventbriteApiService {
    // Authentication Heartbeat: Only use this to verify the token
    @GET("v3/users/me/")
    suspend fun getMyUserDetails(
        @Header("Authorization") bearerToken: String
    ): EventbriteUser
}