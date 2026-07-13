package com.example.eventplanner.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.eventplanner.model.Event
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.model.EventSource
import com.example.eventplanner.model.Location
import com.example.eventplanner.ui.screens.SearchResultsScreen
import com.example.eventplanner.viewmodel.SearchResultsUiState
import com.example.eventplanner.viewmodel.SearchResultsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SearchResultsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCategoryFilterClickUpdatesUI() {
        // Setup mock view model with some fake data
        val mockViewModel = mockk<SearchResultsViewModel>(relaxed = true)
        val mockState = MutableStateFlow<SearchResultsUiState>(
            SearchResultsUiState.Success(
                listOf(
                    Event(
                        id = "1", title = "Rock Concert", description = null,
                        source = EventSource.TICKETMASTER, sourceUrl = "", category = EventCategory.LIVE_MUSIC,
                        startTimestamp = 0L, endTimestamp = null, cost = null, imageUrls = emptyList(),
                        location = Location("Venue", "", "", 0.0, 0.0), tags = emptyList(), organizerName = null
                    ),
                    Event(
                        id = "2", title = "Soccer Game", description = null,
                        source = EventSource.TICKETMASTER, sourceUrl = "", category = EventCategory.SPORTS_RECREATION,
                        startTimestamp = 0L, endTimestamp = null, cost = null, imageUrls = emptyList(),
                        location = Location("Venue", "", "", 0.0, 0.0), tags = emptyList(), organizerName = null
                    )
                )
            )
        )

        every { mockViewModel.eventsState } returns mockState

        composeTestRule.setContent {
            SearchResultsScreen(
                city = "Atlanta",
                onBackClick = {},
                onEventClick = {},
                viewModel = mockViewModel
            )
        }

        // Before filtering, both should be visible
        composeTestRule.onNodeWithText("Rock Concert").assertIsDisplayed()
        composeTestRule.onNodeWithText("Soccer Game").assertIsDisplayed()

        // Click the LIVE MUSIC category filter pill (targets the first instance, which is the Pill, not the Card)
        composeTestRule.onAllNodesWithText(EventCategory.LIVE_MUSIC.displayName).onFirst().performClick()

        // After filtering, Rock Concert should be visible, Soccer Game should not
        composeTestRule.onNodeWithText("Rock Concert").assertIsDisplayed()
        composeTestRule.onNodeWithText("Soccer Game").assertDoesNotExist()
    }

    @Test
    fun testSourceFilterClickUpdatesUI() {
        val mockViewModel = mockk<SearchResultsViewModel>(relaxed = true)
        val mockState = MutableStateFlow<SearchResultsUiState>(
            SearchResultsUiState.Success(
                listOf(
                    Event(
                        id = "1", title = "TM Event", description = null,
                        source = EventSource.TICKETMASTER, sourceUrl = "", category = EventCategory.LIVE_MUSIC,
                        startTimestamp = 0L, endTimestamp = null, cost = null, imageUrls = emptyList(),
                        location = Location("Venue", "", "", 0.0, 0.0), tags = emptyList(), organizerName = null
                    ),
                    Event(
                        id = "2", title = "EB Event", description = null,
                        source = EventSource.EVENTBRITE, sourceUrl = "", category = EventCategory.LIVE_MUSIC,
                        startTimestamp = 0L, endTimestamp = null, cost = null, imageUrls = emptyList(),
                        location = Location("Venue", "", "", 0.0, 0.0), tags = emptyList(), organizerName = null
                    )
                )
            )
        )

        every { mockViewModel.eventsState } returns mockState

        composeTestRule.setContent {
            SearchResultsScreen(
                city = "Atlanta",
                onBackClick = {},
                onEventClick = {},
                viewModel = mockViewModel
            )
        }

        composeTestRule.onNodeWithText("TM Event").assertIsDisplayed()
        composeTestRule.onNodeWithText("EB Event").assertIsDisplayed()

        // Click the Eventbrite filter
        composeTestRule.onNodeWithText("Eventbrite").performClick()

        // Only EB event should show
        composeTestRule.onNodeWithText("EB Event").assertIsDisplayed()
        composeTestRule.onNodeWithText("TM Event").assertDoesNotExist()
    }
}
