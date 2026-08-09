package com.ustad.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onOtpSent: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    var phoneInput by remember { mutableStateOf("+92 300 1234567") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.OtpSent) {
            onOtpSent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login to Ustad") },
                actions = {
                    TextButton(onClick = onNavigateToDevMenu) {
                        Text("Dev Menu")
                    }
                }
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
                text = "Mahir Karigar 30 Minute Me",
                style = MaterialTheme.typography.headlineSmall,
                color = Primary
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Enter your phone number to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Test Credentials Helper Banner for Local Testing
            Surface(
                shape = UstadShapes.medium,
                color = PrimaryLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text(
                        text = "🧪 Test Credentials (Firebase Emulator):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phone: +92 300 1234567\nCode: 123456",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            OutlinedTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null, tint = Primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            val context = androidx.compose.ui.platform.LocalContext.current
            val activity = context as? android.app.Activity

            UstadPrimaryButton(
                text = if (uiState is AuthUiState.Loading) "Sending Code..." else "Send Verification Code",
                enabled = phoneInput.isNotBlank() && uiState !is AuthUiState.Loading,
                onClick = {
                    if (activity != null) {
                        viewModel.sendOtp(activity, phoneInput)
                    }
                }
            )


            if (uiState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = (uiState as AuthUiState.Error).message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
