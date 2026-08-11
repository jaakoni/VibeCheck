package com.example.eventplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                },
            ) { error ->
                _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Google sign-in failed")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }
}
