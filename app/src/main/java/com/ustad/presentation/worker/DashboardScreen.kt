package com.ustad.presentation.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.IncomingJobCard
import com.ustad.presentation.components.OnlineToggleSwitch
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WorkerViewModel,
    onNavigateToActiveJob: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    val isOnline by viewModel.isOnline.collectAsState()
    val pendingJobs by viewModel.pendingJobs.collectAsState()
    val completedCount by viewModel.completedJobsToday.collectAsState()
    val rating by viewModel.workerRating.collectAsState()
    val alertMessage by viewModel.alertMessage.collectAsState()

    val newestJob = pendingJobs.firstOrNull()

    // Alert Dialog for Double-Accept Race Condition
    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAlert() },
            title = { Text("Job Already Claimed ⚡", fontWeight = FontWeight.Bold) },
            text = { Text(alertMessage ?: "This job was already accepted by another Ustad.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearAlert() },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("OK, Got It")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustad Worker Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryDark),
                actions = {
                    TextButton(onClick = onNavigateToDevMenu) {
                        Text("Dev Menu", color = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Dark Header Section (SecondaryDark #111827) per Section 8 of design.md
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryDark)
                    .padding(Spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Salam, ${viewModel.workerName}! 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Skill: ${viewModel.workerSkills.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    OnlineToggleSwitch(
                        isOnline = isOnline,
                        onToggle = { online -> viewModel.toggleOnlineStatus(online) }
                    )
                }
            }

            Column(modifier = Modifier.padding(Spacing.lg)) {
                // Today's Stats Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Stat Card 1: Completed Jobs
                    Card(
                        shape = UstadShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.Work, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$completedCount", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Jobs Completed", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

                    // Stat Card 2: Rating
                    Card(
                        shape = UstadShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Warning, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$rating ★", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Worker Rating", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                // Live Incoming Job Section
                Text(
                    text = "Live Incoming Request",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(Spacing.md))

                if (!isOnline) {
                    Surface(
                        shape = UstadShapes.medium,
                        color = Background,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.xl),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("You are currently OFFLINE 🔴", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Toggle ONLINE above to start receiving live job requests in Sahiwal.", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                } else if (newestJob == null) {
                    Surface(
                        shape = UstadShapes.medium,
                        color = PrimaryLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.xl),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Listening for nearby requests... 📡", style = MaterialTheme.typography.titleMedium, color = Primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("You are visible to customers within 10km.", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                } else {
                    IncomingJobCard(
                        job = newestJob.job,
                        distanceKmStr = newestJob.distanceFormatted,
                        onAcceptClick = {
                            viewModel.acceptJobTransactional(newestJob.job.id) {
                                onNavigateToActiveJob()
                            }
                        },
                        onRejectClick = {
                            viewModel.rejectJobLocally(newestJob.job.id)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}
