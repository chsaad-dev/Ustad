package com.ustad.domain.repository

import com.ustad.domain.model.WorkerModel
import kotlinx.coroutines.flow.Flow

interface WorkerRepository {
    fun watchNearbyWorkers(latitude: Double, longitude: Double, category: String, radiusKm: Double): Flow<List<WorkerModel>>
    suspend fun setOnlineStatus(workerId: String, isOnline: Boolean): Result<Unit>
    suspend fun updateLocation(workerId: String, latitude: Double, longitude: Double): Result<Unit>
    suspend fun submitVerificationDocs(workerId: String, cnicFront: String, cnicBack: String, selfie: String): Result<Unit>
}
