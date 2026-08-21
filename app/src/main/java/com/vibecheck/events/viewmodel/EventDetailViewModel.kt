package com.vibecheck.events.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibecheck.events.BuildConfig
import com.vibecheck.events.model.Event
import com.vibecheck.events.model.WeatherPeriod
import com.vibecheck.events.network.NetworkModule
import com.vibecheck.events.repository.TicketmasterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EventDetailUiState {
    object Loading : EventDetailUiState
    data class Success(val event: Event, val weather: List<WeatherPeriod>?) : EventDetailUiState
    data class Error(val message: String) : EventDetailUiState
}

class EventDetailViewModel : ViewModel() {

    private val repository = TicketmasterRepository(BuildConfig.TM_API_KEY)

    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.value = EventDetailUiState.Loading
            try {
                val event = repository.fetchEventById(eventId)
                val weather = fetchWeatherForecast(event.location.latitude, event.location.longitude)
                _uiState.value = EventDetailUiState.Success(event, weather)
            } catch (e: Exception) {
                _uiState.value = EventDetailUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    private suspend fun fetchWeatherForecast(latitude: Double, longitude: Double): List<WeatherPeriod>? {
        // NWS requires lat/lon coordinates to be within the United States.
        // If coordinates are 0.0 (default/unknown), skip weather lookup.
        if ((latitude == 0.0) && (longitude == 0.0)) {
            return null
        }

        return try {
            // Step 1: Query Points Metadata to get the Grid Forecast URL
            val pointsResponse = NetworkModule.weatherApi.getPointsMetadata(latitude, longitude)
            val forecastUrl = pointsResponse.properties.forecast

            // Step 2: Retrieve actual Forecast periods from that URL
            val forecastResponse = NetworkModule.weatherApi.getForecast(forecastUrl)
            
            // Map DTO models to our clean Domain Models
            forecastResponse.properties.periods.map { periodDto ->
                WeatherPeriod(
                    name = periodDto.name,
                    temperature = periodDto.temperature,
                    temperatureUnit = periodDto.temperatureUnit,
                    shortForecast = periodDto.shortForecast,
                    iconUrl = periodDto.icon,
                )
            }
        } catch (e: Exception) {
            Log.e("EventDetailViewModel", "NWS Weather API error: ${e.message}")
            null // Return null gracefully, the UI will fall back gracefully or show empty outlook
        }
    }
}