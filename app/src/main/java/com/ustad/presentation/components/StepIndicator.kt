package com.ustad.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ustad.presentation.theme.Primary
import com.ustad.presentation.theme.Spacing
import com.ustad.presentation.theme.TextSecondary

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    stepTitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.labelSmall,
            color = Primary
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = "•  $stepTitle",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
