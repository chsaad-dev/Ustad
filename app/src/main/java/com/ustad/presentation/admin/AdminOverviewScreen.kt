package com.ustad.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOverviewScreen(
    viewModel: AdminViewModel,
    onNavigateToQueue: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    val stats by viewModel.adminStats.collectAsState()
    val pendingWorkers by viewModel.pendingWorkers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustad Admin Console", color = Color.White) },
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
            // Dark Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryDark)
                    .padding(Spacing.lg)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Shield, contentDescription = null, tint = Success, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Authorization Verified 🛡️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Authenticated via Firebase Auth Custom Claim (admin: true)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text("System Metrics & Overview", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(Spacing.md))

                // Dense Stat Grid per design.md Admin Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
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
                            Icon(Icons.Rounded.Group, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${stats.totalUsers}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Total Users", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

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
                            Icon(Icons.Rounded.Work, contentDescription = null, tint = Warning, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${stats.activeJobs}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Total Jobs", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
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
                            Icon(Icons.Rounded.Assignment, contentDescription = null, tint = Success, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${pendingWorkers.size}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Error)
                            Text("Pending CNIC Reviews", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

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
                            Icon(Icons.Rounded.Work, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${stats.jobsToday}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Jobs Today", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xl))

                // Direct action card to Verification Queue
                Card(
                    shape = UstadShapes.medium,
                    colors = CardDefaults.cardColors(containerColor = PrimaryLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.lg),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CNIC Verification Queue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${pendingWorkers.size} Ustads awaiting document approval", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                        Button(
                            onClick = onNavigateToQueue,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Review Queue")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}
