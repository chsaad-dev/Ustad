package com.ustad.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ustad.presentation.theme.BorderColor
import com.ustad.presentation.theme.Primary
import com.ustad.presentation.theme.Spacing
import com.ustad.presentation.theme.TextSecondary

data class TimelineStep(
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

@Composable
fun StatusTimeline(
    steps: List<TimelineStep>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top
            ) {
                // Connected Dot Column
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val circleRadius = 8.dp.toPx()
                        val circleCenter = Offset(size.width / 2, circleRadius + 4.dp.toPx())

                        // Draw connecting vertical line if not last
                        if (index < steps.size - 1) {
                            drawLine(
                                color = if (step.isCompleted) Primary else BorderColor,
                                start = circleCenter,
                                end = Offset(size.width / 2, size.height),
                                strokeWidth = 3.dp.toPx()
                            )
                        }

                        // Draw Dot Circle
                        if (step.isCompleted || step.isCurrent) {
                            drawCircle(
                                color = Primary,
                                radius = circleRadius,
                                center = circleCenter
                            )
                        } else {
                            drawCircle(
                                color = BorderColor,
                                radius = circleRadius,
                                center = circleCenter,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(Spacing.sm))

                // Step Labels Column
                Column(
                    modifier = Modifier
                        .padding(bottom = Spacing.lg)
                        .weight(1f)
                ) {
                    Text(
                        text = step.title,
                        style = if (step.isCurrent) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                        color = if (step.isCompleted || step.isCurrent) MaterialTheme.colorScheme.onBackground else TextSecondary
                    )
                    if (step.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
