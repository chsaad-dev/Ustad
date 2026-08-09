package com.ustad.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ustad.domain.model.ReportModel
import com.ustad.domain.model.WorkerModel
import com.ustad.domain.repository.AdminRepository
import com.ustad.domain.repository.AdminStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _pendingWorkers = MutableStateFlow<List<WorkerModel>>(emptyList())
    val pendingWorkers: StateFlow<List<WorkerModel>> = _pendingWorkers.asStateFlow()

    private val _adminStats = MutableStateFlow(AdminStats())
    val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

    val reportsFlow: StateFlow<List<ReportModel>> = adminRepository.watchReports() as? StateFlow<List<ReportModel>> 
        ?: MutableStateFlow(emptyList())

    var selectedWorker = MutableStateFlow<WorkerModel?>(null)
    var alertMessage = MutableStateFlow<String?>(null)
    var actionSuccessMessage = MutableStateFlow<String?>(null)

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            adminRepository.watchPendingVerifications().collect { workers ->
                _pendingWorkers.value = workers
            }
        }
        viewModelScope.launch {
            val result = adminRepository.fetchOverviewStats()
            result.onSuccess { stats ->
                _adminStats.value = stats
            }
        }
    }

    fun approveWorker(workerId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = adminRepository.approveWorker(workerId)
            result.onSuccess {
                actionSuccessMessage.value = "Worker application approved successfully! 🔰"
                onComplete()
            }.onFailure { error ->
                alertMessage.value = "Permission Denied: Only accounts with Admin Custom Claim can approve verification requests. (${error.message})"
            }
        }
    }

    fun rejectWorker(workerId: String, reason: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val result = adminRepository.rejectWorker(workerId, reason)
            result.onSuccess {
                actionSuccessMessage.value = "Worker application rejected."
                onComplete()
            }.onFailure { error ->
                alertMessage.value = "Failed to submit rejection: ${error.message}"
            }
        }
    }

    fun resolveReport(reportId: String, action: String) {
        viewModelScope.launch {
            val result = adminRepository.resolveReport(reportId, action)
            result.onSuccess {
                actionSuccessMessage.value = "Report resolved."
            }.onFailure { error ->
                alertMessage.value = "Failed to resolve report: ${error.message}"
            }
        }
    }

    fun selectWorkerForReview(worker: WorkerModel) {
        selectedWorker.value = worker
    }

    fun clearMessages() {
        alertMessage.value = null
        actionSuccessMessage.value = null
    }
}
