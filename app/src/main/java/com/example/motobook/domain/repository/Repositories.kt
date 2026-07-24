package com.example.motobook.domain.repository

import com.example.motobook.domain.model.*
import kotlinx.coroutines.flow.Flow

interface BikeRepository {
    fun getAllBikes(): Flow<List<Bike>>
    suspend fun getBikeById(id: Long): Bike?
    suspend fun insertBike(bike: Bike): Long
    suspend fun updateBike(bike: Bike)
    suspend fun deleteBike(bike: Bike)
    suspend fun getBikeCount(): Int
}

interface FuelRepository {
    fun getFuelEntriesByBike(bikeId: Long): Flow<List<FuelEntry>>
    fun getFuelEntriesAscendingFlow(bikeId: Long): Flow<List<FuelEntry>>
    suspend fun getFuelEntriesAscending(bikeId: Long): List<FuelEntry>
    suspend fun getFuelEntryById(id: Long): FuelEntry?
    suspend fun insertFuelEntry(entry: FuelEntry): Long
    suspend fun updateFuelEntry(entry: FuelEntry)
    suspend fun deleteFuelEntry(entry: FuelEntry)
    suspend fun getTotalFuelCost(bikeId: Long): Float?
    suspend fun getTotalFuelQuantity(bikeId: Long): Float?
    suspend fun getLastFuelEntry(bikeId: Long): FuelEntry?
}

interface ServiceRepository {
    fun getServiceEntriesByBike(bikeId: Long): Flow<List<ServiceEntry>>
    suspend fun getServiceEntriesByBikeSync(bikeId: Long): List<ServiceEntry>
    suspend fun getLastServiceEntry(bikeId: Long): ServiceEntry?
    suspend fun insertServiceEntry(entry: ServiceEntry): Long
    suspend fun updateServiceEntry(entry: ServiceEntry)
    suspend fun deleteServiceEntry(entry: ServiceEntry)
    suspend fun getTotalServiceCost(bikeId: Long): Float?
}

interface TyrePressureRepository {
    fun getTyrePressureEntriesByBike(bikeId: Long): Flow<List<TyrePressureEntry>>
    suspend fun getTyrePressureEntriesSync(bikeId: Long): List<TyrePressureEntry>
    suspend fun getLastTyrePressureEntry(bikeId: Long): TyrePressureEntry?
    suspend fun insertTyrePressureEntry(entry: TyrePressureEntry): Long
    suspend fun updateTyrePressureEntry(entry: TyrePressureEntry)
    suspend fun deleteTyrePressureEntry(entry: TyrePressureEntry)
}

interface WashRepository {
    fun getWashEntriesByBike(bikeId: Long): Flow<List<WashEntry>>
    suspend fun getWashEntriesSync(bikeId: Long): List<WashEntry>
    suspend fun getLastWashEntry(bikeId: Long): WashEntry?
    suspend fun insertWashEntry(entry: WashEntry): Long
    suspend fun updateWashEntry(entry: WashEntry)
    suspend fun deleteWashEntry(entry: WashEntry)
}

interface ChainRepository {
    fun getChainEntriesByBike(bikeId: Long): Flow<List<ChainEntry>>
    suspend fun getChainEntriesSync(bikeId: Long): List<ChainEntry>
    suspend fun getLastChainEntry(bikeId: Long): ChainEntry?
    suspend fun insertChainEntry(entry: ChainEntry): Long
    suspend fun updateChainEntry(entry: ChainEntry)
    suspend fun deleteChainEntry(entry: ChainEntry)
}

interface BackupRepository {
    suspend fun createBackupJson(): String
    suspend fun restoreBackupJson(jsonString: String): Boolean
}
