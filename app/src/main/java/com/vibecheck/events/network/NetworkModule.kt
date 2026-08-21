package com.vibecheck.events.network

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

    // NWS requires a custom User-Agent header to avoid 403 Forbidden errors
    private val weatherClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "VibeCheckApp/1.0 (contact@vibecheckapp.example.com)")
                .build()
            chain.proceed(request)
        }
        .build()

    private val contentType = "application/json".toMediaType()

    // Ticketmaster Retrofit
    val ticketmasterRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://app.ticketmaster.com/")
        .client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    // National Weather Service (NWS) Retrofit
    val weatherRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.weather.gov/")
        .client(weatherClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val ticketmasterApi: TicketmasterApiService = ticketmasterRetrofit.create(TicketmasterApiService::class.java)
    val weatherApi: WeatherApiService = weatherRetrofit.create(WeatherApiService::class.java)
}