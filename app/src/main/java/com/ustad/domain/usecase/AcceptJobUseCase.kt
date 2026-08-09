package com.ustad.domain.usecase

import com.ustad.domain.repository.JobRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcceptJobUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    /**
     * Executes atomic Firestore transaction to claim job.
     * Re-reads status inside transaction, setting workerId & status = 'accepted' iff still pending.
     * Returns Result.failure with friendly error message if another worker accepted first.
     */
    suspend operator fun invoke(jobId: String, workerId: String): Result<Unit> {
        return jobRepository.acceptJob(jobId, workerId)
    }
}
