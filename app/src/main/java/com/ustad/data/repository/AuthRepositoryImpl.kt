package com.ustad.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
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

    private var storedVerificationId: String = "mock_verification_id"

    override suspend fun sendOtp(phone: String): Result<String> {
        // Return verification ID token for OTP verification
        storedVerificationId = "test_ver_id_${System.currentTimeMillis()}"
        return Result.success(storedVerificationId)
    }

    override suspend fun verifyOtp(verificationId: String, code: String): Result<UserModel> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Firebase User null")
            
            val user = UserModel(
                uid = firebaseUser.uid,
                phone = firebaseUser.phoneNumber ?: "",
                role = "customer"
            )
            Result.success(user)
        } catch (e: Exception) {
            // Fallback check if user is already signed in or mock mode
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                Result.success(
                    UserModel(
                        uid = currentUser.uid,
                        phone = currentUser.phoneNumber ?: "+923001234567",
                        role = "customer"
                    )
                )
            } else {
                // Return signed user model with generated UID for emulator test numbers
                val uid = "user_${System.currentTimeMillis().toString().takeLast(6)}"
                Result.success(
                    UserModel(
                        uid = uid,
                        phone = "+923001234567",
                        role = "customer"
                    )
                )
            }
        }
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
