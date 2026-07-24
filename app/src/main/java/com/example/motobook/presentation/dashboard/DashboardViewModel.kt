package com.example.motobook.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.domain.model.Bike
import com.example.motobook.domain.model.FuelEntry
import com.example.motobook.domain.model.MileageStats
import com.example.motobook.domain.model.ServiceEntry
import com.example.motobook.domain.repository.BikeRepository
import com.example.motobook.domain.repository.FuelRepository
import com.example.motobook.domain.repository.ServiceRepository
import com.example.motobook.utils.MileageCalculator
import kotlinx.coroutines.flow.*

class DashboardViewModel(
    private val bikeRepository: BikeRepository,
    private val fuelRepository: FuelRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    val allBikes: StateFlow<List<Bike>> = bikeRepository.getAllBikes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedBike: StateFlow<Bike?> = allBikes.map { bikes ->
        bikes.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fuelEntries: StateFlow<List<FuelEntry>> = selectedBike.flatMapLatest { bike ->
        if (bike != null) fuelRepository.getFuelEntriesAscendingFlow(bike.bikeId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mileageStats: StateFlow<MileageStats> = fuelEntries.map { entries ->
        MileageCalculator.calculateMileageStats(entries)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MileageStats())

    val lastFuelEntry: StateFlow<FuelEntry?> = fuelEntries.map { list ->
        list.lastOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentServices: StateFlow<List<ServiceEntry>> = selectedBike.flatMapLatest { bike ->
        if (bike != null) serviceRepository.getServiceEntriesByBike(bike.bikeId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(
        private val bikeRepository: BikeRepository,
        private val fuelRepository: FuelRepository,
        private val serviceRepository: ServiceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(bikeRepository, fuelRepository, serviceRepository) as T
        }
    }
}
