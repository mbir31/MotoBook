package com.example.motobook.data.mapper

import com.example.motobook.data.local.entity.*
import com.example.motobook.domain.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

private val moshi = Moshi.Builder().build()
private val stringListAdapter = moshi.adapter<List<String>>(
    Types.newParameterizedType(List::class.java, String::class.java)
)

fun BikeEntity.toDomain(): Bike = Bike(
    bikeId = bikeId,
    bikeName = bikeName,
    brand = brand,
    model = model,
    year = year,
    registrationNumber = registrationNumber,
    fuelType = fuelType,
    tankCapacity = tankCapacity,
    reserveCapacity = reserveCapacity,
    frontTyrePressure = frontTyrePressure,
    rearTyrePressure = rearTyrePressure,
    bikeImagePath = bikeImagePath,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Bike.toEntity(): BikeEntity = BikeEntity(
    bikeId = bikeId,
    bikeName = bikeName,
    brand = brand,
    model = model,
    year = year,
    registrationNumber = registrationNumber,
    fuelType = fuelType,
    tankCapacity = tankCapacity,
    reserveCapacity = reserveCapacity,
    frontTyrePressure = frontTyrePressure,
    rearTyrePressure = rearTyrePressure,
    bikeImagePath = bikeImagePath,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun FuelEntity.toDomain(): FuelEntry = FuelEntry(
    fuelId = fuelId,
    bikeId = bikeId,
    date = date,
    odometer = odometer,
    fuelQuantity = fuelQuantity,
    pricePerLiter = pricePerLiter,
    totalCost = totalCost,
    refuelType = refuelType,
    fuelStation = fuelStation,
    notes = notes,
    createdAt = createdAt
)

fun FuelEntry.toEntity(): FuelEntity = FuelEntity(
    fuelId = fuelId,
    bikeId = bikeId,
    date = date,
    odometer = odometer,
    fuelQuantity = fuelQuantity,
    pricePerLiter = pricePerLiter,
    totalCost = totalCost,
    refuelType = refuelType,
    fuelStation = fuelStation,
    notes = notes,
    createdAt = createdAt
)

fun ServiceEntity.toDomain(): ServiceEntry {
    val items = try {
        stringListAdapter.fromJson(itemsServicedJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    return ServiceEntry(
        serviceId = serviceId,
        bikeId = bikeId,
        date = date,
        odometer = odometer,
        category = category,
        itemsServiced = items,
        isOfficialServiceCenter = isOfficialServiceCenter,
        serviceCenterName = serviceCenterName,
        totalCost = totalCost,
        notes = notes,
        createdAt = createdAt
    )
}

fun ServiceEntry.toEntity(): ServiceEntity = ServiceEntity(
    serviceId = serviceId,
    bikeId = bikeId,
    date = date,
    odometer = odometer,
    category = category,
    itemsServicedJson = stringListAdapter.toJson(itemsServiced),
    isOfficialServiceCenter = isOfficialServiceCenter,
    serviceCenterName = serviceCenterName,
    totalCost = totalCost,
    notes = notes,
    createdAt = createdAt
)

fun TyrePressureEntity.toDomain(): TyrePressureEntry = TyrePressureEntry(
    tyrePressureId = tyrePressureId,
    bikeId = bikeId,
    date = date,
    frontPsi = frontPsi,
    rearPsi = rearPsi,
    notes = notes,
    createdAt = createdAt
)

fun TyrePressureEntry.toEntity(): TyrePressureEntity = TyrePressureEntity(
    tyrePressureId = tyrePressureId,
    bikeId = bikeId,
    date = date,
    frontPsi = frontPsi,
    rearPsi = rearPsi,
    notes = notes,
    createdAt = createdAt
)

fun WashEntity.toDomain(): WashEntry = WashEntry(
    washId = washId,
    bikeId = bikeId,
    date = date,
    washType = washType,
    cost = cost,
    notes = notes,
    createdAt = createdAt
)

fun WashEntry.toEntity(): WashEntity = WashEntity(
    washId = washId,
    bikeId = bikeId,
    date = date,
    washType = washType,
    cost = cost,
    notes = notes,
    createdAt = createdAt
)

fun ChainEntity.toDomain(): ChainEntry = ChainEntry(
    chainId = chainId,
    bikeId = bikeId,
    date = date,
    odometer = odometer,
    lubricantType = lubricantType,
    notes = notes,
    createdAt = createdAt
)

fun ChainEntry.toEntity(): ChainEntity = ChainEntity(
    chainId = chainId,
    bikeId = bikeId,
    date = date,
    odometer = odometer,
    lubricantType = lubricantType,
    notes = notes,
    createdAt = createdAt
)

fun ReminderEntity.toDomain(): MaintenanceReminder = MaintenanceReminder(
    reminderId = reminderId,
    bikeId = bikeId,
    title = title,
    dueOdometer = dueOdometer,
    dueDate = dueDate,
    intervalKm = intervalKm,
    lastDoneOdometer = lastDoneOdometer,
    lastDoneDate = lastDoneDate,
    isCompleted = isCompleted,
    notes = notes,
    createdAt = createdAt
)

fun MaintenanceReminder.toEntity(): ReminderEntity = ReminderEntity(
    reminderId = reminderId,
    bikeId = bikeId,
    title = title,
    dueOdometer = dueOdometer,
    dueDate = dueDate,
    intervalKm = intervalKm,
    lastDoneOdometer = lastDoneOdometer,
    lastDoneDate = lastDoneDate,
    isCompleted = isCompleted,
    notes = notes,
    createdAt = createdAt
)
