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

class SearchResultsViewModel : ViewModel() {

    private val repository = TicketmasterRepository(BuildConfig.TM_API_KEY)

    private val _eventsState = MutableStateFlow<SearchResultsUiState>(SearchResultsUiState.Loading)
    val eventsState: StateFlow<SearchResultsUiState> = _eventsState.asStateFlow()

    fun searchEvents(city: String) {
        viewModelScope.launch {
            _eventsState.value = SearchResultsUiState.Loading
            try {
                // Strip state/country if returned by Places API (e.g., "Atlanta, GA, USA" -> "Atlanta")
                val cleanCity = city.split(",").first().trim()
                
                val fetchedEvents = repository.fetchEvents(cleanCity)
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