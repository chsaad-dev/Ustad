package com.ustad.domain.model

data class ReportModel(
    val id: String = "",
    val jobId: String = "",
    val reporterId: String = "",
    val reportedId: String = "",
    val reporterRole: String = "customer",
    val reason: String = "",
    val details: String = "",
    val status: String = "pending", // pending | resolved | dismissed
    val createdAt: Long = System.currentTimeMillis()
)
