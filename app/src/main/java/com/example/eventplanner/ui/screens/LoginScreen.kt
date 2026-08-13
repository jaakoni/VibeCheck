@file:Suppress("DEPRECATION")

package com.example.eventplanner.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventplanner.viewmodel.AuthUiState
import com.example.eventplanner.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    webClientId: String,
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by authViewModel.uiState.collectAsState()

    // Fallback Intent Launcher for Google Sign-In if Credential Manager has no cached credentials
    val legacyGoogleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    authViewModel.signInWithGoogle(idToken)
                } else {
                    Toast.makeText(context, "Failed to get Google ID Token", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                onLoginSuccess()
                authViewModel.resetUiState()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetUiState()
            }
            else -> {}
        }
    }

    fun launchLegacyGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        legacyGoogleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FF))
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Hero Branding Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF5450C1), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "V",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Editorial Title
                Text(
                    text = "Unlock Your Scene.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF05345C),
                    fontSize = 30.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Sign in to save events, track your favorite vibes, and sync across all your devices.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF3D618C),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Modern Credential Manager Sign-In Button with Legacy Intent Fallback
                val isLoading = uiState is AuthUiState.Loading

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val credentialManager = CredentialManager.create(context)
                            
                            val rawNonce = UUID.randomUUID().toString()
                            val bytes = rawNonce.toByteArray()
                            val md = MessageDigest.getInstance("SHA-256")
                            val digest = md.digest(bytes)
                            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
                                .setServerClientId(webClientId)
                                .setNonce(hashedNonce)
                                .setAutoSelectEnabled(autoSelectEnabled = false)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            try {
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request,
                                )
                                val credential = result.credential
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val idToken = googleIdTokenCredential.idToken
                                authViewModel.signInWithGoogle(idToken)
                            } catch (_: GetCredentialCancellationException) {
                                // User cancelled the sign-in sheet
                            } catch (_: GetCredentialException) {
                                // Fallback to standard Google Sign-In launcher
                                launchLegacyGoogleSignIn()
                            } catch (_: Exception) {
                                launchLegacyGoogleSignIn()
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, Color(0xFFE5EEFF), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF05345C),
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF5450C1),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "G ",
                                color = Color(0xFF4285F4),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Skip / Explore Option
                TextButton(onClick = onBackClick) {
                    Text(
                        text = "Skip for now",
                        color = Color(0xFF3D618C),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
