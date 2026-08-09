package com.ustad.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    viewModel: AuthViewModel,
    onVerified: () -> Unit
) {
    var otpInput by remember { mutableStateOf("123456") }
    var timerSeconds by remember { mutableIntStateOf(30) }
    var isTimerActive by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(isTimerActive) {
        if (isTimerActive) {
            timerSeconds = 30
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
            isTimerActive = false
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Authenticated) {
            onVerified()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verify Phone Number") })
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
                text = "Enter 6-Digit Code",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Code sent to ${viewModel.phoneNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            OutlinedTextField(
                value = otpInput,
                onValueChange = { if (it.length <= 6) otpInput = it },
                label = { Text("6-Digit OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                shape = UstadShapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            if (isTimerActive) {
                Text(
                    text = "Resend code in 0:${timerSeconds.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            } else {
                TextButton(onClick = { isTimerActive = true }) {
                    Text("Resend Code", color = Primary)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            UstadPrimaryButton(
                text = if (uiState is AuthUiState.Loading) "Verifying..." else "Verify & Continue",
                enabled = otpInput.length == 6 && uiState !is AuthUiState.Loading,
                onClick = {
                    viewModel.verifyOtp(otpInput)
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
