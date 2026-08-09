package com.ustad.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ustad.domain.model.UserModel
import com.ustad.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class OtpSent(val verificationId: String, val phone: String) : AuthUiState()
    data class Authenticated(val user: UserModel) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var phoneNumber: String = "+923001234567"
    var verificationId: String = "mock_ver_id"
    var otpCode: String = "123456"

    var userName: String = ""
    var userCity: String = "Sahiwal"
    var userLanguage: String = "ur"
    var selectedRole: String = "customer" // customer | worker | both ONLY

    fun sendOtp(phone: String) {
        phoneNumber = phone
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.sendOtp(phone)
            result.onSuccess { verId ->
                verificationId = verId
                _uiState.value = AuthUiState.OtpSent(verId, phone)
            }.onFailure { err ->
                _uiState.value = AuthUiState.Error(err.message ?: "Failed to send OTP")
            }
        }
    }

    fun verifyOtp(code: String) {
        otpCode = code
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.verifyOtp(verificationId, code)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Authenticated(user)
            }.onFailure { err ->
                _uiState.value = AuthUiState.Error(err.message ?: "Invalid OTP Code")
            }
        }
    }

    fun saveProfileAndRole(onSuccess: (String) -> Unit) {
        // Enforce strict security rule: role MUST be one of customer, worker, both
        val safeRole = when (selectedRole.lowercase()) {
            "worker" -> "worker"
            "both" -> "both"
            else -> "customer" // Defaults safely to customer, NEVER admin!
        }

        val uid = (uiState.value as? AuthUiState.Authenticated)?.user?.uid ?: "test_user_uid"
        val user = UserModel(
            uid = uid,
            name = userName.ifEmpty { "Ali" },
            phone = phoneNumber,
            role = safeRole,
            city = userCity,
            language = userLanguage
        )

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.createOrUpdateUserProfile(user)
            result.onSuccess {
                _uiState.value = AuthUiState.Authenticated(user)
                onSuccess(safeRole)
            }.onFailure { err ->
                _uiState.value = AuthUiState.Error(err.message ?: "Failed to save profile")
            }
        }
    }
}
