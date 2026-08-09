package com.ustad.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.domain.model.WorkerModel
import com.ustad.domain.repository.WorkerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WorkerRepository {

    override fun watchNearbyWorkers(
        latitude: Double,
        longitude: Double,
        category: String,
        radiusKm: Double
    ): Flow<List<WorkerModel>> = callbackFlow {
        val listener = firestore.collection("workers")
            .whereEqualTo("isOnline", true)
            .whereEqualTo("isVerified", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val workers = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(WorkerModel::class.java)
                } ?: emptyList()
                trySend(workers)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun setOnlineStatus(workerId: String, isOnline: Boolean): Result<Unit> {
        return try {
            firestore.collection("workers").document(workerId)
                .update("isOnline", isOnline, "lastOnlineAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLocation(workerId: String, latitude: Double, longitude: Double): Result<Unit> {
        return try {
            firestore.collection("workers").document(workerId)
                .update("latitude", latitude, "longitude", longitude)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitVerificationDocs(
        workerId: String,
        cnicFront: String,
        cnicBack: String,
        selfie: String
    ): Result<Unit> {
        return try {
            firestore.collection("workers").document(workerId)
                .update(
                    mapOf(
                        "cnicFrontUrl" to cnicFront,
                        "cnicBackUrl" to cnicBack,
                        "selfieUrl" to selfie,
                        "isVerified" to false
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
