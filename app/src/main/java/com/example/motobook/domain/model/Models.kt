package com.example.motobook.domain.model

data class Bike(
    val bikeId: Long = 0,
    val bikeName: String,
    val brand: String,
    val model: String,
    val year: Int = 2023,
    val registrationNumber: String = "",
    val fuelType: String = "Octane",
    val tankCapacity: Float = 12.0f,
    val reserveCapacity: Float = 2.0f,
    val frontTyrePressure: Float = 28.0f,
    val rearTyrePressure: Float = 32.0f,
    val bikeImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class FuelEntry(
    val fuelId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val odometer: Float,
    val fuelQuantity: Float,
    val pricePerLiter: Float,
    val totalCost: Float,
    val refuelType: String = "FULL", // "FULL" or "PARTIAL"
    val fuelStation: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class ServiceEntry(
    val serviceId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val odometer: Float,
    val category: String = "REGULAR_SERVICE",
    val itemsServiced: List<String> = emptyList(),
    val isOfficialServiceCenter: Boolean = true,
    val serviceCenterName: String? = null,
    val totalCost: Float = 0.0f,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class TyrePressureEntry(
    val tyrePressureId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val frontPsi: Float,
    val rearPsi: Float,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class WashEntry(
    val washId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val washType: String = "SELF_WASH", // "SELF_WASH" or "PROFESSIONAL"
    val cost: Float? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class ChainEntry(
    val chainId: Long = 0,
    val bikeId: Long,
    val date: Long,
    val odometer: Float? = null,
    val lubricantType: String? = "Chain Lube Spray",
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class MileageCycle(
    val cycleNumber: Int,
    val startEntry: FuelEntry,
    val endEntry: FuelEntry,
    val intermediateEntries: List<FuelEntry>,
    val distanceKm: Float,
    val fuelUsedLiters: Float,
    val mileageKmPerLiter: Float,
    val cycleStartDate: Long,
    val cycleEndDate: Long
)

data class MileageStats(
    val completedCycles: List<MileageCycle> = emptyList(),
    val currentMileage: Float? = null,
    val averageMileage: Float? = null,
    val bestMileage: Float? = null,
    val worstMileage: Float? = null,
    val lastFiveCycleAverage: Float? = null,
    val totalDistanceCovered: Float = 0.0f,
    val totalFuelConsumed: Float = 0.0f,
    val totalCycles: Int = 0,
    val costPerKm: Float? = null,
    val currentCyclePartialFuel: Float = 0.0f
)

data class MaintenanceReminder(
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

data class MonthlyExpenditureSummary(
    val yearMonthKey: String, // e.g. "2026-07"
    val displayMonthName: String, // e.g. "July 2026"
    val totalFuelCost: Float,
    val totalServiceCost: Float,
    val totalWashCost: Float,
    val totalOverallCost: Float
)
