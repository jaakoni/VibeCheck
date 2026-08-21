package com.vibecheck.events.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                _currentUser.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Google sign-in failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _currentUser.value = null
    }
}
