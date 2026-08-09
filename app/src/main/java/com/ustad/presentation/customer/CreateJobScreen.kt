package com.ustad.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ustad.domain.model.CategoryTemplates
import com.ustad.presentation.components.*
import com.ustad.presentation.theme.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    viewModel: CustomerViewModel,
    onJobCreated: (String) -> Unit,
    onBack: () -> Unit
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val jobDescription by viewModel.jobDescription.collectAsState()
    val selectedChips by viewModel.selectedTemplateChips.collectAsState()
    val isUrgent by viewModel.isUrgent.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val voiceFile by viewModel.voiceNoteFile.collectAsState()
    val uploadedPhotos by viewModel.uploadedPhotos.collectAsState()

    val category = selectedCategory ?: CategoryTemplates.categories[0]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Job Request") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.titleMedium)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(Spacing.lg)) {
                    UstadPrimaryButton(
                        text = "Find Ustad Now",
                        enabled = jobDescription.isNotBlank() || selectedChips.isNotEmpty() || voiceFile != null,
                        onClick = {
                            viewModel.createJob { jobId ->
                                onJobCreated(jobId)
                            }
                        }
                    )
                }
            }
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

            // Step Indicator
            StepIndicator(
                currentStep = 2,
                totalSteps = 3,
                stepTitle = "Describe Your Problem"
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Selected Category Badge
            Card(
                shape = UstadShapes.medium,
                colors = CardDefaults.cardColors(containerColor = PrimaryLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column {
                        Text("Selected Category", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text(category.name, style = MaterialTheme.typography.titleMedium, color = Primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Quick Select Template Chips
            Text("Common Issues (Quick Select):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.sm))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                category.templateChips.forEach { chipText ->
                    val isSelected = selectedChips.contains(chipText)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleTemplateChip(chipText) },
                        label = { Text(chipText) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Description Input Field
            Text("Problem Description:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            OutlinedTextField(
                value = jobDescription,
                onValueChange = { viewModel.jobDescription.value = it },
                placeholder = { Text("Explain what needs to be fixed...") },
                minLines = 3,
                maxLines = 5,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Voice Recorder Bar
            Text("Voice Note (Urdu / Local dialect friendly):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            VoiceRecorderBar(
                isRecording = isRecordingVoice,
                onStartRecording = { viewModel.startVoiceRecording() },
                onStopRecording = { viewModel.stopVoiceRecording() },
                hasRecordedAudio = voiceFile != null
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Photo Upload
            Text("Photos (Up to 3):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            DashedUploadBox(
                title = "Add Photo",
                subtitle = "${uploadedPhotos.size}/3 photos uploaded (compressed <800KB)",
                onUploadClick = {
                    // Demo upload trigger
                    val demoFile = java.io.File(viewModel.customerAddress)
                    viewModel.addPhoto(demoFile)
                }
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Location Section
            Text("Job Location:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            OutlinedTextField(
                value = viewModel.customerAddress,
                onValueChange = { viewModel.customerAddress = it },
                leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Primary) },
                singleLine = true,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Urgent Toggle Row
            Card(
                shape = UstadShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (isUrgent) Warning.copy(alpha = 0.15f) else Surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Urgent Request (⚡ 15 Min Arrival)", style = MaterialTheme.typography.titleMedium)
                        Text("Prioritizes your request for active nearby Ustads", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Switch(
                        checked = isUrgent,
                        onCheckedChange = { viewModel.isUrgent.value = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl * 2))
        }
    }
}
