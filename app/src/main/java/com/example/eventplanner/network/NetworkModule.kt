package com.example.eventplanner.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object NetworkModule {

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val contentType = "application/json".toMediaType()

    // Ticketmaster Retrofit
    val ticketmasterRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://app.ticketmaster.com/")
        .client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    // Eventbrite Retrofit (Authentication Heartbeat Only)
    val eventbriteRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.eventbriteapi.com/")
        .client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val ticketmasterApi: TicketmasterApiService = ticketmasterRetrofit.create(TicketmasterApiService::class.java)
    val eventbriteApi: EventbriteApiService = eventbriteRetrofit.create(EventbriteApiService::class.java)
}