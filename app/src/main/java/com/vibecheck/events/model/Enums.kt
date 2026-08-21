package com.vibecheck.events.model

// The exact 10 categories from your PRD
enum class EventCategory(val displayName: String) {
    NIGHTLIFE("Nightlife & Parties"),
    FAMILY("Family & Kids"),
    FOOD_DRINK("Food & Drink"),
    LIVE_MUSIC("Live Music"),
    ARTS_CULTURE("Arts & Culture"),
    HEALTH_WELLNESS("Health & Wellness"),
    COMMUNITY_FESTIVALS("Community & Festivals"),
    WORKSHOPS("Workshops & Classes"),
    PROFESSIONAL_NETWORKING("Professional & Networking"),
    SPORTS_RECREATION("Sports & Recreation")
}

// The sources defined in the PRD and Figma Source Selector
// Note: We are using Ticketmaster and Eventbrite for the MVP mock data per your latest update
enum class EventSource(val displayName: String) {
    EVENTBRITE("Eventbrite"),
    TICKETMASTER("Ticketmaster"),
    MEETUP("Meetup"), // Placeholder for fast-follow
    POSH("Posh.vip"), // Placeholder for scraper
    LUMA("Luma")      // Placeholder for scraper
}
