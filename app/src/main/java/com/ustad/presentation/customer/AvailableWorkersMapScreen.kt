package com.ustad.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ustad.domain.model.WorkerModel
import com.ustad.presentation.components.WorkerCard
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableWorkersMapScreen(
    customerViewModel: CustomerViewModel,
    onNavigateBack: () -> Unit,
    onSelectWorker: (WorkerModel) -> Unit
) {
    val onlineWorkersWithDist by customerViewModel.nearbyWorkers.collectAsState()
    var viewMode by remember { mutableStateOf("list") } // list | map

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Ustads Nearby") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewMode = if (viewMode == "list") "map" else "list" }) {
                        Icon(
                            if (viewMode == "list") Icons.Rounded.Map else Icons.Rounded.PersonPinCircle,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(
                color = PrimaryLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.PersonPinCircle, contentDescription = null, tint = Primary)
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "Showing ${onlineWorkersWithDist.size} active, verified Ustads nearby",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            if (viewMode == "map") {
                // Interactive Radar Map Simulation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SecondaryDark),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Rounded.Map,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "Interactive Radar Map",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Locating verified Ustads within 5km radius",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                if (onlineWorkersWithDist.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active Ustads online right now in your area.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(onlineWorkersWithDist) { item ->
                            WorkerCard(
                                worker = item.worker,
                                distanceKmStr = "${"%.1f".format(item.distanceKm)} km",
                                onCallClick = {},
                                onBookClick = { onSelectWorker(item.worker) }
                            )
                        }
                    }
                }
            }
        }
    }
}
