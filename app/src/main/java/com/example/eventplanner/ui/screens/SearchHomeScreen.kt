package com.example.eventplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventplanner.model.EventCategory
import com.example.eventplanner.viewmodel.SearchHomeViewModel

import com.example.eventplanner.viewmodel.TrendingVibesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHomeScreen(
    viewModel: SearchHomeViewModel = viewModel(),
    onSearchClicked: (city: String, categories: Set<EventCategory>, start: Long?, end: Long?) -> Unit,
    onTrendingCategoryClicked: (city: String, category: EventCategory, start: Long, end: Long) -> Unit,
) {
    val searchCriteria by viewModel.searchCriteria.collectAsState()
    val cityPredictions by viewModel.cityPredictions.collectAsState()
    val trendingVibesState by viewModel.trendingVibesState.collectAsState()
    
    var showCategoryModal by remember { mutableStateOf(value = false) }
    var showDatePicker by remember { mutableStateOf(value = false) }
    var isDropdownExpanded by remember { mutableStateOf(value = false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState = rememberDateRangePickerState()

    Scaffold(
        topBar = { SearchHomeTopBar() },
        bottomBar = { BottomNavigationPlaceholder() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Hero Section
            Text(
                text = "Find Your Scene.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover curated experiences based on the energy you're looking for today.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Search Interaction Canvas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    // Location Input (Google Places Autocomplete)
                    Text("Location", style = MaterialTheme.typography.labelMedium, color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded && cityPredictions.isNotEmpty(),
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = searchCriteria.city,
                            onValueChange = { 
                                viewModel.updateCity(it)
                                isDropdownExpanded = true
                            },
                            placeholder = { Text("Enter City", color = Color.DarkGray) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        // The Autocomplete Dropdown List
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded && cityPredictions.isNotEmpty(),
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            cityPredictions.forEach { prediction ->
                                DropdownMenuItem(
                                    text = { Text(prediction) },
                                    onClick = {
                                        viewModel.selectCityPrediction(prediction)
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timing Input
                    Text("Timing", style = MaterialTheme.typography.labelMedium, color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.getFormattedDateRange().ifEmpty { "" },
                        onValueChange = { },
                        readOnly = true,
                        placeholder = { Text("Select Dates", color = Color.DarkGray) },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Dates") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }, // Opens Date Picker
                        enabled = false, // Prevents typing, relies on clickable overlay
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledPlaceholderColor = Color.DarkGray,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category Selector Trigger
                    Text("Search Categories", style = MaterialTheme.typography.labelMedium, color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showCategoryModal = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val categoryText = when (searchCriteria.selectedCategories.size) {
                            0 -> "All Categories"
                            1 -> searchCriteria.selectedCategories.first().displayName
                            else -> "${searchCriteria.selectedCategories.size} Categories Selected"
                        }
                        Text(
                            text = categoryText,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main Search Action
                    Button(
                        onClick = { 
                            viewModel.onSearchClicked()
                            // Pass the actual selected city, categories, start date, and end date to the navigation action (fallback to Atlanta if empty)
                            val selectedCity = searchCriteria.city.ifEmpty { "Atlanta" }
                            onSearchClicked(selectedCity, searchCriteria.selectedCategories, searchCriteria.startDateMillis, searchCriteria.endDateMillis)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Search Events", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Trending Vibes Section
            Text(
                text = "Trending Vibes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            when (val state = trendingVibesState) {
                is TrendingVibesState.RequiresCity -> {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "Enter a location above to see what's trending near you.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is TrendingVibesState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TrendingVibesState.Empty -> {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No trending vibes for the next 72 hours.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is TrendingVibesState.Success -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        state.trendingCategories.forEach { (category, count) ->
                            TrendingCard(
                                title = "$count Events", 
                                category = category.displayName, 
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    val startMillis = System.currentTimeMillis()
                                    val endMillis = startMillis + (72L * 60 * 60 * 1000)
                                    onTrendingCategoryClicked(searchCriteria.city, category, startMillis, endMillis)
                                }
                            )
                        }
                        
                        // If only 1 category exists, add a spacer to maintain UI grid balance
                        if (state.trendingCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Material 3 Date Range Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDateRange(
                        startMillis = datePickerState.selectedStartDateMillis,
                        endMillis = datePickerState.selectedEndDateMillis
                    )
                    showDatePicker = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DateRangePicker(
                state = datePickerState,
                title = { Text("Select Event Dates", modifier = Modifier.padding(16.dp)) },
                headline = { Text("Start Date - End Date", modifier = Modifier.padding(horizontal = 16.dp)) },
                showModeToggle = false,
                modifier = Modifier.fillMaxWidth().height(500.dp)
            )
        }
    }

    // Category Selection Modal Bottom Sheet
    if (showCategoryModal) {
        ModalBottomSheet(
            onDismissRequest = { showCategoryModal = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Discover Life.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    if (searchCriteria.selectedCategories.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCategories() }) {
                            Text("Clear")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Curated experiences for the modern explorer.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bento Grid of Categories
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 400.dp) // Restrict height to allow scrolling
                ) {
                    items(EventCategory.entries) { category ->
                        val isSelected = searchCriteria.selectedCategories.contains(category)
                        CategoryChip(
                            category = category,
                            isSelected = isSelected,
                            onClick = { viewModel.toggleCategory(category) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showCategoryModal = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Apply Filter", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CategoryChip(category: EventCategory, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = borderModifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = category.displayName,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun TrendingCard(title: String, category: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(150.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHomeTopBar() {
    TopAppBar(
        title = { Text("VibeCheck", fontWeight = FontWeight.Bold) },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(32.dp)
                    .background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun BottomNavigationPlaceholder() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = true, onClick = { }, icon = { Icon(Icons.Default.Search, "Search") }, label = { Text("Explore") })
        NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Default.DateRange, "Saved") }, label = { Text("Saved") })
        NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile") })
    }
}