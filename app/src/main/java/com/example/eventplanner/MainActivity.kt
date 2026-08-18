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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventplanner.ui.screens.EventDetailScreen
import com.example.eventplanner.ui.screens.LoginScreen
import com.example.eventplanner.ui.screens.ProfileScreen
import com.example.eventplanner.ui.screens.SearchHomeScreen
import com.example.eventplanner.ui.screens.SearchResultsScreen
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.example.eventplanner.viewmodel.AuthViewModel
import com.example.eventplanner.viewmodel.SavedEventsViewModel
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
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val savedEventsViewModel: SavedEventsViewModel = viewModel()
                    val currentUser by authViewModel.currentUser.collectAsState()

                    LaunchedEffect(currentUser) {
                        savedEventsViewModel.setUser(currentUser)
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "search_home",
                    ) {
                        composable("search_home") {
                            SearchHomeScreen(
                                onSearchClicked = { city, categories, start, end ->
                                    val encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString())
                                    val queryParams = mutableListOf<String>()
                                    if (start != null) {
                                        val finalEnd = end ?: start
                                        queryParams.add("start=$start")
                                        queryParams.add("end=$finalEnd")
                                    }
                                    if (categories.isNotEmpty()) {
                                        val categoryString = categories.joinToString(",") { it.name }
                                        val encodedCategory = URLEncoder.encode(categoryString, StandardCharsets.UTF_8.toString())
                                        queryParams.add("category=$encodedCategory")
                                    }
                                    val queryString = if (queryParams.isNotEmpty()) "?" + queryParams.joinToString("&") else ""
                                    navController.navigate("search_results/$encodedCity$queryString")
                                },
                                onTrendingCategoryClicked = { city, category, start, end ->
                                    val encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString())
                                    val encodedCategory = URLEncoder.encode(category.name, StandardCharsets.UTF_8.toString())
                                    navController.navigate("search_results/$encodedCity?start=$start&end=$end&category=$encodedCategory")
                                },
                                onProfileClick = {
                                    if (currentUser != null) {
                                        navController.navigate("profile")
                                    } else {
                                        navController.navigate("login")
                                    }
                                },
                            )
                        }

                        // Login Screen Route
                        composable("login") {
                            LoginScreen(
                                authViewModel = authViewModel,
                                webClientId = "960871076699-aih87oghdcrg70iij4sdif0dl536b3rj.apps.googleusercontent.com",
                                onLoginSuccess = {
                                    navController.navigate("profile") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                },
                            )
                        }

                        // User Profile Route
                        composable("profile") {
                            ProfileScreen(
                                authViewModel = authViewModel,
                                savedEventsViewModel = savedEventsViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSignOutSuccess = {
                                    navController.navigate("search_home") {
                                        popUpTo("search_home") { inclusive = true }
                                    }
                                },
                                onEventClick = { event ->
                                    navController.navigate("event_detail/${event.id}")
                                },
                            )
                        }
                        
                        // Transition path expects city argument and optional start/end date and category filters
                        composable(
                            route = "search_results/{city}?start={start}&end={end}&category={category}",
                            arguments = listOf(
                                navArgument("city") { type = NavType.StringType },
                                navArgument("start") { type = NavType.LongType; defaultValue = -1L },
                                navArgument("end") { type = NavType.LongType; defaultValue = -1L },
                                navArgument("category") { type = NavType.StringType; nullable = true }
                            )
                        ) { backStackEntry ->
                            val city = backStackEntry.arguments?.getString("city") ?: "Atlanta"
                            val start = backStackEntry.arguments?.getLong("start") ?: -1L
                            val end = backStackEntry.arguments?.getLong("end") ?: -1L
                            val categoryStr = backStackEntry.arguments?.getString("category")
                            SearchResultsScreen(
                                city = city,
                                startDate = if (start != -1L) start else null,
                                endDate = if (end != -1L) end else null,
                                initialCategory = categoryStr,
                                currentUser = currentUser,
                                savedEventsViewModel = savedEventsViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onEventClick = { event ->
                                    navController.navigate("event_detail/${event.id}")
                                },
                                onProfileClick = {
                                    if (currentUser != null) {
                                        navController.navigate("profile")
                                    } else {
                                        navController.navigate("login")
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                },
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