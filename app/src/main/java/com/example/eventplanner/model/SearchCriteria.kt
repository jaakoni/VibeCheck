package com.example.eventplanner.model

data class SearchCriteria(
    val city: String = "",
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val selectedCategories: Set<EventCategory> = emptySet(), // Changed to a Set for multi-select
    val selectedSource: EventSource? = null // For the Figma Source Selector chips
)