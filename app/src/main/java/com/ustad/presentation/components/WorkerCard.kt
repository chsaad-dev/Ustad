package com.ustad.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ustad.domain.model.WorkerModel
import com.ustad.presentation.theme.*

@Composable
fun WorkerCard(
    worker: WorkerModel,
    distanceKmStr: String,
    onCallClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = UstadShapes.medium,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 48dp Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Avatar",
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = worker.displayName.ifEmpty { "Ustad Worker" },
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (worker.isVerified) {
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Verified",
                                tint = Success,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xs))

                    // Rating & Distance Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Rating",
                            tint = Warning,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${worker.rating} (${worker.completedJobs} jobs)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text = "•  $distanceKmStr",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Skills chips
            if (worker.skills.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    worker.skills.take(3).forEach { skill ->
                        Surface(
                            shape = CircleShape,
                            color = PrimaryLight
                        ) {
                            Text(
                                text = skill,
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // Side-by-side action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    UstadSecondaryButton(
                        text = "Call",
                        onClick = onCallClick
                    )
                }
                Box(modifier = Modifier.weight(1.5f)) {
                    UstadPrimaryButton(
                        text = "Book Now",
                        onClick = onBookClick
                    )
                }
            }
        }
    }
}
