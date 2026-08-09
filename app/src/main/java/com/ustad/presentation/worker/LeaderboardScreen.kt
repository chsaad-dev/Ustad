package com.ustad.presentation.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.domain.model.WorkerModel
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onNavigateBack: () -> Unit
) {
    // Seeded Monthly Top Ustads in Sahiwal
    val topWorkers = listOf(
        WorkerModel(userId = "w1", displayName = "Ustad Tariq", rating = 4.95, completedJobs = 142, trustScore = 99.0, skills = listOf("Electrician")),
        WorkerModel(userId = "w2", displayName = "Ustad Imran", rating = 4.90, completedJobs = 118, trustScore = 98.0, skills = listOf("Plumber")),
        WorkerModel(userId = "w3", displayName = "Ustad Bilal", rating = 4.88, completedJobs = 95, trustScore = 96.0, skills = listOf("AC Technician")),
        WorkerModel(userId = "w4", displayName = "Ustad Rashid", rating = 4.85, completedJobs = 76, trustScore = 95.0, skills = listOf("Carpenter")),
        WorkerModel(userId = "w5", displayName = "Ustad Kamran", rating = 4.82, completedJobs = 64, trustScore = 94.0, skills = listOf("Painter"))
    )

    var showOptInToggle by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Ustad Leaderboard 🏆") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(
                color = SecondaryDark,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Top-Rated Ustads • Sahiwal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Ranked monthly by customer ratings and completed jobs",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Show my profile on Leaderboard",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Switch(
                            checked = showOptInToggle,
                            onCheckedChange = { showOptInToggle = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Primary)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                itemsIndexed(topWorkers) { index, worker ->
                    LeaderboardCard(rank = index + 1, worker = worker)
                }
            }
        }
    }
}

@Composable
fun LeaderboardCard(
    rank: Int,
    worker: WorkerModel
) {
    val rankBadgeColor = when (rank) {
        1 -> Warning
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> PrimaryLight
    }

    Card(
        shape = UstadShapes.medium,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(rankBadgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color.White else Primary
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Person, contentDescription = null, tint = Primary)
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(worker.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = Warning, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${worker.rating} • ${worker.completedJobs} jobs completed", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }

            Surface(
                shape = CircleShape,
                color = Success.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "🔰 ${worker.trustScore.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Success,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
