package com.example.eventplanner.viewmodel

import com.example.eventplanner.model.Event
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.model.EventSource
import com.example.eventplanner.model.Location
import com.example.eventplanner.repository.TicketmasterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class SearchResultsViewModelTest {

    private lateinit var viewModel: SearchResultsViewModel
    private lateinit var mockRepository: TicketmasterRepository

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        mockRepository = mockk()

        viewModel = SearchResultsViewModel()
        
        // Use reflection to inject the mocked repository since it's instantiated inside the ViewModel
        val field: Field = SearchResultsViewModel::class.java.getDeclaredField("repository")
        field.isAccessible = true
        field.set(viewModel, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchEvents fetches events and updates state to Success`() = runTest {
        val mockEvents = listOf(
            Event(
                id = "1", title = "Concert", description = null, source = EventSource.TICKETMASTER,
                sourceUrl = "", category = EventCategory.LIVE_MUSIC, startTimestamp = 0L, endTimestamp = null,
                cost = null, imageUrls = emptyList(), location = Location("Venue", "", "", 0.0, 0.0),
                tags = emptyList(), organizerName = null
            )
        )
        
        coEvery { mockRepository.fetchEvents("Atlanta") } returns mockEvents

        viewModel.searchEvents("Atlanta, GA, USA") // Test city parsing

        // Advance coroutines
        testScheduler.advanceUntilIdle()

        val state = viewModel.eventsState.value
        assertTrue(state is SearchResultsUiState.Success)
        assertEquals(1, (state as SearchResultsUiState.Success).events.size)
    }

    @Test
    fun `searchEvents with no results updates state to Empty`() = runTest {
        coEvery { mockRepository.fetchEvents("Nowhere") } returns emptyList()

        viewModel.searchEvents("Nowhere")
        testScheduler.advanceUntilIdle()

        val state = viewModel.eventsState.value
        assertTrue(state is SearchResultsUiState.Empty)
    }
}
