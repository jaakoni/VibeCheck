package com.example.eventplanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.eventplanner.network.NetworkModule
import com.example.eventplanner.ui.screens.SearchHomeScreen
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the Places SDK securely using the local.properties key
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        // Diagnostic API Connection Test (Phase 3.3)
        // This runs once when the app launches and prints raw JSON to Logcat
        testApiConnections()

        setContent {
            EventPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchHomeScreen()
                }
            }
        }
    }

    private fun testApiConnections() {
        lifecycleScope.launch {
            try {
                Log.d("API_TEST", "--- STARTING TICKETMASTER TEST ---")
                // Testing Ticketmaster with hardcoded "Atlanta"
                val tmResponse = NetworkModule.ticketmasterApi.searchEvents(
                    apiKey = BuildConfig.TM_API_KEY,
                    city = "Atlanta"
                )
                Log.d("API_TEST", "Ticketmaster Success! First 500 chars:")
                Log.d("API_TEST", tmResponse.take(500))

            } catch (e: Exception) {
                Log.e("API_TEST", "Ticketmaster Failed: ${e.message}")
            }

            try {
                Log.d("API_TEST", "--- STARTING EVENTBRITE TEST ---")
                // Testing Eventbrite with a hardcoded Org ID (Atlanta Tech Village)
                val ebResponse = NetworkModule.eventbriteApi.getOrganizationEvents(
                    bearerToken = "Bearer ${BuildConfig.EB_API_KEY}",
                    organizationId = "15467382910" 
                )
                Log.d("API_TEST", "Eventbrite Success! First 500 chars:")
                Log.d("API_TEST", ebResponse.take(500))

            } catch (e: Exception) {
                Log.e("API_TEST", "Eventbrite Failed: ${e.message}")
            }
        }
    }
}