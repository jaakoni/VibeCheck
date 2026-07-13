package com.example.eventplanner.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.eventplanner.model.Event
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.model.EventSource
import com.example.eventplanner.model.Location
import com.example.eventplanner.ui.screens.EventDetailScreen
import com.example.eventplanner.viewmodel.EventDetailUiState
import com.example.eventplanner.viewmodel.EventDetailViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class EventDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEventDetailDisplaysCorrectInformation() {
        val mockViewModel = mockk<EventDetailViewModel>(relaxed = true)

        // Mock event matching Requirement #3
        val mockEvent = Event(
            id = "mock-id-123",
            title = "The Ultimate Hackathon 2026",
            description = "Join the biggest coding event of the year.",
            source = EventSource.TICKETMASTER,
            sourceUrl = "https://mock.com",
            category = EventCategory.WORKSHOPS,
            startTimestamp = 1783300000000L, // Some future date
            endTimestamp = null,
            cost = 15.50,
            imageUrls = emptyList(),
            location = Location(
                venueName = "Tech Convention Center",
                address = "404 Hacker Way",
                city = "Atlanta",
                latitude = 33.7,
                longitude = -84.3
            ),
            tags = emptyList(),
            organizerName = "Jane Doe"
        )

        val mockState = MutableStateFlow<EventDetailUiState>(
            EventDetailUiState.Success(event = mockEvent, weather = emptyList())
        )
        every { mockViewModel.uiState } returns mockState

        composeTestRule.setContent {
            EventDetailScreen(
                eventId = "mock-id-123",
                onBackClick = {},
                viewModel = mockViewModel
            )
        }

        // Verify Title
        composeTestRule.onNodeWithText("The Ultimate Hackathon 2026").assertExists()

        // Verify Description
        composeTestRule.onNodeWithText("Join the biggest coding event of the year.").assertExists()

        // Verify Venue and Address (both appear twice in UI: Header Card and Map section)
        composeTestRule.onAllNodesWithText("Tech Convention Center").onFirst().assertExists()
        composeTestRule.onAllNodesWithText("404 Hacker Way").onFirst().assertExists()

        // Verify Organizer
        composeTestRule.onNodeWithText("Jane Doe").assertExists()
    }
}
