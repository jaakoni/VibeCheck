package com.vibecheck.events.model

data class Location(
    val venueName: String,           // e.g., "Innovation Center Plaza"
    val address: String,             // e.g., "450 Market Street, San Francisco, CA"
    val city: String,                // Crucial for the "Enter City" search filtering
    val latitude: Double,            // Required for the Weather Widget & Maps
    val longitude: Double            // Required for the Weather Widget & Maps
)
