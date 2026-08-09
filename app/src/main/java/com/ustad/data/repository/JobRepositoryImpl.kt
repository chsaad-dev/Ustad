package com.ustad.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.domain.model.JobModel
import com.ustad.domain.repository.JobRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : JobRepository {

    override fun watchJob(jobId: String): Flow<JobModel?> = callbackFlow {
        val listener = firestore.collection("jobs").document(jobId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(JobModel::class.java))
            }
        awaitClose { listener.remove() }
    }

    override fun watchCustomerJobs(customerId: String): Flow<List<JobModel>> = callbackFlow {
        val listener = firestore.collection("jobs")
            .whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val jobs = snapshot?.documents?.mapNotNull { it.toObject(JobModel::class.java) } ?: emptyList()
                trySend(jobs)
            }
        awaitClose { listener.remove() }
    }

    override fun watchPendingJobsForWorker(skills: List<String>, geohashPrefix: String): Flow<List<JobModel>> = callbackFlow {
        val listener = firestore.collection("jobs")
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val jobs = snapshot?.documents?.mapNotNull { it.toObject(JobModel::class.java) } ?: emptyList()
                trySend(jobs)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createJob(job: JobModel): Result<String> {
        return try {
            val docRef = firestore.collection("jobs").document()
            val jobWithId = job.copy(id = docRef.id)
            docRef.set(jobWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestWorker(jobId: String, workerId: String): Result<Unit> {
        return try {
            firestore.collection("jobs").document(jobId)
                .update("preferredWorkerId", workerId)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptJob(jobId: String, workerId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val docRef = firestore.collection("jobs").document(jobId)
                val snapshot = transaction.get(docRef)
                val status = snapshot.getString("status")
                if (status == "pending") {
                    transaction.update(docRef, mapOf(
                        "workerId" to workerId,
                        "status" to "accepted",
                        "acceptedAt" to System.currentTimeMillis()
                    ))
                } else {
                    throw IllegalStateException("Job was already accepted by another worker")
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStatus(jobId: String, status: String): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>("status" to status)
            if (status == "completed") {
                updates["completedAt"] = System.currentTimeMillis()
            }
            firestore.collection("jobs").document(jobId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelJob(jobId: String, reason: String): Result<Unit> {
        return try {
            firestore.collection("jobs").document(jobId).update(
                mapOf(
                    "status" to "cancelled",
                    "cancellationReason" to reason
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
