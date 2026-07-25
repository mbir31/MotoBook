package com.example.motobook.data.repository

import com.example.motobook.data.local.dao.*
import com.example.motobook.data.mapper.*
import com.example.motobook.domain.model.*
import com.example.motobook.domain.repository.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BikeRepositoryImpl(
    private val bikeDao: BikeDao,
    private val onDataChanged: (suspend () -> Unit)? = null
) : BikeRepository {
    override fun getAllBikes(): Flow<List<Bike>> =
        bikeDao.getAllBikes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getBikeById(id: Long): Bike? =
        bikeDao.getBikeById(id)?.toDomain()

    override suspend fun insertBike(bike: Bike): Long {
        val result = bikeDao.insertBike(bike.toEntity())
        onDataChanged?.invoke()
        return result
    }

    override suspend fun updateBike(bike: Bike) {
        bikeDao.updateBike(bike.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun deleteBike(bike: Bike) {
        bikeDao.deleteBike(bike.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun getBikeCount(): Int =
        bikeDao.getBikeCount()
}

class FuelRepositoryImpl(
    private val fuelDao: FuelDao,
    private val onDataChanged: (suspend () -> Unit)? = null
) : FuelRepository {
    override fun getFuelEntriesByBike(bikeId: Long): Flow<List<FuelEntry>> =
        fuelDao.getFuelEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override fun getFuelEntriesAscendingFlow(bikeId: Long): Flow<List<FuelEntry>> =
        fuelDao.getFuelEntriesAscendingFlow(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getFuelEntriesAscending(bikeId: Long): List<FuelEntry> =
        fuelDao.getFuelEntriesAscending(bikeId).map { it.toDomain() }

    override suspend fun getFuelEntryById(id: Long): FuelEntry? =
        fuelDao.getFuelEntryById(id)?.toDomain()

    override suspend fun insertFuelEntry(entry: FuelEntry): Long {
        val id = fuelDao.insertFuelEntry(entry.toEntity())
        onDataChanged?.invoke()
        return id
    }

    override suspend fun updateFuelEntry(entry: FuelEntry) {
        fuelDao.updateFuelEntry(entry.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun deleteFuelEntry(entry: FuelEntry) {
        fuelDao.deleteFuelEntry(entry.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun getTotalFuelCost(bikeId: Long): Float? =
        fuelDao.getTotalFuelCost(bikeId)

    override suspend fun getTotalFuelQuantity(bikeId: Long): Float? =
        fuelDao.getTotalFuelQuantity(bikeId)

    override suspend fun getLastFuelEntry(bikeId: Long): FuelEntry? =
        fuelDao.getLastFuelEntry(bikeId)?.toDomain()
}

class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao,
    private val onDataChanged: (suspend () -> Unit)? = null
) : ServiceRepository {
    override fun getServiceEntriesByBike(bikeId: Long): Flow<List<ServiceEntry>> =
        serviceDao.getServiceEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getServiceEntriesByBikeSync(bikeId: Long): List<ServiceEntry> =
        serviceDao.getServiceEntriesByBikeSync(bikeId).map { it.toDomain() }

    override suspend fun getLastServiceEntry(bikeId: Long): ServiceEntry? =
        serviceDao.getLastServiceEntry(bikeId)?.toDomain()

    override suspend fun insertServiceEntry(entry: ServiceEntry): Long {
        val id = serviceDao.insertServiceEntry(entry.toEntity())
        onDataChanged?.invoke()
        return id
    }

    override suspend fun updateServiceEntry(entry: ServiceEntry) {
        serviceDao.updateServiceEntry(entry.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun deleteServiceEntry(entry: ServiceEntry) {
        serviceDao.deleteServiceEntry(entry.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun getTotalServiceCost(bikeId: Long): Float? =
        serviceDao.getTotalServiceCost(bikeId)
}

class TyrePressureRepositoryImpl(
    private val tyreDao: TyrePressureDao,
    private val onDataChanged: (suspend () -> Unit)? = null
) : TyrePressureRepository {
    override fun getTyrePressureEntriesByBike(bikeId: Long): Flow<List<TyrePressureEntry>> =
        tyreDao.getTyrePressureEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getTyrePressureEntriesSync(bikeId: Long): List<TyrePressureEntry> =
        tyreDao.getTyrePressureEntriesSync(bikeId).map { it.toDomain() }

    override suspend fun getLastTyrePressureEntry(bikeId: Long): TyrePressureEntry? =
        tyreDao.getLastTyrePressureEntry(bikeId)?.toDomain()

    override suspend fun insertTyrePressureEntry(entry: TyrePressureEntry): Long {
        val id = tyreDao.insertTyrePressureEntry(entry.toEntity())
        onDataChanged?.invoke()
        return id
    }

    override suspend fun updateTyrePressureEntry(entry: TyrePressureEntry) {
        tyreDao.updateTyrePressureEntry(entry.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun deleteTyrePressureEntry(entry: TyrePressureEntry) {
        tyreDao.deleteTyrePressureEntry(entry.toEntity())
        onDataChanged?.invoke()
    }
}

class WashRepositoryImpl(
    private val washDao: WashDao,
    private val onDataChanged: (suspend () -> Unit)? = null
) : WashRepository {
    override fun getWashEntriesByBike(bikeId: Long): Flow<List<WashEntry>> =
        washDao.getWashEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getWashEntriesSync(bikeId: Long): List<WashEntry> =
        washDao.getWashEntriesSync(bikeId).map { it.toDomain() }

    override suspend fun getLastWashEntry(bikeId: Long): WashEntry? =
        washDao.getLastWashEntry(bikeId)?.toDomain()

    override suspend fun insertWashEntry(entry: WashEntry): Long {
        val id = washDao.insertWashEntry(entry.toEntity())
        onDataChanged?.invoke()
        return id
    }

    override suspend fun updateWashEntry(entry: WashEntry) {
        washDao.updateWashEntry(entry.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun deleteWashEntry(entry: WashEntry) {
        washDao.deleteWashEntry(entry.toEntity())
        onDataChanged?.invoke()
    }
}

class ChainRepositoryImpl(
    private val chainDao: ChainDao,
    private val onDataChanged: (suspend () -> Unit)? = null
) : ChainRepository {
    override fun getChainEntriesByBike(bikeId: Long): Flow<List<ChainEntry>> =
        chainDao.getChainEntriesByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getChainEntriesSync(bikeId: Long): List<ChainEntry> =
        chainDao.getChainEntriesSync(bikeId).map { it.toDomain() }

    override suspend fun getLastChainEntry(bikeId: Long): ChainEntry? =
        chainDao.getLastChainEntry(bikeId)?.toDomain()

    override suspend fun insertChainEntry(entry: ChainEntry): Long {
        val id = chainDao.insertChainEntry(entry.toEntity())
        onDataChanged?.invoke()
        return id
    }

    override suspend fun updateChainEntry(entry: ChainEntry) {
        chainDao.updateChainEntry(entry.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun deleteChainEntry(entry: ChainEntry) {
        chainDao.deleteChainEntry(entry.toEntity())
        onDataChanged?.invoke()
    }
}

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao,
    private val onDataChanged: (suspend () -> Unit)? = null
) : ReminderRepository {
    override fun getRemindersByBike(bikeId: Long): Flow<List<MaintenanceReminder>> =
        reminderDao.getRemindersByBike(bikeId).map { list -> list.map { it.toDomain() } }

    override suspend fun getRemindersByBikeSync(bikeId: Long): List<MaintenanceReminder> =
        reminderDao.getRemindersByBikeSync(bikeId).map { it.toDomain() }

    override suspend fun insertReminder(reminder: MaintenanceReminder): Long {
        val id = reminderDao.insertReminder(reminder.toEntity())
        onDataChanged?.invoke()
        return id
    }

    override suspend fun updateReminder(reminder: MaintenanceReminder) {
        reminderDao.updateReminder(reminder.toEntity())
        onDataChanged?.invoke()
    }

    override suspend fun deleteReminder(reminder: MaintenanceReminder) {
        reminderDao.deleteReminder(reminder.toEntity())
        onDataChanged?.invoke()
    }
}


class BackupRepositoryImpl(
    private val database: com.example.motobook.data.local.database.MotoBookDatabase
) : BackupRepository {

    override suspend fun createBackupJson(): String {
        return com.example.motobook.data.backup.AutoBackupManager.generateBackupJson(database)
    }

    override suspend fun restoreBackupJson(jsonString: String): Boolean {
        return com.example.motobook.data.backup.AutoBackupManager.restoreFromBackupJson(database, jsonString)
    }
}
