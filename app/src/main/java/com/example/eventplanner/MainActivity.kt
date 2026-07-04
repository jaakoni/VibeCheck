package com.example.eventplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventplanner.ui.screens.EventDetailScreen
import com.example.eventplanner.ui.screens.SearchHomeScreen
import com.example.eventplanner.ui.screens.SearchResultsScreen
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.google.android.libraries.places.api.Places
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        setContent {
            EventPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "search_home"
                    ) {
                        composable("search_home") {
                            SearchHomeScreen(
                                onSearchClicked = { city ->
                                    // URL encode the city string to securely support spaces (e.g. "New York" -> "New%20York")
                                    val encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString())
                                    navController.navigate("search_results/$encodedCity")
                                }
                            )
                        }
                        
                        // Transition path expects city argument
                        composable(
                            route = "search_results/{city}",
                            arguments = listOf(navArgument("city") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val city = backStackEntry.arguments?.getString("city") ?: "Atlanta"
                            SearchResultsScreen(
                                city = city,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onEventClick = { event ->
                                    navController.navigate("event_detail/${event.id}")
                                }
                            )
                        }

                        // Event details page route
                        composable(
                            route = "event_detail/{eventId}",
                            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                            EventDetailScreen(
                                eventId = eventId,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}