package com.example.eventplanner.repository

import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.network.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TicketmasterRepositoryTest {

    private lateinit var mockApiService: TicketmasterApiService
    private lateinit var repository: TicketmasterRepository

    @Before
    fun setup() {
        mockApiService = mockk()
        // Inject the mocked API service into the repository via a testing setter or reflection if necessary.
        // For simplicity in this test, we'll assume the API service can be mocked directly or we modify
        // the NetworkModule (or construct a repository taking the service as a parameter).
        // To make this perfectly testable without modifying the original code, we can use mockkObject
        // on NetworkModule, but ideally the repository accepts the api service in its constructor.
    }
    
    // As the repository currently hardcodes NetworkModule.ticketmasterApi, we will mock the singleton
    @Test
    fun `test fetchEvents successfully maps DTOs to internal Event model`() = runTest {
        io.mockk.mockkObject(NetworkModule)
        io.mockk.every { NetworkModule.ticketmasterApi } returns mockApiService
        
        repository = TicketmasterRepository("fake-api-key")

        val mockResponse = TicketmasterResponse(
            _embedded = EmbeddedEvents(
                events = listOf(
                    TicketmasterEvent(
                        id = "1",
                        name = "Mock Event",
                        url = "http://mock.com",
                        dates = TicketmasterDates(TicketmasterStartDates("2026-08-14", "20:00:00")),
                        images = listOf(TicketmasterImage("http://mock-image.com")),
                        _embedded = TicketmasterEventEmbedded(
                            venues = listOf(
                                TicketmasterVenue(
                                    name = "Mock Venue",
                                    address = TicketmasterAddress("123 Mock St"),
                                    city = TicketmasterCity("Mock City"),
                                    location = TicketmasterCoords("33.0", "-84.0")
                                )
                            )
                        )
                    )
                )
            )
        )

        coEvery { mockApiService.searchEvents(any(), any(), any(), any(), any()) } returns mockResponse

        val events = repository.fetchEvents("Mock City")

        assertEquals(1, events.size)
        val event = events.first()
        assertEquals("1", event.id)
        assertEquals("Mock Event", event.title)
        assertEquals("http://mock.com", event.sourceUrl)
        assertEquals(EventCategory.LIVE_MUSIC, event.category)
        assertEquals("Mock Venue", event.location.venueName)
        assertEquals(33.0, event.location.latitude, 0.0)
        
        io.mockk.unmockkObject(NetworkModule)
    }

    @Test
    fun `test fetchEvents returns empty list when API returns no events`() = runTest {
        io.mockk.mockkObject(NetworkModule)
        io.mockk.every { NetworkModule.ticketmasterApi } returns mockApiService
        
        repository = TicketmasterRepository("fake-api-key")

        val mockResponse = TicketmasterResponse(_embedded = null) // No events

        coEvery { mockApiService.searchEvents(any(), any(), any(), any(), any()) } returns mockResponse

        val events = repository.fetchEvents("Empty City")

        assertTrue(events.isEmpty())
        
        io.mockk.unmockkObject(NetworkModule)
    }
}
