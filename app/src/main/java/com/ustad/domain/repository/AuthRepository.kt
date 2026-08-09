package com.ustad.domain.repository

import com.ustad.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendOtp(phone: String): Result<String>
    suspend fun verifyOtp(verificationId: String, code: String): Result<UserModel>
    fun authStateChanges(): Flow<UserModel?>
    suspend fun createOrUpdateUserProfile(user: UserModel): Result<Unit>
    suspend fun isAdmin(): Boolean
}
