package com.ustad.presentation.customer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ustad.core.geohash.GeohashHelper
import com.ustad.core.media.ImageCompressor
import com.ustad.core.media.VoiceRecorderManager
import com.ustad.core.util.DistanceHelper
import com.ustad.core.util.SeedDataHelper
import com.ustad.domain.model.CategoryTemplates
import com.ustad.domain.model.JobModel
import com.ustad.domain.model.ServiceCategory
import com.ustad.domain.model.WorkerModel
import com.ustad.domain.repository.JobRepository
import com.ustad.domain.repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class WorkerWithDistance(
    val worker: WorkerModel,
    val distanceKm: Double,
    val distanceFormatted: String
)

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val workerRepository: WorkerRepository,
    private val seedDataHelper: SeedDataHelper,
    private val voiceRecorderManager: VoiceRecorderManager,
    private val imageCompressor: ImageCompressor,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Customer Location (Default: Sahiwal Center)
    var customerLat: Double = 30.6682
    var customerLon: Double = 73.1114
    var customerAddress: String = "Farid Town, Sahiwal, Punjab"

    // Home State
    var searchQuery = MutableStateFlow("")
    private val _recentJobs = MutableStateFlow<List<JobModel>>(emptyList())
    val recentJobs: StateFlow<List<JobModel>> = _recentJobs.asStateFlow()

    // Create Job State
    var selectedCategory = MutableStateFlow<ServiceCategory?>(CategoryTemplates.categories[0])
    var jobDescription = MutableStateFlow("")
    var selectedTemplateChips = MutableStateFlow<Set<String>>(emptySet())
    var isUrgent = MutableStateFlow(false)
    var isRecordingVoice = MutableStateFlow(false)
    var voiceNoteFile = MutableStateFlow<File?>(null)
    var uploadedPhotos = MutableStateFlow<List<File>>(emptyList())

    // Created Job & Tracking State
    var activeJobId = MutableStateFlow<String?>(null)
    private val _activeJob = MutableStateFlow<JobModel?>(null)
    val activeJob: StateFlow<JobModel?> = _activeJob.asStateFlow()

    // Find Nearby Workers State
    private val _nearbyWorkers = MutableStateFlow<List<WorkerWithDistance>>(emptyList())
    val nearbyWorkers: StateFlow<List<WorkerWithDistance>> = _nearbyWorkers.asStateFlow()
    val isSearchingWorkers = MutableStateFlow(false)

    init {
        // Seed mock workers on startup if Firestore collection is empty
        viewModelScope.launch {
            seedDataHelper.seedMockWorkersIfEmpty()
        }
    }

    fun selectCategory(category: ServiceCategory) {
        selectedCategory.value = category
        selectedTemplateChips.value = emptySet()
    }

    fun toggleTemplateChip(chipText: String) {
        val current = selectedTemplateChips.value.toMutableSet()
        if (current.contains(chipText)) {
            current.remove(chipText)
        } else {
            current.add(chipText)
        }
        selectedTemplateChips.value = current
    }

    fun startVoiceRecording() {
        isRecordingVoice.value = true
        voiceNoteFile.value = voiceRecorderManager.startRecording(maxDurationSeconds = 60)
    }

    fun stopVoiceRecording() {
        isRecordingVoice.value = false
        val file = voiceRecorderManager.stopRecording()
        if (file != null) {
            voiceNoteFile.value = file
        }
    }

    fun addPhoto(file: File) {
        if (uploadedPhotos.value.size < 3) {
            val compressed = imageCompressor.compressImage(file, targetSizeKb = 800)
            uploadedPhotos.value = uploadedPhotos.value + compressed
        }
    }

    fun createJob(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val category = selectedCategory.value?.name ?: "Electrician"
            val chipsText = selectedTemplateChips.value.joinToString(", ")
            val fullDescription = if (chipsText.isNotEmpty()) {
                "[$chipsText] ${jobDescription.value}"
            } else {
                jobDescription.value
            }

            val newJob = JobModel(
                customerId = "customer_demo_uid",
                category = category,
                description = fullDescription,
                templateTag = selectedTemplateChips.value.firstOrNull(),
                isUrgent = isUrgent.value,
                latitude = customerLat,
                longitude = customerLon,
                geohash = GeohashHelper.encode(customerLat, customerLon),
                address = customerAddress,
                city = "Sahiwal",
                status = "pending"
            )

            val result = jobRepository.createJob(newJob)
            result.onSuccess { jobId ->
                activeJobId.value = jobId
                watchJobStatus(jobId)
                findNearbyWorkers(category)
                onCreated(jobId)
            }
        }
    }

    fun findNearbyWorkers(category: String) {
        viewModelScope.launch {
            isSearchingWorkers.value = true
            workerRepository.watchNearbyWorkers(customerLat, customerLon, category, radiusKm = 10.0)
                .collect { workers ->
                    // Haversine distance calculation and nearest-first sort
                    val listWithDistances = workers.map { worker ->
                        val dist = DistanceHelper.calculateHaversineDistanceKm(
                            customerLat, customerLon,
                            worker.latitude, worker.longitude
                        )
                        val formatted = String.format("%.1f km away", dist)
                        WorkerWithDistance(worker, dist, formatted)
                    }.sortedBy { it.distanceKm }

                    _nearbyWorkers.value = listWithDistances
                    isSearchingWorkers.value = false
                }
        }
    }

    fun bookWorker(jobId: String, workerId: String, onBooked: () -> Unit) {
        viewModelScope.launch {
            // Section 8/4A spec: preferredWorkerId = workerId, status remains 'pending'
            jobRepository.requestWorker(jobId, workerId)
            watchJobStatus(jobId)
            onBooked()
        }
    }

    fun watchJobStatus(jobId: String) {
        viewModelScope.launch {
            jobRepository.watchJob(jobId).collect { job ->
                _activeJob.value = job
            }
        }
    }

    fun cancelActiveJob(reason: String) {
        val jobId = activeJobId.value ?: return
        viewModelScope.launch {
            jobRepository.cancelJob(jobId, reason)
        }
    }
}
