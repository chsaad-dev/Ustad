package com.ustad.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class SplashState {
    object Loading : SplashState()
    data class Success(val startDestination: String) : SplashState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                _splashState.value = SplashState.Success(Screen.AuthGraph.route)
                return@launch
            }

            // 1. Check Admin Custom Claim
            if (authRepository.isAdmin()) {
                _splashState.value = SplashState.Success(Screen.AdminGraph.route)
                return@launch
            }

            // 2. Fetch User Profile & Role from Firestore
            try {
                val doc = firestore.collection("users").document(currentUser.uid).get().await()
                val role = doc.getString("role")?.lowercase() ?: "customer"
                val startDest = when (role) {
                    "worker" -> Screen.WorkerGraph.route
                    else -> Screen.CustomerGraph.route
                }
                _splashState.value = SplashState.Success(startDest)
            } catch (e: Exception) {
                _splashState.value = SplashState.Success(Screen.CustomerGraph.route)
            }
        }
    }
}
