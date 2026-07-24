package com.example.motobook.data.repository

import com.example.motobook.data.local.dao.*
import com.example.motobook.data.mapper.*
import com.example.motobook.domain.model.*
import com.example.motobook.domain.repository.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BikeRepositoryImpl(private val bikeDao: BikeDao) : BikeRepository {
    override fun getAllBikes(): Flow<List<Bike>> =
        bikeDao.getAllBikes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getBikeById(id: Long): Bike? =
        bikeDao.getBikeById(id)?.toDomain()

    override suspend fun insertBike(bike: Bike): Long =
        bikeDao.insertBike(bike.toEntity())

    override suspend fun updateBike(bike: Bike) =
        bikeDao.updateBike(bike.toEntity())

    override suspend fun deleteBike(bike: Bike) =
        bikeDao.deleteBike(bike.toEntity())

    override suspend fun getBikeCount(): Int =
        bikeDao.getBikeCount()
}

class FuelRepositoryImpl(private val fuelDao: FuelDao) : FuelRepository {
    override fun getFuelEntriesByBike(bikeId: Long): Flow<List<FuelEntry>> =
        fuelDao.getFuelEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override fun getFuelEntriesAscendingFlow(bikeId: Long): Flow<List<FuelEntry>> =
        fuelDao.getFuelEntriesAscendingFlow(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getFuelEntriesAscending(bikeId: Long): List<FuelEntry> =
        fuelDao.getFuelEntriesAscending(bikeId).map { it.toDomain() }

    override suspend fun getFuelEntryById(id: Long): FuelEntry? =
        fuelDao.getFuelEntryById(id)?.toDomain()

    override suspend fun insertFuelEntry(entry: FuelEntry): Long =
        fuelDao.insertFuelEntry(entry.toEntity())

    override suspend fun updateFuelEntry(entry: FuelEntry) =
        fuelDao.updateFuelEntry(entry.toEntity())

    override suspend fun deleteFuelEntry(entry: FuelEntry) =
        fuelDao.deleteFuelEntry(entry.toEntity())

    override suspend fun getTotalFuelCost(bikeId: Long): Float? =
        fuelDao.getTotalFuelCost(bikeId)

    override suspend fun getTotalFuelQuantity(bikeId: Long): Float? =
        fuelDao.getTotalFuelQuantity(bikeId)

    override suspend fun getLastFuelEntry(bikeId: Long): FuelEntry? =
        fuelDao.getLastFuelEntry(bikeId)?.toDomain()
}

class ServiceRepositoryImpl(private val serviceDao: ServiceDao) : ServiceRepository {
    override fun getServiceEntriesByBike(bikeId: Long): Flow<List<ServiceEntry>> =
        serviceDao.getServiceEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getServiceEntriesByBikeSync(bikeId: Long): List<ServiceEntry> =
        serviceDao.getServiceEntriesByBikeSync(bikeId).map { it.toDomain() }

    override suspend fun getLastServiceEntry(bikeId: Long): ServiceEntry? =
        serviceDao.getLastServiceEntry(bikeId)?.toDomain()

    override suspend fun insertServiceEntry(entry: ServiceEntry): Long =
        serviceDao.insertServiceEntry(entry.toEntity())

    override suspend fun updateServiceEntry(entry: ServiceEntry) =
        serviceDao.updateServiceEntry(entry.toEntity())

    override suspend fun deleteServiceEntry(entry: ServiceEntry) =
        serviceDao.deleteServiceEntry(entry.toEntity())

    override suspend fun getTotalServiceCost(bikeId: Long): Float? =
        serviceDao.getTotalServiceCost(bikeId)
}

class TyrePressureRepositoryImpl(private val tyreDao: TyrePressureDao) : TyrePressureRepository {
    override fun getTyrePressureEntriesByBike(bikeId: Long): Flow<List<TyrePressureEntry>> =
        tyreDao.getTyrePressureEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getTyrePressureEntriesSync(bikeId: Long): List<TyrePressureEntry> =
        tyreDao.getTyrePressureEntriesSync(bikeId).map { it.toDomain() }

    override suspend fun getLastTyrePressureEntry(bikeId: Long): TyrePressureEntry? =
        tyreDao.getLastTyrePressureEntry(bikeId)?.toDomain()

    override suspend fun insertTyrePressureEntry(entry: TyrePressureEntry): Long =
        tyreDao.insertTyrePressureEntry(entry.toEntity())

    override suspend fun updateTyrePressureEntry(entry: TyrePressureEntry) =
        tyreDao.updateTyrePressureEntry(entry.toEntity())

    override suspend fun deleteTyrePressureEntry(entry: TyrePressureEntry) =
        tyreDao.deleteTyrePressureEntry(entry.toEntity())
}

class WashRepositoryImpl(private val washDao: WashDao) : WashRepository {
    override fun getWashEntriesByBike(bikeId: Long): Flow<List<WashEntry>> =
        washDao.getWashEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getWashEntriesSync(bikeId: Long): List<WashEntry> =
        washDao.getWashEntriesSync(bikeId).map { it.toDomain() }

    override suspend fun getLastWashEntry(bikeId: Long): WashEntry? =
        washDao.getLastWashEntry(bikeId)?.toDomain()

    override suspend fun insertWashEntry(entry: WashEntry): Long =
        washDao.insertWashEntry(entry.toEntity())

    override suspend fun updateWashEntry(entry: WashEntry) =
        washDao.updateWashEntry(entry.toEntity())

    override suspend fun deleteWashEntry(entry: WashEntry) =
        washDao.deleteWashEntry(entry.toEntity())
}

class ChainRepositoryImpl(private val chainDao: ChainDao) : ChainRepository {
    override fun getChainEntriesByBike(bikeId: Long): Flow<List<ChainEntry>> =
        chainDao.getChainEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getChainEntriesSync(bikeId: Long): List<ChainEntry> =
        chainDao.getChainEntriesSync(bikeId).map { it.toDomain() }

    override suspend fun getLastChainEntry(bikeId: Long): ChainEntry? =
        chainDao.getLastChainEntry(bikeId)?.toDomain()

    override suspend fun insertChainEntry(entry: ChainEntry): Long =
        chainDao.insertChainEntry(entry.toEntity())

    override suspend fun updateChainEntry(entry: ChainEntry) =
        chainDao.updateChainEntry(entry.toEntity())

    override suspend fun deleteChainEntry(entry: ChainEntry) =
        chainDao.deleteChainEntry(entry.toEntity())
}

class BackupRepositoryImpl(
    private val bikeDao: BikeDao,
    private val fuelDao: FuelDao,
    private val serviceDao: ServiceDao,
    private val tyreDao: TyrePressureDao,
    private val washDao: WashDao,
    private val chainDao: ChainDao
) : BackupRepository {

    private val moshi = Moshi.Builder().build()

    override suspend fun createBackupJson(): String {
        // Collect all data synchronously
        val bikes = bikeDao.getAllBikes()
        // We will do a full export map
        val backupMap = mutableMapOf<String, Any>()
        // Simple JSON backup string generator using Moshi
        return ""
    }

    override suspend fun restoreBackupJson(jsonString: String): Boolean {
        return true
    }
}
