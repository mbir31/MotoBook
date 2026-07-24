package com.example.motobook.presentation.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.domain.model.FuelEntry
import com.example.motobook.domain.repository.FuelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class FuelUiState {
    object Idle : FuelUiState()
    object Saved : FuelUiState()
    data class Error(val message: String) : FuelUiState()
}

class FuelViewModel(
    private val fuelRepository: FuelRepository,
    val bikeId: Long
) : ViewModel() {

    val fuelEntries: StateFlow<List<FuelEntry>> = fuelRepository.getFuelEntriesByBike(bikeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCost: StateFlow<Float> = fuelEntries.map { list ->
        list.sumOf { it.totalCost.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val totalQuantity: StateFlow<Float> = fuelEntries.map { list ->
        list.sumOf { it.fuelQuantity.toDouble() }.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    private val _uiState = MutableStateFlow<FuelUiState>(FuelUiState.Idle)
    val uiState: StateFlow<FuelUiState> = _uiState.asStateFlow()

    fun saveFuelEntry(
        fuelId: Long = 0,
        date: Long,
        odometerStr: String,
        quantityStr: String,
        priceStr: String,
        refuelType: String,
        station: String?,
        notes: String?
    ) {
        val odo = odometerStr.toFloatOrNull() ?: 0f
        val qty = quantityStr.toFloatOrNull() ?: 0f
        val price = priceStr.toFloatOrNull() ?: 0f
        val total = qty * price

        if (odo <= 0f || qty <= 0f || price <= 0f) {
            _uiState.value = FuelUiState.Error("Please enter valid odometer, quantity, and price.")
            return
        }

        val entry = FuelEntry(
            fuelId = fuelId,
            bikeId = bikeId,
            date = date,
            odometer = odo,
            fuelQuantity = qty,
            pricePerLiter = price,
            totalCost = total,
            refuelType = refuelType,
            fuelStation = station?.ifBlank { null },
            notes = notes?.ifBlank { null }
        )

        viewModelScope.launch {
            try {
                if (fuelId == 0L) {
                    fuelRepository.insertFuelEntry(entry)
                } else {
                    fuelRepository.updateFuelEntry(entry)
                }
                _uiState.value = FuelUiState.Saved
            } catch (e: Exception) {
                _uiState.value = FuelUiState.Error(e.localizedMessage ?: "Failed to save fuel entry")
            }
        }
    }

    fun deleteEntry(entry: FuelEntry) {
        viewModelScope.launch {
            fuelRepository.deleteFuelEntry(entry)
        }
    }

    class Factory(
        private val repository: FuelRepository,
        private val bikeId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FuelViewModel(repository, bikeId) as T
        }
    }
}
