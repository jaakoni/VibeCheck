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
import com.example.eventplanner.repository.TicketmasterRepository
import com.example.eventplanner.ui.screens.SearchHomeScreen
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        // Phase 3.5: Ingestion Test
        // Verify Repository maps API JSON to our Event data model
        testRepositoryMapping()

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

    private fun testRepositoryMapping() {
        val repository = TicketmasterRepository(BuildConfig.TM_API_KEY)
        
        lifecycleScope.launch {
            Log.d("MAPPING_TEST", "--- STARTING REPOSITORY MAPPING TEST ---")
            try {
                val events = repository.fetchEvents("Atlanta")
                Log.d("MAPPING_TEST", "Successfully mapped ${events.size} events:")
                
                events.forEachIndexed { index, event ->
                    Log.d("MAPPING_TEST", "${index + 1}: Title: ${event.title}, ID: ${event.id}, Source: ${event.source}")
                }
            } catch (e: Exception) {
                Log.e("MAPPING_TEST", "Repository mapping failed: ${e.message}")
            }
        }
    }
}