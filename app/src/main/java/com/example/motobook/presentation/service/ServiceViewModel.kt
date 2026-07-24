package com.example.motobook.presentation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.domain.model.ServiceEntry
import com.example.motobook.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ServiceUiState {
    object Idle : ServiceUiState()
    object Saved : ServiceUiState()
    data class Error(val message: String) : ServiceUiState()
}

class ServiceViewModel(
    private val serviceRepository: ServiceRepository,
    val bikeId: Long
) : ViewModel() {

    val serviceEntries: StateFlow<List<ServiceEntry>> = serviceRepository.getServiceEntriesByBike(bikeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalServiceCost: StateFlow<Float> = serviceEntries.map { list ->
        list.sumOf { it.totalCost.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    private val _uiState = MutableStateFlow<ServiceUiState>(ServiceUiState.Idle)
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    fun saveServiceEntry(
        serviceId: Long = 0,
        date: Long,
        odometerStr: String,
        category: String,
        itemsServiced: List<String>,
        isOfficial: Boolean,
        centerName: String?,
        totalCostStr: String,
        notes: String?
    ) {
        val odo = odometerStr.toFloatOrNull() ?: 0f
        val cost = totalCostStr.toFloatOrNull() ?: 0f

        val entry = ServiceEntry(
            serviceId = serviceId,
            bikeId = bikeId,
            date = date,
            odometer = odo,
            category = category,
            itemsServiced = itemsServiced,
            isOfficialServiceCenter = isOfficial,
            serviceCenterName = centerName?.ifBlank { null },
            totalCost = cost,
            notes = notes?.ifBlank { null }
        )

        viewModelScope.launch {
            try {
                if (serviceId == 0L) {
                    serviceRepository.insertServiceEntry(entry)
                } else {
                    serviceRepository.updateServiceEntry(entry)
                }
                _uiState.value = ServiceUiState.Saved
            } catch (e: Exception) {
                _uiState.value = ServiceUiState.Error(e.localizedMessage ?: "Failed to save service entry")
            }
        }
    }

    fun deleteServiceEntry(entry: ServiceEntry) {
        viewModelScope.launch {
            serviceRepository.deleteServiceEntry(entry)
        }
    }

    class Factory(
        private val repository: ServiceRepository,
        private val bikeId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ServiceViewModel(repository, bikeId) as T
        }
    }
}
