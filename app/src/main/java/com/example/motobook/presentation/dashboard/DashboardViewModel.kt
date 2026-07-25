package com.example.motobook.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.domain.model.*
import com.example.motobook.domain.repository.*
import com.example.motobook.utils.MileageCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val bikeRepository: BikeRepository,
    private val fuelRepository: FuelRepository,
    private val serviceRepository: ServiceRepository,
    private val washRepository: WashRepository,
    private val reminderRepository: ReminderRepository
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

    val washEntries: StateFlow<List<WashEntry>> = selectedBike.flatMapLatest { bike ->
        if (bike != null) washRepository.getWashEntriesByBike(bike.bikeId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<MaintenanceReminder>> = selectedBike.flatMapLatest { bike ->
        if (bike != null) reminderRepository.getRemindersByBike(bike.bikeId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentOdometer: StateFlow<Float?> = combine(fuelEntries, recentServices) { fuels, services ->
        val maxFuelOdo = fuels.maxOfOrNull { it.odometer } ?: 0f
        val maxServiceOdo = services.mapNotNull { it.odometer }.maxOrNull() ?: 0f
        maxOf(maxFuelOdo, maxServiceOdo).takeIf { it > 0f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addReminder(reminder: MaintenanceReminder) {
        viewModelScope.launch {
            reminderRepository.insertReminder(reminder)
        }
    }

    fun completeReminder(reminder: MaintenanceReminder) {
        viewModelScope.launch {
            val currentOdo = currentOdometer.value ?: 0f
            if (reminder.intervalKm != null && reminder.intervalKm > 0) {
                // Auto schedule next cycle!
                val nextReminder = reminder.copy(
                    lastDoneOdometer = currentOdo,
                    lastDoneDate = System.currentTimeMillis(),
                    dueOdometer = currentOdo + reminder.intervalKm,
                    isCompleted = false
                )
                reminderRepository.updateReminder(nextReminder)
            } else {
                val updated = reminder.copy(
                    isCompleted = true,
                    lastDoneOdometer = currentOdo,
                    lastDoneDate = System.currentTimeMillis()
                )
                reminderRepository.updateReminder(updated)
            }
        }
    }

    fun deleteReminder(reminder: MaintenanceReminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
        }
    }

    class Factory(
        private val bikeRepository: BikeRepository,
        private val fuelRepository: FuelRepository,
        private val serviceRepository: ServiceRepository,
        private val washRepository: WashRepository,
        private val reminderRepository: ReminderRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(
                bikeRepository,
                fuelRepository,
                serviceRepository,
                washRepository,
                reminderRepository
            ) as T
        }
    }
}

