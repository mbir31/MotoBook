package com.example.motobook.data.local.dao

import androidx.room.*
import com.example.motobook.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeDao {
    @Query("SELECT * FROM bikes ORDER BY createdAt DESC")
    fun getAllBikes(): Flow<List<BikeEntity>>

    @Query("SELECT * FROM bikes ORDER BY createdAt DESC")
    suspend fun getAllBikesSync(): List<BikeEntity>

    @Query("SELECT * FROM bikes WHERE bikeId = :id")
    suspend fun getBikeById(id: Long): BikeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBike(bike: BikeEntity): Long

    @Update
    suspend fun updateBike(bike: BikeEntity)

    @Delete
    suspend fun deleteBike(bike: BikeEntity)

    @Query("SELECT COUNT(*) FROM bikes")
    suspend fun getBikeCount(): Int
}

@Dao
interface FuelDao {
    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getFuelEntriesByBike(bikeId: Long): Flow<List<FuelEntity>>

    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    suspend fun getFuelEntriesByBikeSync(bikeId: Long): List<FuelEntity>

    @Query("SELECT * FROM fuel_entries ORDER BY date DESC")
    suspend fun getAllFuelEntriesSync(): List<FuelEntity>

    @Query("SELECT * FROM fuel_entries WHERE fuelId = :id")
    suspend fun getFuelEntryById(id: Long): FuelEntity?

    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId ORDER BY date ASC, odometer ASC")
    fun getFuelEntriesAscendingFlow(bikeId: Long): Flow<List<FuelEntity>>

    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId ORDER BY date ASC, odometer ASC")
    suspend fun getFuelEntriesAscending(bikeId: Long): List<FuelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelEntry(entry: FuelEntity): Long

    @Update
    suspend fun updateFuelEntry(entry: FuelEntity)

    @Delete
    suspend fun deleteFuelEntry(entry: FuelEntity)

    @Query("SELECT SUM(totalCost) FROM fuel_entries WHERE bikeId = :bikeId")
    suspend fun getTotalFuelCost(bikeId: Long): Float?

    @Query("SELECT SUM(fuelQuantity) FROM fuel_entries WHERE bikeId = :bikeId")
    suspend fun getTotalFuelQuantity(bikeId: Long): Float?

    @Query("SELECT * FROM fuel_entries WHERE bikeId = :bikeId ORDER BY date DESC LIMIT 1")
    suspend fun getLastFuelEntry(bikeId: Long): FuelEntity?
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM service_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getServiceEntriesByBike(bikeId: Long): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM service_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    suspend fun getServiceEntriesByBikeSync(bikeId: Long): List<ServiceEntity>

    @Query("SELECT * FROM service_entries ORDER BY date DESC")
    suspend fun getAllServicesSync(): List<ServiceEntity>

    @Query("SELECT * FROM service_entries WHERE bikeId = :bikeId ORDER BY date DESC LIMIT 1")
    suspend fun getLastServiceEntry(bikeId: Long): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceEntry(entry: ServiceEntity): Long

    @Update
    suspend fun updateServiceEntry(entry: ServiceEntity)

    @Delete
    suspend fun deleteServiceEntry(entry: ServiceEntity)

    @Query("SELECT SUM(totalCost) FROM service_entries WHERE bikeId = :bikeId")
    suspend fun getTotalServiceCost(bikeId: Long): Float?
}

@Dao
interface TyrePressureDao {
    @Query("SELECT * FROM tyre_pressure_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getTyrePressureEntriesByBike(bikeId: Long): Flow<List<TyrePressureEntity>>

    @Query("SELECT * FROM tyre_pressure_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    suspend fun getTyrePressureEntriesSync(bikeId: Long): List<TyrePressureEntity>

    @Query("SELECT * FROM tyre_pressure_entries ORDER BY date DESC")
    suspend fun getAllTyresSync(): List<TyrePressureEntity>

    @Query("SELECT * FROM tyre_pressure_entries WHERE bikeId = :bikeId ORDER BY date DESC LIMIT 1")
    suspend fun getLastTyrePressureEntry(bikeId: Long): TyrePressureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTyrePressureEntry(entry: TyrePressureEntity): Long

    @Update
    suspend fun updateTyrePressureEntry(entry: TyrePressureEntity)

    @Delete
    suspend fun deleteTyrePressureEntry(entry: TyrePressureEntity)
}

@Dao
interface WashDao {
    @Query("SELECT * FROM wash_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getWashEntriesByBike(bikeId: Long): Flow<List<WashEntity>>

    @Query("SELECT * FROM wash_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    suspend fun getWashEntriesSync(bikeId: Long): List<WashEntity>

    @Query("SELECT * FROM wash_entries ORDER BY date DESC")
    suspend fun getAllWashesSync(): List<WashEntity>

    @Query("SELECT * FROM wash_entries WHERE bikeId = :bikeId ORDER BY date DESC LIMIT 1")
    suspend fun getLastWashEntry(bikeId: Long): WashEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWashEntry(entry: WashEntity): Long

    @Update
    suspend fun updateWashEntry(entry: WashEntity)

    @Delete
    suspend fun deleteWashEntry(entry: WashEntity)
}

@Dao
interface ChainDao {
    @Query("SELECT * FROM chain_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    fun getChainEntriesByBike(bikeId: Long): Flow<List<ChainEntity>>

    @Query("SELECT * FROM chain_entries WHERE bikeId = :bikeId ORDER BY date DESC")
    suspend fun getChainEntriesSync(bikeId: Long): List<ChainEntity>

    @Query("SELECT * FROM chain_entries ORDER BY date DESC")
    suspend fun getAllChainsSync(): List<ChainEntity>

    @Query("SELECT * FROM chain_entries WHERE bikeId = :bikeId ORDER BY date DESC LIMIT 1")
    suspend fun getLastChainEntry(bikeId: Long): ChainEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChainEntry(entry: ChainEntity): Long

    @Update
    suspend fun updateChainEntry(entry: ChainEntity)

    @Delete
    suspend fun deleteChainEntry(entry: ChainEntity)
}


@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE bikeId = :bikeId ORDER BY isCompleted ASC, dueOdometer ASC, dueDate ASC")
    fun getRemindersByBike(bikeId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE bikeId = :bikeId ORDER BY isCompleted ASC, dueOdometer ASC, dueDate ASC")
    suspend fun getRemindersByBikeSync(bikeId: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders")
    suspend fun getAllRemindersSync(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
}
