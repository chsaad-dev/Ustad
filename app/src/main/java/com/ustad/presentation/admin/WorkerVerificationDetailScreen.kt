package com.ustad.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.components.UstadSecondaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerVerificationDetailScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val selectedWorker by viewModel.selectedWorker.collectAsState()
    val alertMessage by viewModel.alertMessage.collectAsState()
    val actionSuccessMessage by viewModel.actionSuccessMessage.collectAsState()

    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectionReasonText by remember { mutableStateOf("") }

    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessages() },
            title = { Text("Admin Security Warning ⚠️", fontWeight = FontWeight.Bold) },
            text = { Text(alertMessage ?: "") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearMessages() },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

    if (actionSuccessMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearMessages()
                onBack()
            },
            title = { Text("Success ✅", fontWeight = FontWeight.Bold) },
            text = { Text(actionSuccessMessage ?: "") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearMessages()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Success)
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Worker Application") },
            text = {
                Column {
                    Text("Provide reason for rejection (e.g. Blurry CNIC photo):", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    OutlinedTextField(
                        value = rejectionReasonText,
                        onValueChange = { rejectionReasonText = it },
                        placeholder = { Text("Rejection reason...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedWorker?.let { worker ->
                            viewModel.rejectWorker(worker.userId, rejectionReasonText) {
                                showRejectDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Confirm Rejection")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Document Review") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleMedium)
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

            if (selectedWorker == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No worker application selected", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            } else {
                val worker = selectedWorker!!

                Text(worker.displayName.ifEmpty { "Ustad Application #${worker.userId.take(5)}" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text("Requested Skills: ${worker.skills.joinToString(", ")}", style = MaterialTheme.typography.titleMedium, color = Primary)

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Document Viewers (CNIC Front, Back, Live Selfie)
                Text("1. CNIC Front Image:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Spacing.xs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Background, shape = UstadShapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷 [Full Resolution CNIC Front Image Viewer]", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Text("2. CNIC Back Image:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Spacing.xs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Background, shape = UstadShapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📷 [Full Resolution CNIC Back Image Viewer]", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Text("3. Live Selfie Photo:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Spacing.xs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Background, shape = UstadShapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤳 [Live Selfie Front Face Photo Viewer]", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(Spacing.xxl))

                // Approval / Rejection Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        UstadSecondaryButton(
                            text = "Reject App",
                            borderColor = Error,
                            contentColor = Error,
                            onClick = { showRejectDialog = true }
                        )
                    }

                    Box(modifier = Modifier.weight(1.5f)) {
                        UstadPrimaryButton(
                            text = "Approve Worker 🔰",
                            containerColor = Success,
                            onClick = {
                                viewModel.approveWorker(worker.userId) {}
                            }
                        )
                    }
                }


                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}
