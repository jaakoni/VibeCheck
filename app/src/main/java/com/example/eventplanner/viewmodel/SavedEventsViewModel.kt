package com.example.eventplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.model.Event
import com.example.eventplanner.repository.SavedEventsRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedEventsViewModel(
    private val savedEventsRepository: SavedEventsRepository = SavedEventsRepository(),
) : ViewModel() {

    private val _savedEvents = MutableStateFlow<List<Event>>(emptyList())
    val savedEvents: StateFlow<List<Event>> = _savedEvents.asStateFlow()

    val savedEventIds: StateFlow<Set<String>> = _savedEvents
        .map { events -> events.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private var observeJob: Job? = null

    fun setUser(user: FirebaseUser?) {
        observeJob?.cancel()
        if (user == null) {
            _savedEvents.value = emptyList()
            return
        }
        observeJob = viewModelScope.launch {
            savedEventsRepository.getSavedEventsFlow(user.uid).collect { events ->
                _savedEvents.value = events
            }
        }
    }

    fun toggleSaveEvent(
        user: FirebaseUser?,
        event: Event,
        onRequireAuth: () -> Unit,
    ) {
        if (user == null) {
            onRequireAuth()
            return
        }

        viewModelScope.launch {
            val isCurrentlySaved = savedEventIds.value.contains(event.id)
            if (isCurrentlySaved) {
                savedEventsRepository.removeEvent(user.uid, event.id)
            } else {
                savedEventsRepository.saveEvent(user.uid, event)
            }
        }
    }
}
