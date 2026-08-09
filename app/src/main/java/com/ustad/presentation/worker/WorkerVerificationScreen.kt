package com.ustad.presentation.worker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.DashedUploadBox
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerVerificationScreen(
    viewModel: WorkerViewModel,
    onBack: () -> Unit
) {
    var cnicFrontUploaded by remember { mutableStateOf(false) }
    var cnicBackUploaded by remember { mutableStateOf(false) }
    var selfieUploaded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CNIC & Selfie Verification") },
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

            Text("Become a Verified Ustad 🔰", style = MaterialTheme.typography.headlineSmall, color = Primary)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text("Verified Ustads get 3x more bookings and higher customer trust in Sahiwal.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Spacer(modifier = Modifier.height(Spacing.lg))

            // CNIC Front
            Text("1. CNIC Front Side", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            DashedUploadBox(
                title = if (cnicFrontUploaded) "CNIC Front Uploaded ✓" else "Upload CNIC Front",
                subtitle = "Clear photo of your NADRA CNIC front side",
                onUploadClick = { cnicFrontUploaded = true }
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // CNIC Back
            Text("2. CNIC Back Side", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            DashedUploadBox(
                title = if (cnicBackUploaded) "CNIC Back Uploaded ✓" else "Upload CNIC Back",
                subtitle = "Clear photo of your NADRA CNIC back side",
                onUploadClick = { cnicBackUploaded = true }
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Live Selfie
            Text("3. Live Selfie Photo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            DashedUploadBox(
                title = if (selfieUploaded) "Selfie Photo Captured ✓" else "Take Live Selfie",
                subtitle = "Front face selfie photo without hat or glasses",
                onUploadClick = { selfieUploaded = true }
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            UstadPrimaryButton(
                text = if (isSubmitting) "Submitting Documents..." else "Submit for Admin Review",
                enabled = cnicFrontUploaded && cnicBackUploaded && selfieUploaded && !isSubmitting,
                onClick = {
                    isSubmitting = true
                    onBack()
                }
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}
