package com.example.motobook.presentation.mileage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.domain.model.MileageStats
import com.example.motobook.domain.repository.FuelRepository
import com.example.motobook.utils.MileageCalculator
import kotlinx.coroutines.flow.*

class MileageViewModel(
    private val fuelRepository: FuelRepository,
    val bikeId: Long
) : ViewModel() {

    val mileageStats: StateFlow<MileageStats> = fuelRepository.getFuelEntriesAscendingFlow(bikeId)
        .map { entries ->
            MileageCalculator.calculateMileageStats(entries)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MileageStats())

    class Factory(
        private val repository: FuelRepository,
        private val bikeId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MileageViewModel(repository, bikeId) as T
        }
    }
}
