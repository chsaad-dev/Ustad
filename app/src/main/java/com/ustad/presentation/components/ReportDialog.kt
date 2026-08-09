package com.ustad.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
    jobId: String,
    reportedId: String,
    reporterRole: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    val reasons = listOf(
        "Unprofessional behavior",
        "Overcharging / Price dispute",
        "Did not show up",
        "Damaged property",
        "Other"
    )
    var selectedReason by remember { mutableStateOf(reasons.first()) }
    var detailsInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Report Issue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Error
            )
        },
        text = {
            Column {
                Text(
                    text = "Please select the reason for reporting this user:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(Spacing.sm))

                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        RadioButton(
                            selected = (selectedReason == reason),
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = Error)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                OutlinedTextField(
                    value = detailsInput,
                    onValueChange = { detailsInput = it },
                    label = { Text("Additional Details (Optional)") },
                    shape = UstadShapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedReason, detailsInput) },
                colors = ButtonDefaults.buttonColors(containerColor = Error)
            ) {
                Text("Submit Report", color = androidx.compose.ui.graphics.Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
