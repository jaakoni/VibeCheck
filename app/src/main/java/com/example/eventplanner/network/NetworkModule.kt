package com.example.eventplanner.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkModule {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Prints raw request/response to Logcat
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Ticketmaster Base URL
    val ticketmasterRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://app.ticketmaster.com/")
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create()) // Temporary: Allows reading raw String
        .build()

    // Eventbrite Base URL
    val eventbriteRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.eventbriteapi.com/")
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create()) // Temporary: Allows reading raw String
        .build()

    val ticketmasterApi: TicketmasterApiService = ticketmasterRetrofit.create(TicketmasterApiService::class.java)
    val eventbriteApi: EventbriteApiService = eventbriteRetrofit.create(EventbriteApiService::class.java)
}