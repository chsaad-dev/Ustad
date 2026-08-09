package com.ustad.domain.model

data class JobModel(
    val id: String = "",
    val customerId: String = "",
    val workerId: String? = null,
    val preferredWorkerId: String? = null,
    val category: String = "",
    val description: String = "",
    val templateTag: String? = null,
    val isUrgent: Boolean = false,
    val photoUrls: List<String> = emptyList(),
    val afterPhotoUrls: List<String> = emptyList(),
    val voiceUrl: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val geohash: String = "",
    val address: String = "",
    val city: String = "Sahiwal",
    val status: String = "pending", // pending, accepted, onTheWay, workStarted, completed, cancelled, rejected
    val createdAt: Long = System.currentTimeMillis(),
    val acceptedAt: Long? = null,
    val completedAt: Long? = null,
    val cancellationReason: String? = null,
    val isRated: Boolean = false
)
