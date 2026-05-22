package com.example.eventplanner.model

data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val source: EventSource,
    val sourceUrl: String,
    val category: EventCategory,
    val startTimestamp: Long,
    val endTimestamp: Long?,
    val cost: Double?,
    val imageUrls: List<String>,
    val location: Location,
    val tags: List<String>,
    val organizerName: String?
)
