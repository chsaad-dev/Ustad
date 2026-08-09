package com.ustad.presentation.components

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ustad.presentation.theme.*

@Composable
fun VoicePlayerBar(
    audioUrlOrPath: String,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var durationText by remember { mutableStateOf("0:00") }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(audioUrlOrPath) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Surface(
        shape = UstadShapes.medium,
        color = Background,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        try {
                            if (mediaPlayer == null) {
                                mediaPlayer = MediaPlayer().apply {
                                    setDataSource(audioUrlOrPath)
                                    prepare()
                                    setOnCompletionListener {
                                        isPlaying = false
                                        progress = 0f
                                    }
                                }
                            }
                            mediaPlayer?.start()
                            isPlaying = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                            isPlaying = false
                        }
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Primary, contentColor = Color.White),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPlaying) "Playing Voice Note 🎵" else "Voice Note Recorded",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress = { if (isPlaying) 0.6f else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Primary,
                    trackColor = BorderColor,
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            Text(
                text = "0:30",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
