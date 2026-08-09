package com.ustad.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.domain.model.UserModel
import com.ustad.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun sendOtp(phone: String): Result<String> {
        // Will be wired in Phase 1 with Activity callback
        return Result.success("mock_verification_id")
    }

    override suspend fun verifyOtp(verificationId: String, code: String): Result<UserModel> {
        val uid = firebaseAuth.currentUser?.uid ?: "user_123"
        val user = UserModel(uid = uid, phone = "+923000000000", role = "customer")
        return Result.success(user)
    }

    override fun authStateChanges(): Flow<UserModel?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(UserModel(uid = firebaseUser.uid, phone = firebaseUser.phoneNumber ?: ""))
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun createOrUpdateUserProfile(user: UserModel): Result<Unit> {
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAdmin(): Boolean {
        return try {
            val tokenResult = firebaseAuth.currentUser?.getIdToken(true)?.await()
            tokenResult?.claims?.get("admin") == true
        } catch (e: Exception) {
            false
        }
    }
}
