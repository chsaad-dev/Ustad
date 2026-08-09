package com.ustad.presentation.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    viewModel: CustomerViewModel,
    onSubmitted: () -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rate Ustad") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "How was your experience?",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Your rating helps keep Ustads reliable in Sahiwal",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // 5-Star Selector Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                        contentDescription = "Star $i",
                        tint = if (i <= rating) Warning else TextSecondary,
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { rating = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            OutlinedTextField(
                value = reviewComment,
                onValueChange = { reviewComment = it },
                label = { Text("Write a comment (optional)") },
                placeholder = { Text("e.g. Arrived fast, fixed fan quickly!") },
                minLines = 3,
                maxLines = 5,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            UstadPrimaryButton(
                text = if (isSubmitting) "Submitting..." else "Submit Rating",
                onClick = {
                    isSubmitting = true
                    onSubmitted()
                }
            )
        }
    }
}
