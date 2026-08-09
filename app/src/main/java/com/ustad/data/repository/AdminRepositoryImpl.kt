package com.ustad.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.domain.model.JobModel
import com.ustad.domain.model.WorkerModel
import com.ustad.domain.repository.AdminRepository
import com.ustad.domain.repository.AdminStats
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminRepository {

    override fun watchPendingVerifications(): Flow<List<WorkerModel>> = callbackFlow {
        val listener = firestore.collection("workers")
            .whereEqualTo("isVerified", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val workers = snapshot?.documents?.mapNotNull { it.toObject(WorkerModel::class.java) } ?: emptyList()
                trySend(workers)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun approveWorker(workerId: String): Result<Unit> {
        return try {
            firestore.collection("workers").document(workerId)
                .update("isVerified", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectWorker(workerId: String, reason: String): Result<Unit> {
        return try {
            firestore.collection("workers").document(workerId)
                .update("rejectionReason", reason)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchOverviewStats(): Result<AdminStats> {
        return try {
            val usersCount = firestore.collection("users").get().await().size()
            val jobsCount = firestore.collection("jobs").get().await().size()
            val pendingVerifications = firestore.collection("workers")
                .whereEqualTo("isVerified", false).get().await().size()
            Result.success(
                AdminStats(
                    totalUsers = usersCount,
                    activeJobs = jobsCount,
                    pendingVerifications = pendingVerifications,
                    jobsToday = jobsCount
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchJobs(statusFilter: String?): Result<List<JobModel>> {
        return try {
            val query = if (statusFilter != null) {
                firestore.collection("jobs").whereEqualTo("status", statusFilter)
            } else {
                firestore.collection("jobs")
            }
            val snapshot = query.get().await()
            val jobs = snapshot.documents.mapNotNull { it.toObject(JobModel::class.java) }
            Result.success(jobs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
