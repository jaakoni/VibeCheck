package com.example.eventplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.util.Locale
import com.example.eventplanner.model.Event
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.model.EventSource
import com.example.eventplanner.viewmodel.SearchResultsUiState
import com.example.eventplanner.viewmodel.SearchResultsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    city: String,
    startDate: Long? = null,
    endDate: Long? = null,
    onBackClick: () -> Unit,
    onEventClick: (Event) -> Unit,
    viewModel: SearchResultsViewModel = viewModel(),
) {
    var selectedDayFilter by remember { mutableStateOf("Today") }
    var selectedCategoryFilter by remember { mutableStateOf<EventCategory?>(null) }
    var selectedSourceFilter by remember { mutableStateOf("All Sources") }

    val uiState by viewModel.eventsState.collectAsState()

    // Trigger search automatically when the screen loads
    LaunchedEffect(city) {
        viewModel.searchEvents(city)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VibeCheck", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search shortcut */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { BottomNavigationPlaceholder() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FF)) // Figma Surface Color
        ) {
            
            // Editorial Header Section
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(
                    text = "Find your vibe",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF05345C) // Figma Title Color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Curated experiences happening in $city this week.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF3D618C)
                )
            }

            // Filters Section (Day, Category, Source selectors matching Figma)
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Day Selector Horizontal Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val days = listOf("Today", "Mon 12", "Tue 13", "Wed 14", "Thu 15", "Fri 16")
                    items(days) { day ->
                        val isSelected = selectedDayFilter == day
                        DayChip(day = day, isSelected = isSelected) {
                            selectedDayFilter = day
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selector Horizontal Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(EventCategory.entries) { category ->
                        val isSelected = selectedCategoryFilter == category
                        CategoryFilterChip(category = category, isSelected = isSelected) {
                            selectedCategoryFilter = if (isSelected) null else category
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Source Selector Title & Row
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "EVENT SOURCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D618C),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val sources = listOf("All Sources", "Eventbrite", "Ticketmaster", "Luma")
                        items(sources) { source ->
                            val isSelected = selectedSourceFilter == source
                            SourceChip(source = source, isSelected = isSelected) {
                                selectedSourceFilter = source
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // UI State Representation
            when (val state = uiState) {
                is SearchResultsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5450C1))
                    }
                }
                is SearchResultsUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No events found in $city. Adjust your filters or try another city!",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
                is SearchResultsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
                is SearchResultsUiState.Success -> {
                    val filteredEvents = state.events.filter { event ->
                        val matchesCategory = selectedCategoryFilter == null || event.category == selectedCategoryFilter
                        val matchesSource = when (selectedSourceFilter) {
                            "All Sources" -> true
                            "Eventbrite" -> event.source == EventSource.EVENTBRITE
                            "Ticketmaster" -> event.source == EventSource.TICKETMASTER
                            "Luma" -> event.source == EventSource.LUMA
                            else -> true
                        }
                        val matchesDate = if (startDate != null && endDate != null) {
                            // Ensure the event start falls within the selected range (adding 24 hours in millis to the end date to include the full final day)
                            event.startTimestamp >= startDate && event.startTimestamp <= (endDate + 86400000L)
                        } else {
                            true
                        }
                        matchesCategory && matchesSource && matchesDate
                    }

                    if (filteredEvents.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No events match your current filter selections.",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredEvents) { event ->
                                EventCard(event = event) {
                                    onEventClick(event)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayChip(day: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF5450C1) else Color.White
    val contentColor = if (isSelected) Color.White else Color(0xFF3D618C)
    val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, Color(0xFFE5EEFF), RoundedCornerShape(99.dp))

    Box(
        modifier = borderModifier
            .clip(RoundedCornerShape(99.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = day,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun CategoryFilterChip(category: EventCategory, isSelected: Boolean, onClick: () -> Unit) {
    // Unique aesthetic styling matching Figma (e.g. Health is teal, Family is aqua, Arts is orange)
    val chipColors = when (category) {
        EventCategory.NIGHTLIFE -> Pair(Color(0xFFE5EEFF), Color(0xFF5450C1))
        EventCategory.HEALTH_WELLNESS -> Pair(Color(0xFF89F5E7), Color(0xFF005C54))
        EventCategory.FOOD_DRINK -> Pair(Color(0xFFFFF2E5), Color(0xFF4A2500))
        EventCategory.ARTS_CULTURE -> Pair(Color(0xFFFFE5CC), Color(0xFF4A2500))
        else -> Pair(Color.White, Color(0xFF05345C))
    }

    val backgroundColor = if (isSelected) chipColors.first else Color.White
    val contentColor = if (isSelected) chipColors.second else Color(0xFF05345C)
    val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, Color(0xFFE5EEFF), RoundedCornerShape(99.dp))

    Box(
        modifier = borderModifier
            .clip(RoundedCornerShape(99.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 17.dp, vertical = 7.dp)
    ) {
        Text(
            text = category.displayName,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun SourceChip(source: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF5450C1) else Color.White
    val contentColor = if (isSelected) Color.White else Color(0xFF05345C)
    val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, Color(0xFFE5EEFF), RoundedCornerShape(99.dp))

    Box(
        modifier = borderModifier
            .clip(RoundedCornerShape(99.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 17.dp, vertical = 9.dp)
    ) {
        Text(
            text = source,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(217.dp)
            ) {
                // Event image using Coil
                AsyncImage(
                    model = event.imageUrls.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Bookmark icon mockup
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.95f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DateRange, // Placeholder for Bookmark icon
                        contentDescription = "Save Event",
                        tint = Color(0xFF5450C1),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Tags and Info Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Tag
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF89F5E7), RoundedCornerShape(99.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = event.category.displayName,
                            color = Color(0xFF005C54),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Price Tag
                    Text(
                        text = if ((event.cost == null || event.cost == 0.0)) "Free" else "$${String.format(Locale.getDefault(), "%.2f", event.cost)}",
                        color = Color(0xFF3D618C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Source Badge
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE5EEFF), RoundedCornerShape(99.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Source: ${event.source.displayName}",
                            color = Color(0xFF3D618C),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Event Title
                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF05345C)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Time and Location Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF3D618C),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sun, Sep 18", color = Color(0xFF3D618C), fontSize = 12.dp.value.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF3D618C),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("09:00 AM", color = Color(0xFF3D618C), fontSize = 12.dp.value.sp)
                    }
                }
            }
        }
    }
}