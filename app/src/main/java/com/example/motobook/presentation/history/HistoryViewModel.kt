package com.example.motobook.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.motobook.domain.model.*
import com.example.motobook.domain.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UnifiedHistoryItem(val dateMillis: Long) {
    data class Fuel(val entry: FuelEntry) : UnifiedHistoryItem(entry.date)
    data class Service(val entry: ServiceEntry) : UnifiedHistoryItem(entry.date)
    data class Tyre(val entry: TyrePressureEntry) : UnifiedHistoryItem(entry.date)
    data class Wash(val entry: WashEntry) : UnifiedHistoryItem(entry.date)
    data class Chain(val entry: ChainEntry) : UnifiedHistoryItem(entry.date)
}

enum class HistoryCategory { ALL, FUEL, SERVICE, TYRE, WASH, CHAIN }

class HistoryViewModel(
    private val fuelRepository: FuelRepository,
    private val serviceRepository: ServiceRepository,
    private val tyreRepository: TyrePressureRepository,
    private val washRepository: WashRepository,
    private val chainRepository: ChainRepository,
    val bikeId: Long
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(HistoryCategory.ALL)
    val selectedCategory: StateFlow<HistoryCategory> = _selectedCategory.asStateFlow()

    val historyItems: StateFlow<List<UnifiedHistoryItem>> = combine(
        fuelRepository.getFuelEntriesByBike(bikeId),
        serviceRepository.getServiceEntriesByBike(bikeId),
        tyreRepository.getTyrePressureEntriesByBike(bikeId),
        washRepository.getWashEntriesByBike(bikeId),
        chainRepository.getChainEntriesByBike(bikeId)
    ) { fuels, services, tyres, washes, chains ->
        val list = mutableListOf<UnifiedHistoryItem>()
        list.addAll(fuels.map { UnifiedHistoryItem.Fuel(it) })
        list.addAll(services.map { UnifiedHistoryItem.Service(it) })
        list.addAll(tyres.map { UnifiedHistoryItem.Tyre(it) })
        list.addAll(washes.map { UnifiedHistoryItem.Wash(it) })
        list.addAll(chains.map { UnifiedHistoryItem.Chain(it) })
        list.sortedByDescending { it.dateMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredItems: StateFlow<List<UnifiedHistoryItem>> = combine(
        historyItems,
        selectedCategory
    ) { items, cat ->
        when (cat) {
            HistoryCategory.ALL -> items
            HistoryCategory.FUEL -> items.filterIsInstance<UnifiedHistoryItem.Fuel>()
            HistoryCategory.SERVICE -> items.filterIsInstance<UnifiedHistoryItem.Service>()
            HistoryCategory.TYRE -> items.filterIsInstance<UnifiedHistoryItem.Tyre>()
            HistoryCategory.WASH -> items.filterIsInstance<UnifiedHistoryItem.Wash>()
            HistoryCategory.CHAIN -> items.filterIsInstance<UnifiedHistoryItem.Chain>()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategory(category: HistoryCategory) {
        _selectedCategory.value = category
    }

    fun deleteItem(item: UnifiedHistoryItem) {
        viewModelScope.launch {
            when (item) {
                is UnifiedHistoryItem.Fuel -> fuelRepository.deleteFuelEntry(item.entry)
                is UnifiedHistoryItem.Service -> serviceRepository.deleteServiceEntry(item.entry)
                is UnifiedHistoryItem.Tyre -> tyreRepository.deleteTyrePressureEntry(item.entry)
                is UnifiedHistoryItem.Wash -> washRepository.deleteWashEntry(item.entry)
                is UnifiedHistoryItem.Chain -> chainRepository.deleteChainEntry(item.entry)
            }
        }
    }

    class Factory(
        private val fuelRepository: FuelRepository,
        private val serviceRepository: ServiceRepository,
        private val tyreRepository: TyrePressureRepository,
        private val washRepository: WashRepository,
        private val chainRepository: ChainRepository,
        private val bikeId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HistoryViewModel(
                fuelRepository,
                serviceRepository,
                tyreRepository,
                washRepository,
                chainRepository,
                bikeId
            ) as T
        }
    }
}
