package com.example.eventplanner.model

data class WeatherForecast(
    val periods: List<WeatherPeriod>
)

data class WeatherPeriod(
    val name: String,             // e.g., "Today", "Tonight", "Monday"
    val temperature: Int,         // e.g., 72
    val temperatureUnit: String,  // "F"
    val shortForecast: String,    // e.g., "Partly Cloudy"
    val iconUrl: String           // Icon URL from NWS
)