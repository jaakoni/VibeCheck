package com.example.eventplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.BuildConfig
import com.example.eventplanner.model.Event
import com.example.eventplanner.repository.TicketmasterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SearchResultsViewModel : ViewModel() {

    private val repository = TicketmasterRepository(BuildConfig.TM_API_KEY)

    private val _eventsState = MutableStateFlow<SearchResultsUiState>(SearchResultsUiState.Loading)
    val eventsState: StateFlow<SearchResultsUiState> = _eventsState.asStateFlow()

    fun searchEvents(city: String, startDate: Long? = null, endDate: Long? = null) {
        viewModelScope.launch {
            _eventsState.value = SearchResultsUiState.Loading
            try {
                // Strip state/country if returned by Places API (e.g., "Atlanta, GA, USA" -> "Atlanta")
                val cleanCity = city.split(",").first().trim()

                var startDateTime: String? = null
                var endDateTime: String? = null
                
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }

                if (startDate != null) {
                    startDateTime = formatter.format(Date(startDate))
                }
                if (endDate != null) {
                    // Add 86399000L to encompass the full end day up to 23:59:59
                    endDateTime = formatter.format(Date(endDate + 86399000L))
                }

                val fetchedEvents = repository.fetchEvents(cleanCity, startDateTime, endDateTime)
                if (fetchedEvents.isEmpty()) {
                    _eventsState.value = SearchResultsUiState.Empty
                } else {
                    _eventsState.value = SearchResultsUiState.Success(fetchedEvents)
                }
            } catch (e: Exception) {
                _eventsState.value = SearchResultsUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

sealed interface SearchResultsUiState {
    object Loading : SearchResultsUiState
    object Empty : SearchResultsUiState
    data class Success(val events: List<Event>) : SearchResultsUiState
    data class Error(val message: String) : SearchResultsUiState
}