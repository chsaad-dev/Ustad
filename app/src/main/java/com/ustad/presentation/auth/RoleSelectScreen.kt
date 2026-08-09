package com.ustad.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectScreen(
    viewModel: AuthViewModel,
    onRoleConfirmed: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf("customer") } // customer | worker | both
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Select Your Role") }) }
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
                text = "How will you use Ustad?",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Choose your primary role (you can switch in settings)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Role 1: Customer
            RoleSelectionCard(
                title = "Customer (Mujhe Karigar Chahiye)",
                subtitle = "I need an electrician, plumber, or technician for repairs",
                icon = Icons.Rounded.Person,
                isSelected = selectedRole == "customer",
                onClick = { selectedRole = "customer" }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Role 2: Worker
            RoleSelectionCard(
                title = "Technician / Worker (Main Karigar Hun)",
                subtitle = "I want to offer my services and earn money",
                icon = Icons.Rounded.Build,
                isSelected = selectedRole == "worker",
                onClick = { selectedRole = "worker" }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Role 3: Both
            RoleSelectionCard(
                title = "Both (Donon)",
                subtitle = "I want to both request jobs and provide services",
                icon = Icons.Rounded.SwapHoriz,
                isSelected = selectedRole == "both",
                onClick = { selectedRole = "both" }
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            UstadPrimaryButton(
                text = if (uiState is AuthUiState.Loading) "Saving Profile..." else "Complete Registration",
                enabled = uiState !is AuthUiState.Loading,
                onClick = {
                    viewModel.selectedRole = selectedRole
                    viewModel.saveProfileAndRole { assignedRole ->
                        onRoleConfirmed(assignedRole)
                    }
                }
            )

            if (uiState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun RoleSelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = UstadShapes.medium,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Primary else BorderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryLight else Surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.lg)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}
