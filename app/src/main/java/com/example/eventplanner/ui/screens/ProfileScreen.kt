package com.example.eventplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eventplanner.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onSignOutSuccess: () -> Unit,
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = { BottomNavigationPlaceholder() },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FF))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // User Avatar Photo or Default Icon
            val photoUrl = currentUser?.photoUrl?.toString()
            if (!photoUrl.isNullOrBlankSafe()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF5450C1), CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5EEFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Icon",
                        tint = Color(0xFF5450C1),
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = currentUser?.displayName ?: "Explorer",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF05345C),
            )

            // User Email
            Text(
                text = currentUser?.email ?: "No email provided",
                fontSize = 14.sp,
                color = Color(0xFF3D618C),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Saved Events Section Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFF5450C1),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Saved Events",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF05345C),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You haven't saved any events yet. Bookmark events to track your upcoming vibes here!",
                        fontSize = 13.sp,
                        color = Color(0xFF3D618C),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Out Button
            OutlinedButton(
                onClick = {
                    authViewModel.signOut()
                    onSignOutSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun String?.isNullOrBlankSafe(): Boolean {
    return (this == null) || this.trim().isEmpty()
}
