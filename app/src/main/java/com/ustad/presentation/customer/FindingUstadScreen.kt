package com.ustad.presentation.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.StepIndicator
import com.ustad.presentation.components.WorkerCard
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindingUstadScreen(
    viewModel: CustomerViewModel,
    onWorkerBooked: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val nearbyWorkers by viewModel.nearbyWorkers.collectAsState()
    val isSearching by viewModel.isSearchingWorkers.collectAsState()
    val activeJobId by viewModel.activeJobId.collectAsState()

    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categoryName = selectedCategory?.name ?: "Electrician"

    LaunchedEffect(Unit) {
        viewModel.findNearbyWorkers(categoryName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finding Nearby Ustads") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleMedium)
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

            // Step Indicator
            StepIndicator(
                currentStep = 3,
                totalSteps = 3,
                stepTitle = "Select Your Ustad"
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Radar Pulse Header
            Surface(
                shape = UstadShapes.medium,
                color = PrimaryLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "radar")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Radar",
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.md))

                    Column {
                        Text(
                            text = "Searching Ustads nearby...",
                            style = MaterialTheme.typography.titleMedium,
                            color = Primary
                        )
                        Text(
                            text = "Sorted nearest-first via Haversine distance",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (nearbyWorkers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No online $categoryName Ustads found within 10km.\nExpanding search radius...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                Text(
                    text = "Available $categoryName Ustads (${nearbyWorkers.size}):",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    contentPadding = PaddingValues(bottom = Spacing.xxl)
                ) {
                    items(nearbyWorkers) { workerWithDist ->
                        WorkerCard(
                            worker = workerWithDist.worker,
                            distanceKmStr = workerWithDist.distanceFormatted,
                            onCallClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+923001234567"))
                                context.startActivity(intent)
                            },
                            onBookClick = {
                                val jobId = activeJobId ?: "temp_job_123"
                                viewModel.bookWorker(jobId, workerWithDist.worker.userId) {
                                    onWorkerBooked()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
