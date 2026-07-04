package com.example.eventplanner.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventplanner.model.Event
import com.example.eventplanner.model.WeatherPeriod
import com.example.eventplanner.viewmodel.EventDetailUiState
import com.example.eventplanner.viewmodel.EventDetailViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onBackClick: () -> Unit,
    viewModel: EventDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details", fontWeight = FontWeight.Bold, color = Color(0xFF05345C)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF05345C))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Share event url intent
                        val state = uiState
                        if (state is EventDetailUiState.Success) {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out ${state.event.title} at VibeCheck! ${state.event.sourceUrl}")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Event"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF5450C1))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { BottomNavigationPlaceholder() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FF)) // Figma Surface Color
        ) {
            when (val state = uiState) {
                is EventDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        color = Color(0xFF5450C1),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is EventDetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onBackClick) {
                            Text("Go Back")
                        }
                    }
                }
                is EventDetailUiState.Success -> {
                    val event = state.event
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // 1. Hero Image Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(268.dp)
                        ) {
                            AsyncImage(
                                model = event.imageUrls.firstOrNull(),
                                contentDescription = "Event Main Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // 2. Overlapping Header Info Card
                        HeaderInfoCard(event = event) {
                            // Buy ticket action
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event.sourceUrl))
                            context.startActivity(browserIntent)
                        }

                        // Content Padding container for sections below card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))

                            // 3. About Section
                            AboutSection(event = event)

                            Spacer(modifier = Modifier.height(32.dp))

                            // 4. Weather Outlook Widget
                            if (!state.weather.isNullOrEmpty()) {
                                WeatherWidget(city = event.location.city, forecasts = state.weather)
                                Spacer(modifier = Modifier.height(32.dp))
                            }

                            // 5. Google Maps Location Section
                            LocationMapSection(event = event)

                            Spacer(modifier = Modifier.height(32.dp))

                            // 6. Hosted By Section
                            HostedBySection(organizerName = event.organizerName ?: "VibeCheck Creator")

                            Spacer(modifier = Modifier.height(32.dp))

                            // 7. You Might Like Section (Figma recommendations placeholder)
                            YouMightLikeSection()

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderInfoCard(event: Event, onRegisterClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .offset(y = (-30).dp), // Negative offset for elegant Figma overlapping effect
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Badges row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Tag
                Box(
                    modifier = Modifier
                        .background(Color(0xFF89F5E7), RoundedCornerShape(99.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = event.category.displayName.uppercase(Locale.US),
                        color = Color(0xFF005C54),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Source Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE5EEFF), RoundedCornerShape(99.dp))
                        .padding(horizontal = 11.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Source: ${event.source.displayName}",
                        color = Color(0xFF3D618C),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05345C)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Time Info Box
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF2F6FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF5450C1),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val dateString = if (event.startTimestamp != 0L) {
                        val formatter = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
                        formatter.format(Date(event.startTimestamp))
                    } else "Sunday, September 18, 2024"

                    val timeString = if (event.startTimestamp != 0L) {
                        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        formatter.format(Date(event.startTimestamp))
                    } else "09:00 AM"

                    Text(text = dateString, fontWeight = FontWeight.Bold, color = Color(0xFF05345C), fontSize = 14.sp)
                    Text(text = timeString, color = Color(0xFF3D618C), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Address Info Box
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF2F6FF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF5450C1),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = event.location.venueName, fontWeight = FontWeight.Bold, color = Color(0xFF05345C), fontSize = 14.sp)
                    Text(text = event.location.address, color = Color(0xFF3D618C), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Action Button
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5450C1))
            ) {
                Text(text = "Register / Buy Tickets", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun AboutSection(event: Event) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "About this event",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05345C)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE5EEFF))
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = event.description ?: "Join us for an incredible day-long experience filled with visionary keynotes, hands-on masterclasses, and vibrant community-led discussions.",
            color = Color(0xFF3D618C),
            lineHeight = 22.sp,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bullet point lists
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val bulletItems = listOf(
                "Exclusive panel Q&As with top tier professionals.",
                "Catered gourmet food & open networking bar.",
                "Premium physical gift bag & souvenir credentials included."
            )
            bulletItems.forEach { item ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("•", color = Color(0xFF5450C1), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item, color = Color(0xFF3D618C), fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
fun WeatherWidget(city: String, forecasts: List<WeatherPeriod>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Forecast Outlook",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF05345C)
                )
                Text(
                    text = "Live weather in $city",
                    fontSize = 12.sp,
                    color = Color(0xFF3D618C)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE5EEFF))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(forecasts.take(5)) { period ->
                WeatherCard(period = period)
            }
        }
    }
}

