package com.ustad.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.domain.model.UserModel
import com.ustad.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun sendOtp(
        activity: Activity,
        phone: String,
        onCodeSent: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    firebaseAuth.signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onError(e.message ?: "Phone verification failed")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    onCodeSent(verificationId)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)


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
            Result.failure(e)
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
