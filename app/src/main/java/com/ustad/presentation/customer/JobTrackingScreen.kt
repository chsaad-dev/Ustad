package com.ustad.presentation.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.StatusPill
import com.ustad.presentation.components.StatusTimeline
import com.ustad.presentation.components.TimelineStep
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.components.UstadSecondaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobTrackingScreen(
    viewModel: CustomerViewModel,
    onNavigateToRating: () -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val activeJob by viewModel.activeJob.collectAsState()
    val status = activeJob?.status?.lowercase() ?: "pending"

    val timelineSteps = listOf(
        TimelineStep(
            title = "Job Requested",
            description = "Broadcasting to nearby verified Ustads",
            isCompleted = true,
            isCurrent = status == "pending"
        ),
        TimelineStep(
            title = "Ustad Accepted",
            description = "Worker has accepted your request",
            isCompleted = status in listOf("accepted", "ontheway", "workstarted", "completed"),
            isCurrent = status == "accepted"
        ),
        TimelineStep(
            title = "On The Way",
            description = "Worker is traveling to your location",
            isCompleted = status in listOf("ontheway", "workstarted", "completed"),
            isCurrent = status == "ontheway"
        ),
        TimelineStep(
            title = "Work Started",
            description = "Worker is performing the requested service",
            isCompleted = status in listOf("workstarted", "completed"),
            isCurrent = status == "workstarted"
        ),
        TimelineStep(
            title = "Job Completed",
            description = "Work completed! Pay worker directly in cash",
            isCompleted = status == "completed",
            isCurrent = status == "completed"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Tracking & Live Status") },
                actions = {
                    TextButton(onClick = onBackToHome) {
                        Text("Home")
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

            // Job Status Header Card
            Card(
                shape = UstadShapes.medium,
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeJob?.category ?: "Service Request",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        StatusPill(status = status)
                    }

                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = activeJob?.description ?: "Finding Ustad...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Location: ${activeJob?.address ?: "Farid Town, Sahiwal"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Live Vertical Status Timeline
            Text(
                text = "Live Progress",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            StatusTimeline(steps = timelineSteps)

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Assigned Worker Details Card (if accepted/active)
            Surface(
                shape = UstadShapes.medium,
                color = Surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(Spacing.lg)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Primary
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assigned Ustad", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("Ustad Tariq Electrician", style = MaterialTheme.typography.titleMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Warning, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("4.9 (84 jobs)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+923001234567"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PrimaryLight)
                    ) {
                        Icon(Icons.Rounded.Call, contentDescription = "Call", tint = Primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            var showReportDialog by remember { mutableStateOf(false) }

            if (showReportDialog) {
                com.ustad.presentation.components.ReportDialog(
                    jobId = activeJob?.id ?: "job_123",
                    reportedId = activeJob?.workerId ?: "worker_123",
                    reporterRole = "customer",
                    onDismiss = { showReportDialog = false },
                    onSubmit = { reason, details ->
                        showReportDialog = false
                    }
                )
            }

            if (status == "completed") {
                UstadPrimaryButton(
                    text = "Rate Your Experience ★",
                    onClick = onNavigateToRating
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showReportDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text("Report ⚠️")
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        UstadSecondaryButton(
                            text = "Cancel Request",
                            borderColor = Error,
                            contentColor = Error,
                            onClick = { viewModel.cancelActiveJob("Customer cancelled request") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

