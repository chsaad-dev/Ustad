package com.ustad.core.util

import com.google.firebase.firestore.FirebaseFirestore
import com.ustad.core.geohash.GeohashHelper
import com.ustad.domain.model.WorkerModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataHelper @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun seedMockWorkersIfEmpty() {
        try {
            val snapshot = firestore.collection("workers").limit(1).get().await()
            if (snapshot.isEmpty) {
                val mockWorkers = listOf(
                    WorkerModel(
                        userId = "mock_worker_electrician_1",
                        displayName = "Ustad Tariq",
                        skills = listOf("Electrician"),
                        experienceYears = 12,
                        isVerified = true,
                        isOnline = true,
                        rating = 4.9,
                        trustScore = 98.0,
                        totalJobs = 88,
                        completedJobs = 84,
                        latitude = 30.6710,
                        longitude = 73.1130,
                        geohash = GeohashHelper.encode(30.6710, 73.1130),
                        address = "Farid Town, Sahiwal",
                        bio = "12+ years experience in domestic & commercial electrical wiring & fan repair."
                    ),
                    WorkerModel(
                        userId = "mock_worker_plumber_1",
                        displayName = "Ustad Rashid",
                        skills = listOf("Plumber"),
                        experienceYears = 9,
                        isVerified = true,
                        isOnline = true,
                        rating = 4.8,
                        trustScore = 95.0,
                        totalJobs = 65,
                        completedJobs = 62,
                        latitude = 30.6750,
                        longitude = 73.1180,
                        geohash = GeohashHelper.encode(30.6750, 73.1180),
                        address = "High Street, Sahiwal",
                        bio = "Sanitary fittings, water tank cleaning, and leak repairs."
                    ),
                    WorkerModel(
                        userId = "mock_worker_ac_1",
                        displayName = "Ustad Imran AC Specialist",
                        skills = listOf("AC"),
                        experienceYears = 15,
                        isVerified = true,
                        isOnline = true,
                        rating = 4.95,
                        trustScore = 99.0,
                        totalJobs = 125,
                        completedJobs = 120,
                        latitude = 30.6800,
                        longitude = 73.1250,
                        geohash = GeohashHelper.encode(30.6800, 73.1250),
                        address = "College Road, Sahiwal",
                        bio = "Master AC servicing, gas refill, inverter AC diagnosis."
                    ),
                    WorkerModel(
                        userId = "mock_worker_carpenter_1",
                        displayName = "Ustad Bilal",
                        skills = listOf("Carpenter"),
                        experienceYears = 8,
                        isVerified = true,
                        isOnline = true,
                        rating = 4.7,
                        trustScore = 92.0,
                        totalJobs = 48,
                        completedJobs = 45,
                        latitude = 30.6870,
                        longitude = 73.1320,
                        geohash = GeohashHelper.encode(30.6870, 73.1320),
                        address = "Main Market, Sahiwal",
                        bio = "Door lock replacement, furniture assembly, and wooden repairs."
                    ),
                    WorkerModel(
                        userId = "mock_worker_painter_1",
                        displayName = "Ustad Akram",
                        skills = listOf("Painter"),
                        experienceYears = 10,
                        isVerified = true,
                        isOnline = true,
                        rating = 4.85,
                        trustScore = 96.0,
                        totalJobs = 82,
                        completedJobs = 78,
                        latitude = 30.6950,
                        longitude = 73.1400,
                        geohash = GeohashHelper.encode(30.6950, 73.1400),
                        address = "Scheme No. 3, Sahiwal",
                        bio = "Single room paint, dampness touchup, doors and windows polish."
                    ),
                    WorkerModel(
                        userId = "mock_worker_bikemechanic_1",
                        displayName = "Ustad Usman Bike Master",
                        skills = listOf("Bike Mechanic"),
                        experienceYears = 11,
                        isVerified = true,
                        isOnline = true,
                        rating = 4.9,
                        trustScore = 97.0,
                        totalJobs = 100,
                        completedJobs = 95,
                        latitude = 30.7050,
                        longitude = 73.1500,
                        geohash = GeohashHelper.encode(30.7050, 73.1500),
                        address = "G T Road, Sahiwal",
                        bio = "70cc & 125cc bike tuning, engine oil change, brake overhaul."
                    )
                )

                for (worker in mockWorkers) {
                    firestore.collection("workers").document(worker.userId).set(worker).await()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
