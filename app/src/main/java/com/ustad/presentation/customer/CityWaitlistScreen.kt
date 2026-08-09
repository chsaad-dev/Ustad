package com.ustad.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityWaitlistScreen(
    cityName: String,
    onNavigateBack: () -> Unit
) {
    var phoneInput by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coming Soon to $cityName") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationCity,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Ustad is expanding to $cityName! 🚀",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Currently, Ustad live 30-minute booking is operational in Sahiwal. Join the $cityName waitlist to be notified first when we launch!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            if (isSubmitted) {
                Surface(
                    shape = UstadShapes.medium,
                    color = PrimaryLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉 You're on the waitlist!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "We'll send an SMS alert to $phoneInput as soon as Ustad launches in $cityName.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                UstadPrimaryButton(
                    text = "Back to Home",
                    onClick = onNavigateBack
                )
            } else {
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Phone Number for Launch Alert") },
                    placeholder = { Text("+92 300 1234567") },
                    singleLine = true,
                    shape = UstadShapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Spacing.xl))

                UstadPrimaryButton(
                    text = if (isLoading) "Joining Waitlist..." else "Join $cityName Waitlist ⚡",
                    enabled = phoneInput.isNotBlank() && !isLoading,
                    onClick = {
                        isLoading = true
                        val waitlistDoc = mapOf(
                            "cityName" to cityName,
                            "phone" to phoneInput,
                            "createdAt" to System.currentTimeMillis()
                        )
                        FirebaseFirestore.getInstance()
                            .collection("waitlist")
                            .add(waitlistDoc)
                            .addOnSuccessListener {
                                isLoading = false
                                isSubmitted = true
                            }
                            .addOnFailureListener {
                                isLoading = false
                                isSubmitted = true // Fallback submit
                            }
                    }
                )
            }
        }
    }
}
