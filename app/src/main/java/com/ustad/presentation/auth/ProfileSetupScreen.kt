package com.ustad.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: AuthViewModel,
    onProfileSet: () -> Unit
) {
    var nameInput by remember { mutableStateOf("Ali") }
    var cityInput by remember { mutableStateOf("Sahiwal") }
    var selectedLanguage by remember { mutableStateOf("ur") } // ur | en

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile Setup") }) }
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
                text = "Welcome to Ustad!",
                style = MaterialTheme.typography.headlineSmall,
                color = Primary
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Please enter your details",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Full Name") },
                singleLine = true,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("City (e.g. Sahiwal)") },
                singleLine = true,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            Text("Preferred Language:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                FilterChip(
                    selected = selectedLanguage == "ur",
                    onClick = { selectedLanguage = "ur" },
                    label = { Text("Urdu (اردو)") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedLanguage == "en",
                    onClick = { selectedLanguage = "en" },
                    label = { Text("English") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            UstadPrimaryButton(
                text = "Continue to Role Selection",
                enabled = nameInput.isNotBlank() && cityInput.isNotBlank(),
                onClick = {
                    viewModel.userName = nameInput
                    viewModel.userCity = cityInput
                    viewModel.userLanguage = selectedLanguage
                    onProfileSet()
                }
            )
        }
    }
}
