package com.example.motobook.presentation.bike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.domain.model.Bike
import com.example.motobook.domain.repository.BikeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class BikeUiState {
    object Idle : BikeUiState()
    object Loading : BikeUiState()
    data class Success(val bike: Bike) : BikeUiState()
    data class Error(val message: String) : BikeUiState()
    object Saved : BikeUiState()
}

class BikeViewModel(
    private val bikeRepository: BikeRepository
) : ViewModel() {

    val allBikes: StateFlow<List<Bike>> = bikeRepository.getAllBikes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<BikeUiState>(BikeUiState.Idle)
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    fun saveBike(
        bikeId: Long = 0,
        bikeName: String,
        brand: String,
        model: String,
        yearStr: String,
        registrationNumber: String,
        fuelType: String,
        tankCapacityStr: String,
        reserveCapacityStr: String,
        frontPsiStr: String,
        rearPsiStr: String,
        color: String = "Black",
        engineCcStr: String = "150",
        maxPower: String = "",
        recommendedOilGrade: String = "",
        maintenanceScheduleNote: String = "",
        countryOfOrigin: String = "Global",
        manualUrl: String = "",
        manualSummary: String = "",
        bikeImagePath: String? = null
    ) {
        val tankCap = tankCapacityStr.toFloatOrNull() ?: 12f
        val reserveCap = reserveCapacityStr.toFloatOrNull() ?: 2f
        val frontPsi = frontPsiStr.toFloatOrNull() ?: 28f
        val rearPsi = rearPsiStr.toFloatOrNull() ?: 32f
        val year = yearStr.toIntOrNull() ?: 2023
        val engineCc = engineCcStr.toFloatOrNull() ?: 150f

        val bike = Bike(
            bikeId = bikeId,
            bikeName = bikeName.ifBlank { "My Motorcycle" },
            brand = brand.ifBlank { "Honda" },
            model = model.ifBlank { "Standard" },
            year = year,
            registrationNumber = registrationNumber,
            fuelType = fuelType,
            tankCapacity = tankCap,
            reserveCapacity = reserveCap,
            frontTyrePressure = frontPsi,
            rearTyrePressure = rearPsi,
            color = color.ifBlank { "Black" },
            engineCc = engineCc,
            maxPower = maxPower,
            recommendedOilGrade = recommendedOilGrade,
            maintenanceScheduleNote = maintenanceScheduleNote,
            countryOfOrigin = countryOfOrigin.ifBlank { "Global" },
            manualUrl = manualUrl,
            manualSummary = manualSummary,
            bikeImagePath = bikeImagePath
        )

        viewModelScope.launch {
            _uiState.value = BikeUiState.Loading
            try {
                if (bikeId == 0L) {
                    bikeRepository.insertBike(bike)
                } else {
                    bikeRepository.updateBike(bike)
                }
                _uiState.value = BikeUiState.Saved
            } catch (e: Exception) {
                _uiState.value = BikeUiState.Error(e.localizedMessage ?: "Failed to save motorcycle")
            }
        }
    }

    fun deleteBike(bike: Bike) {
        viewModelScope.launch {
            bikeRepository.deleteBike(bike)
        }
    }

    class Factory(private val repository: BikeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BikeViewModel(repository) as T
        }
    }
}
