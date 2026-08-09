package com.ustad.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ustad.presentation.theme.Primary
import com.ustad.presentation.theme.PrimaryLight
import com.ustad.presentation.theme.Spacing
import com.ustad.presentation.theme.UstadShapes
import kotlinx.coroutines.delay

@Composable
fun VoiceRecorderBar(
    isRecording: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    hasRecordedAudio: Boolean,
    modifier: Modifier = Modifier
) {
    var secondsRecorded by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            secondsRecorded = 0
            while (secondsRecorded < 60 && isRecording) {
                delay(1000)
                secondsRecorded++
            }
            if (secondsRecorded >= 60) {
                onStopRecording()
            }
        }
    }

    Surface(
        shape = UstadShapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isRecording) onStopRecording() else onStartRecording()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) MaterialTheme.colorScheme.error else Primary)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Rounded.Stop else Icons.Rounded.Mic,
                    contentDescription = "Voice Record",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        isRecording -> "Recording voice note..."
                        hasRecordedAudio -> "Voice note recorded (Tap to re-record)"
                        else -> "Tap mic to record voice note (60s max)"
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                if (isRecording) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Animated Waveform Bars
                        repeat(8) { index ->
                            val infiniteTransition = rememberInfiniteTransition(label = "wave")
                            val height by infiniteTransition.animateFloat(
                                initialValue = 8f,
                                targetValue = 24f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 300 + index * 60, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "barHeight"
                            )
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(height.dp)
                                    .background(Primary, CircleShape)
                            )
                        }
                    }
                }
            }

            if (isRecording) {
                Text(
                    text = "0:${secondsRecorded.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
