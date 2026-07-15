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
                                onSearchClicked = { city, start, end ->
                                    // URL encode the city string to securely support spaces (e.g. "New York" -> "New%20York")
                                    val encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString())
                                    val route = "search_results/$encodedCity" +
                                        if (start != null && end != null) "?start=$start&end=$end" else ""
                                    navController.navigate(route)
                                }
                            )
                        }
                        
                        // Transition path expects city argument and optional start/end date filters
                        composable(
                            route = "search_results/{city}?start={start}&end={end}",
                            arguments = listOf(
                                navArgument("city") { type = NavType.StringType },
                                navArgument("start") { type = NavType.LongType; defaultValue = -1L },
                                navArgument("end") { type = NavType.LongType; defaultValue = -1L }
                            )
                        ) { backStackEntry ->
                            val city = backStackEntry.arguments?.getString("city") ?: "Atlanta"
                            val start = backStackEntry.arguments?.getLong("start") ?: -1L
                            val end = backStackEntry.arguments?.getLong("end") ?: -1L
                            SearchResultsScreen(
                                city = city,
                                startDate = if (start != -1L) start else null,
                                endDate = if (end != -1L) end else null,
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