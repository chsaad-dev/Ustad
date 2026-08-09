package com.ustad.domain.repository

import android.app.Activity
import com.ustad.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun sendOtp(activity: Activity, phone: String, onCodeSent: (String) -> Unit, onError: (String) -> Unit)
    suspend fun verifyOtp(verificationId: String, code: String): Result<UserModel>
    fun authStateChanges(): Flow<UserModel?>
    suspend fun createOrUpdateUserProfile(user: UserModel): Result<Unit>
    suspend fun isAdmin(): Boolean
}