@Composable
fun WeatherCard(period: WeatherPeriod) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = period.name.split(" ").firstOrNull() ?: period.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF3D618C),
                textAlign = TextAlign.Center
            )

            AsyncImage(
                model = period.iconUrl,
                contentDescription = period.shortForecast,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )

            Text(
                text = "${period.temperature}°${period.temperatureUnit}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF05345C)
            )

            Text(
                text = period.shortForecast,
                fontSize = 10.sp,
                color = Color(0xFF3D618C),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LocationMapSection(event: Event) {
    val context = LocalContext.current
    
    // Default coordinates pointing to San Francisco center if 0.0
    val lat = if (event.location.latitude != 0.0) event.location.latitude else 37.7749
    val lon = if (event.location.longitude != 0.0) event.location.longitude else -122.4194

    val coordinates = LatLng(lat, lon)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(coordinates, 15f)
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Location",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05345C)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE5EEFF))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Interactive Map block
                val isInspection = LocalInspectionMode.current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE5EEFF), RoundedCornerShape(16.dp))
                ) {
                    if (isInspection) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0xFFE5EEFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Map Preview Placeholder", color = Color(0xFF3D618C))
                        }
                    } else {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(
                                state = rememberMarkerState(position = coordinates),
                                title = event.location.venueName,
                                snippet = event.location.address
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location Details Text block
                Text(
                    text = event.location.venueName,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF05345C),
                    fontSize = 14.sp
                )
                Text(
                    text = event.location.address,
                    color = Color(0xFF3D618C),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Directions action Button
                OutlinedButton(
                    onClick = {
                        val uriStr = "geo:$lat,$lon?q=${Uri.encode(event.location.venueName + ", " + event.location.address)}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF5450C1))
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Get Directions", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun HostedBySection(organizerName: String) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Hosted by",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05345C)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE5EEFF))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Organizer circular letter avatar mockup
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE5EEFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = organizerName.take(1).uppercase(Locale.US),
                    color = Color(0xFF5450C1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = organizerName, fontWeight = FontWeight.Bold, color = Color(0xFF05345C), fontSize = 14.sp)
                Text(text = "Official Event Host", color = Color(0xFF3D618C), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun YouMightLikeSection() {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "You might like",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05345C)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE5EEFF))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recommendations List mockups matching Figma
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RecommendationRow(date = "Oct 28", title = "Next Gen Founders Forum", place = "Downtown Tech Hub")
            RecommendationRow(date = "Nov 02", title = "Acoustic Nights Under the Stars", place = "Amphitheater Plaza")
        }
    }
}

@Composable
fun RecommendationRow(date: String, title: String, place: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recommendation thumbnail mockup
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F6FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date,
                    color = Color(0xFF5450C1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF05345C), fontSize = 14.sp, maxLines = 1)
                Text(text = place, color = Color(0xFF3D618C), fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationMapSectionPreview() {
    val mockEvent = Event(
        id = "1",
        title = "Sample Event",
        description = "This is a sample event for preview.",
        source = com.example.eventplanner.model.EventSource.TICKETMASTER,
        sourceUrl = "",
        category = com.example.eventplanner.model.EventCategory.LIVE_MUSIC,
        startTimestamp = System.currentTimeMillis(),
        endTimestamp = null,
        cost = null,
        imageUrls = listOf(),
        location = com.example.eventplanner.model.Location("Sample Venue", "123 Main St", "San Francisco", 37.7749, -122.4194),
        tags = listOf("Sample"),
        organizerName = "Mock Organizer"
    )
    LocationMapSection(event = mockEvent)
}