package com.ustad.presentation.worker

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.StatusPill
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.components.UstadSecondaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveJobScreen(
    viewModel: WorkerViewModel,
    onJobCompleted: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    val context = LocalContext.current
    val activeJob by viewModel.activeJob.collectAsState()

    val currentStatus = activeJob?.status?.lowercase() ?: "accepted"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Job Management") },
                actions = {
                    TextButton(onClick = onNavigateToDevMenu) {
                        Text("Dev Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            if (activeJob == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No active job claimed yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text("Accept a request from your Dashboard or Requests feed!", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            } else {
                val job = activeJob!!

                // Job Details Card
                Card(
                    shape = UstadShapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.lg)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(job.category, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            StatusPill(status = currentStatus)
                        }

                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(job.description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

                        Spacer(modifier = Modifier.height(Spacing.md))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(job.address.ifEmpty { "Farid Town, Sahiwal" }, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                // Customer Actions Row: Maps Navigation & Phone Call
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        UstadSecondaryButton(
                            text = "Navigate 🗺️",
                            onClick = {
                                val gmmIntentUri = Uri.parse("google.navigation:q=${job.latitude},${job.longitude}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                context.startActivity(mapIntent)
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        UstadPrimaryButton(
                            text = "Call Customer 📞",
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+923001234567"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxl))

                // Sequential Action Button Step
                Text(
                    text = "Update Job Status:",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(Spacing.md))

                when (currentStatus) {
                    "accepted", "pending" -> {
                        UstadPrimaryButton(
                            text = "Mark On The Way 🚗",
                            containerColor = Primary,
                            onClick = {
                                viewModel.advanceJobStatus("ontheway")
                            }
                        )
                    }
                    "ontheway" -> {
                        UstadPrimaryButton(
                            text = "Mark Work Started 🛠️",
                            containerColor = Primary,
                            onClick = {
                                viewModel.advanceJobStatus("workstarted")
                            }
                        )
                    }
                    "workstarted" -> {
                        var hasUploadedAfterPhoto by remember { mutableStateOf(false) }

                        Surface(
                            shape = UstadShapes.medium,
                            color = PrimaryLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(Spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (hasUploadedAfterPhoto) "📸 After-Photo Uploaded!" else "📸 Take/Upload After-Photo (Optional)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Primary,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { hasUploadedAfterPhoto = !hasUploadedAfterPhoto }) {
                                    Text(if (hasUploadedAfterPhoto) "Change" else "Upload")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Spacing.md))

                        UstadPrimaryButton(
                            text = "Mark Completed ✅",
                            containerColor = Success,
                            onClick = {
                                viewModel.advanceJobStatus("completed")
                                onJobCompleted()
                            }
                        )
                    }

                    "completed" -> {
                        Surface(
                            shape = UstadShapes.medium,
                            color = Success.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.lg),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("JOB COMPLETED 🎉", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Success)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Collect payment directly from customer in cash.", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}
