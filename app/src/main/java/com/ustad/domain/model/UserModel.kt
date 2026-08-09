package com.ustad.domain.model

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = "customer", // customer | worker | both
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val fcmToken: String = "",
    val language: String = "ur",
    val city: String = "Sahiwal",
    val referralCode: String = "",
    val referredBy: String? = null,
    val referralCount: Int = 0
)

