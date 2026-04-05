package com.example.eventplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.model.SearchCriteria
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchHomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchCriteria = MutableStateFlow(SearchCriteria())
    val searchCriteria: StateFlow<SearchCriteria> = _searchCriteria.asStateFlow()

    // Google Places Client & State
    private var placesClient: PlacesClient? = null
    private var sessionToken: AutocompleteSessionToken? = null
    private var searchJob: Job? = null
    
    private val _cityPredictions = MutableStateFlow<List<String>>(emptyList())
    val cityPredictions: StateFlow<List<String>> = _cityPredictions.asStateFlow()

    init {
        // Initialize Places Client if SDK is ready
        if (Places.isInitialized()) {
            placesClient = Places.createClient(application)
            sessionToken = AutocompleteSessionToken.newInstance()
        }
    }

    fun updateCity(city: String) {
        _searchCriteria.update { it.copy(city = city) }
        fetchCityPredictions(city)
    }

    private fun fetchCityPredictions(query: String) {
        // Don't search if query is too short
        if (query.length < 2) {
            _cityPredictions.value = emptyList()
            return
        }

        // Cancel any pending search to avoid spamming the API as the user types fast
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            // Debounce: Wait 300ms before making the API call
            delay(300)
            
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(query)
                // Filter to only show Cities (no exact addresses or businesses)
                .setTypesFilter(listOf(PlaceTypes.CITIES))
                // Optional: Restrict to US to save API calls, remove to search globally
                .setCountries(listOf("US")) 
                .build()

            placesClient?.findAutocompletePredictions(request)
                ?.addOnSuccessListener { response ->
                    val predictions = response.autocompletePredictions.map { it.getFullText(null).toString() }
                    _cityPredictions.value = predictions
                }
                ?.addOnFailureListener { exception ->
                    println("Places API Error: ${exception.message}")
                    _cityPredictions.value = emptyList()
                }
        }
    }

    // Called when the user clicks a specific city from the dropdown list
    fun selectCityPrediction(selectedCity: String) {
        _searchCriteria.update { it.copy(city = selectedCity) }
        _cityPredictions.value = emptyList() // Hide dropdown
        
        // Reset the token so the next search is billed as a new session
        sessionToken = AutocompleteSessionToken.newInstance()
    }

    fun updateDateRange(startMillis: Long?, endMillis: Long?) {
        _searchCriteria.update { 
            it.copy(startDateMillis = startMillis, endDateMillis = endMillis) 
        }
    }

    fun toggleCategory(category: EventCategory) {
        _searchCriteria.update { currentCriteria ->
            val currentSelected = currentCriteria.selectedCategories.toMutableSet()
            if (currentSelected.contains(category)) {
                currentSelected.remove(category)
            } else {
                currentSelected.add(category)
            }
            currentCriteria.copy(selectedCategories = currentSelected)
        }
    }
    
    fun clearCategories() {
        _searchCriteria.update { it.copy(selectedCategories = emptySet()) }
    }

    fun getFormattedDateRange(): String {
        val start = searchCriteria.value.startDateMillis
        val end = searchCriteria.value.endDateMillis
        
        if (start == null) return ""
        
        val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
        val startStr = formatter.format(Date(start))
        
        return if (end != null && start != end) {
            val endStr = formatter.format(Date(end))
            "$startStr - $endStr"
        } else {
            startStr
        }
    }

    fun onSearchClicked() {
        val currentCriteria = _searchCriteria.value
        println("Executing search for: $currentCriteria")
    }
}