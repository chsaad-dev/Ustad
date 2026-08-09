package com.ustad.presentation.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.domain.model.WorkerModel
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationQueueScreen(
    viewModel: AdminViewModel,
    onSelectWorker: (WorkerModel) -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    val pendingWorkers by viewModel.pendingWorkers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Verification Queue (${pendingWorkers.size})") },
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
        ) {
            if (pendingWorkers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Queue is clear! 🎉", style = MaterialTheme.typography.titleMedium, color = Success, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("No pending worker CNIC verification applications.", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            } else {
                // Dense List Style per design.md Admin Section
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(pendingWorkers) { index, worker ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectWorkerForReview(worker)
                                    onSelectWorker(worker)
                                }
                                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = worker.displayName.ifEmpty { "Ustad Application #${worker.userId.take(5)}" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Skills: ${worker.skills.joinToString(", ")} • 8 yrs exp",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                shape = UstadShapes.small,
                                color = Warning.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Review →",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Warning,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (index < pendingWorkers.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.lg), color = BorderColor)
                        }
                    }
                }
            }
        }
    }
}
