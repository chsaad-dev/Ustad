package com.ustad.presentation.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.IncomingJobCard
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    viewModel: WorkerViewModel,
    onNavigateToActiveJob: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    val pendingJobs by viewModel.pendingJobs.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val alertMessage by viewModel.alertMessage.collectAsState()

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
                title = { Text("Available Job Feed (${pendingJobs.size})") },
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
        ) {
            Spacer(modifier = Modifier.height(Spacing.sm))

            if (!isOnline) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("You are currently OFFLINE. Go to Dashboard to turn ONLINE.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            } else if (pendingJobs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No pending jobs available", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("New job requests in Sahiwal will appear here live.", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(bottom = Spacing.xxl)
                ) {
                    items(pendingJobs) { jobWithDist ->
                        IncomingJobCard(
                            job = jobWithDist.job,
                            distanceKmStr = jobWithDist.distanceFormatted,
                            onAcceptClick = {
                                viewModel.acceptJobTransactional(jobWithDist.job.id) {
                                    onNavigateToActiveJob()
                                }
                            },
                            onRejectClick = {
                                viewModel.rejectJobLocally(jobWithDist.job.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
