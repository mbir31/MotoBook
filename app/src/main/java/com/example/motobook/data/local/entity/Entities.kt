package com.example.motobook.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "bikes")
data class BikeEntity(
    @PrimaryKey(autoGenerate = true)
    val bikeId: Long = 0,
    val bikeName: String,
    val brand: String,
    val model: String,
    val year: Int,
    val registrationNumber: String,
    val fuelType: String,
    val tankCapacity: Float,
    val reserveCapacity: Float,
    val frontTyrePressure: Float,
    val rearTyrePressure: Float,
    val bikeImagePath: String?,
    val color: String = "Black",
    val engineCc: Float = 150.0f,
    val maxPower: String = "",
    val recommendedOilGrade: String = "",
    val maintenanceScheduleNote: String = "",
    val countryOfOrigin: String = "Global",
    val manualUrl: String = "",
    val manualSummary: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "fuel_entries",
    foreignKeys = [ForeignKey(
        entity = BikeEntity::class,
        parentColumns = ["bikeId"],
        childColumns = ["bikeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bikeId"])]
)
data class FuelEntity(
    @PrimaryKey(autoGenerate = true)
    val fuelId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val odometer: Float,
    val fuelQuantity: Float,
    val pricePerLiter: Float,
    val totalCost: Float,
    val refuelType: String,
    val fuelStation: String?,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "service_entries",
    foreignKeys = [ForeignKey(
        entity = BikeEntity::class,
        parentColumns = ["bikeId"],
        childColumns = ["bikeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bikeId"])]
)
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val serviceId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val odometer: Float,
    val category: String,
    val itemsServicedJson: String, // JSON string list of items
    val isOfficialServiceCenter: Boolean,
    val serviceCenterName: String?,
    val totalCost: Float,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "tyre_pressure_entries",
    foreignKeys = [ForeignKey(
        entity = BikeEntity::class,
        parentColumns = ["bikeId"],
        childColumns = ["bikeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bikeId"])]
)
data class TyrePressureEntity(
    @PrimaryKey(autoGenerate = true)
    val tyrePressureId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val frontPsi: Float,
    val rearPsi: Float,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "wash_entries",
    foreignKeys = [ForeignKey(
        entity = BikeEntity::class,
        parentColumns = ["bikeId"],
        childColumns = ["bikeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bikeId"])]
)
data class WashEntity(
    @PrimaryKey(autoGenerate = true)
    val washId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val washType: String,
    val cost: Float?,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chain_entries",
    foreignKeys = [ForeignKey(
        entity = BikeEntity::class,
        parentColumns = ["bikeId"],
        childColumns = ["bikeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bikeId"])]
)
data class ChainEntity(
    @PrimaryKey(autoGenerate = true)
    val chainId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val odometer: Float?,
    val lubricantType: String?,
    val notes: String?,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reminders",
    foreignKeys = [ForeignKey(
        entity = BikeEntity::class,
        parentColumns = ["bikeId"],
        childColumns = ["bikeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["bikeId"])]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val reminderId: Long = 0,
    val bikeId: Long,
    val title: String,
    val dueOdometer: Float? = null,
    val dueDate: Long? = null,
    val intervalKm: Float? = null,
    val lastDoneOdometer: Float? = null,
    val lastDoneDate: Long? = null,
    val isCompleted: Boolean = false,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
