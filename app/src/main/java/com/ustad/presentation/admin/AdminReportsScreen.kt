package com.ustad.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.domain.model.ReportModel
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    adminViewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val reports by adminViewModel.reportsFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Abuse & Dispute Reports") }
            )
        }
    ) { padding ->
        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No pending reports to review 🎉",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(reports) { report ->
                    ReportItemCard(
                        report = report,
                        onDismiss = { adminViewModel.resolveReport(report.id, "dismissed") },
                        onResolve = { adminViewModel.resolveReport(report.id, "resolved") }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportItemCard(
    report: ReportModel,
    onDismiss: () -> Unit,
    onResolve: () -> Unit
) {
    Surface(
        shape = UstadShapes.medium,
        color = Surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "Reason: ${report.reason}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            if (report.details.isNotEmpty()) {
                Text(
                    text = "Details: ${report.details}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
            }

            Text(
                text = "Job ID: ${report.jobId} | Reporter: ${report.reporterId}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Button(
                    onClick = onResolve,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Resolve Dispute")
                }
            }
        }
    }
}
