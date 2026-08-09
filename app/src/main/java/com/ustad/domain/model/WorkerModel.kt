package com.ustad.domain.model

data class WorkerModel(
    val userId: String = "",
    val displayName: String = "",
    val skills: List<String> = emptyList(),
    val experienceYears: Int = 0,
    val cnicFrontUrl: String = "",
    val cnicBackUrl: String = "",
    val selfieUrl: String = "",
    val isVerified: Boolean = false,
    val verificationTier: String = "basic",
    val isOnline: Boolean = false,
    val rating: Double = 5.0,
    val trustScore: Double = 100.0,
    val totalJobs: Int = 0,
    val completedJobs: Int = 0,
    val cancelledJobs: Int = 0,
    val avgResponseTimeSeconds: Int = 300,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val geohash: String = "",
    val address: String = "",
    val bio: String = "",
    val lastOnlineAt: Long = System.currentTimeMillis()
)
