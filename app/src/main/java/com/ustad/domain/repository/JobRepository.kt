package com.ustad.domain.repository

import com.ustad.domain.model.JobModel
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    fun watchJob(jobId: String): Flow<JobModel?>
    fun watchCustomerJobs(customerId: String): Flow<List<JobModel>>
    fun watchPendingJobsForWorker(skills: List<String>, geohashPrefix: String): Flow<List<JobModel>>
    suspend fun createJob(job: JobModel): Result<String>
    suspend fun requestWorker(jobId: String, workerId: String): Result<Unit>
    suspend fun acceptJob(jobId: String, workerId: String): Result<Unit>
    suspend fun updateStatus(jobId: String, status: String): Result<Unit>
    suspend fun cancelJob(jobId: String, reason: String): Result<Unit>
}
