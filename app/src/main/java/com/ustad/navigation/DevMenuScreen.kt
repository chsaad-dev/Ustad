package com.ustad.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ustad.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevMenuScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToCustomer: () -> Unit,
    onNavigateToWorker: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustad Dev & Navigation Switcher") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
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
                text = "Phase 0 Navigation Skeleton",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Use these debug shortcuts to test graph transitions:",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(Spacing.xl))

            Button(
                onClick = onNavigateToAuth,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Go to Auth Graph (Splash / Login / Role Select)")
            }
            Spacer(modifier = Modifier.height(Spacing.md))

            Button(
                onClick = onNavigateToCustomer,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Go to Customer Graph (Home, Create Job, Tracking)")
            }
            Spacer(modifier = Modifier.height(Spacing.md))

            Button(
                onClick = onNavigateToWorker,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Go to Worker Graph (Dashboard, Requests, Active Job)")
            }
            Spacer(modifier = Modifier.height(Spacing.md))

            Button(
                onClick = onNavigateToAdmin,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Go to Admin Graph (Debug Shortcut)")
            }
        }
    }
}
