package com.ustad.presentation.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.components.UstadSecondaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerProfileScreen(
    viewModel: WorkerViewModel,
    onNavigateToVerification: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    val rating by viewModel.workerRating.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Profile") },
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

            Card(
                shape = UstadShapes.medium,
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(viewModel.workerName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Verified", tint = Success, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text("Skills: ${viewModel.workerSkills.joinToString(", ")}", style = MaterialTheme.typography.titleMedium, color = Primary)
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text("City: Sahiwal, Punjab", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Rating & Trust Score Card
            Surface(
                shape = UstadShapes.medium,
                color = PrimaryLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Warning, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$rating ★", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                        Text("Overall Rating", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }

                    Divider(modifier = Modifier.height(40.dp).width(1.dp), color = BorderColor)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("98.0 %", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary)
                        Text("Trust Score", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Referral Code Card
            Card(
                shape = UstadShapes.medium,
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Text("Your Invite Code", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("USTAD-W789", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.Share, contentDescription = "Share", tint = Primary)
                        }
                    }
                    Text("3 invitees registered", style = MaterialTheme.typography.labelSmall, color = Success)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            UstadSecondaryButton(
                text = "View Monthly Leaderboard 🏆",
                onClick = onNavigateToLeaderboard
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            UstadSecondaryButton(
                text = "CNIC Verification Status (Verified 🔰)",
                onClick = onNavigateToVerification
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            UstadPrimaryButton(
                text = "Sign Out Worker Session",
                onClick = onLogout
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}
