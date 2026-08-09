package com.ustad.domain.repository

import com.ustad.domain.model.JobModel
import com.ustad.domain.model.ReportModel
import com.ustad.domain.model.WorkerModel
import kotlinx.coroutines.flow.Flow

data class AdminStats(
    val totalUsers: Int = 0,
    val activeJobs: Int = 0,
    val pendingVerifications: Int = 0,
    val jobsToday: Int = 0
)

interface AdminRepository {
    fun watchPendingVerifications(): Flow<List<WorkerModel>>
    suspend fun approveWorker(workerId: String): Result<Unit>
    suspend fun rejectWorker(workerId: String, reason: String): Result<Unit>
    suspend fun fetchOverviewStats(): Result<AdminStats>
    suspend fun fetchJobs(statusFilter: String?): Result<List<JobModel>>
    fun watchReports(): Flow<List<ReportModel>>
    suspend fun resolveReport(reportId: String, action: String): Result<Unit>
}
