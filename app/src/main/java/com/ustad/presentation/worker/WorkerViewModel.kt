package com.ustad.presentation.worker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ustad.core.util.DistanceHelper
import com.ustad.domain.model.JobModel
import com.ustad.domain.repository.JobRepository
import com.ustad.domain.repository.WorkerRepository
import com.ustad.domain.usecase.AcceptJobUseCase
import com.ustad.service.WorkerOnlineForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobWithDistance(
    val job: JobModel,
    val distanceKm: Double,
    val distanceFormatted: String
)

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val workerRepository: WorkerRepository,
    private val jobRepository: JobRepository,
    private val acceptJobUseCase: AcceptJobUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var workerId: String = "mock_worker_electrician_1"
    var workerName: String = "Ustad Tariq"
    var workerSkills: List<String> = listOf("Electrician")
    var workerLat: Double = 30.6710
    var workerLon: Double = 73.1130

    // Online Toggle State
    var isOnline = MutableStateFlow(true)

    // Worker Stats
    var completedJobsToday = MutableStateFlow(12)
    var workerRating = MutableStateFlow(4.9)

    // Local dismissal set for rejected jobs (does not modify Firestore job doc)
    private val dismissedJobIds = MutableStateFlow<Set<String>>(emptySet())

    // Pending jobs matching worker skill & area
    private val _pendingJobs = MutableStateFlow<List<JobWithDistance>>(emptyList())
    val pendingJobs: StateFlow<List<JobWithDistance>> = _pendingJobs.asStateFlow()

    // Active Job State
    var activeJobId = MutableStateFlow<String?>(null)
    private val _activeJob = MutableStateFlow<JobModel?>(null)
    val activeJob: StateFlow<JobModel?> = _activeJob.asStateFlow()

    // Friendly Alert message for UI (e.g. Double-accept race alert)
    var alertMessage = MutableStateFlow<String?>(null)
    var isAccepting = MutableStateFlow(false)

    init {
        startWorkerSession()
    }

    fun startWorkerSession() {
        // Start foreground service when online
        if (isOnline.value) {
            WorkerOnlineForegroundService.startService(context)
        }
        listenToPendingJobs()
    }

    fun toggleOnlineStatus(online: Boolean) {
        isOnline.value = online
        viewModelScope.launch {
            workerRepository.setOnlineStatus(workerId, online)
            if (online) {
                WorkerOnlineForegroundService.startService(context)
            } else {
                WorkerOnlineForegroundService.stopService(context)
            }
        }
    }

    private fun listenToPendingJobs() {
        viewModelScope.launch {
            jobRepository.watchPendingJobsForWorker(workerSkills, "ttmg").collect { jobs ->
                val filtered = jobs.filter { job ->
                    !dismissedJobIds.value.contains(job.id) &&
                    (workerSkills.contains(job.category) || job.preferredWorkerId == workerId)
                }.map { job ->
                    val dist = DistanceHelper.calculateHaversineDistanceKm(
                        workerLat, workerLon,
                        job.latitude, job.longitude
                    )
                    val formatted = String.format("%.1f km away", dist)
                    JobWithDistance(job, dist, formatted)
                }.sortedBy { it.distanceKm }

                _pendingJobs.value = filtered
            }
        }
    }

    fun acceptJobTransactional(jobId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isAccepting.value = true
            alertMessage.value = null

            val result = acceptJobUseCase(jobId, workerId)
            isAccepting.value = false

            result.onSuccess {
                activeJobId.value = jobId
                watchActiveJob(jobId)
                onSuccess()
            }.onFailure { error ->
                val msg = error.message ?: "This job was already accepted by another Ustad"
                alertMessage.value = msg
            }
        }
    }

    fun rejectJobLocally(jobId: String) {
        // Section 16 SCOPE 4: Only remove from this worker's local visible feed
        dismissedJobIds.value = dismissedJobIds.value + jobId
        _pendingJobs.value = _pendingJobs.value.filter { it.job.id != jobId }
    }

    fun watchActiveJob(jobId: String) {
        viewModelScope.launch {
            jobRepository.watchJob(jobId).collect { job ->
                _activeJob.value = job
            }
        }
    }

    fun advanceJobStatus(status: String) {
        val jobId = activeJobId.value ?: return
        viewModelScope.launch {
            jobRepository.updateStatus(jobId, status)
        }
    }

    fun clearAlert() {
        alertMessage.value = null
    }
}
