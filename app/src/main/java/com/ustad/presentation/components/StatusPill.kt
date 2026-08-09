package com.ustad.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ustad.presentation.theme.Error
import com.ustad.presentation.theme.Success
import com.ustad.presentation.theme.Warning

@Composable
fun StatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, label) = when (status.lowercase()) {
        "pending" -> Triple(Warning.copy(alpha = 0.15f), Warning, "PENDING")
        "accepted" -> Triple(Success.copy(alpha = 0.15f), Success, "ACCEPTED")
        "ontheway" -> Triple(Success.copy(alpha = 0.15f), Success, "ON THE WAY")
        "workstarted" -> Triple(Success.copy(alpha = 0.15f), Success, "IN PROGRESS")
        "completed" -> Triple(Success.copy(alpha = 0.15f), Success, "COMPLETED")
        "cancelled", "rejected" -> Triple(Error.copy(alpha = 0.15f), Error, status.uppercase())
        else -> Triple(Color.LightGray.copy(alpha = 0.3f), Color.DarkGray, status.uppercase())
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .background(backgroundColor, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
