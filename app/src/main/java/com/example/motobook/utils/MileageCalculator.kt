package com.example.motobook.utils

import com.example.motobook.domain.model.FuelEntry
import com.example.motobook.domain.model.MileageCycle
import com.example.motobook.domain.model.MileageStats

object MileageCalculator {

    fun calculateMileageStats(entries: List<FuelEntry>): MileageStats {
        if (entries.isEmpty()) {
            return MileageStats()
        }

        // Sort entries by date ascending, then odometer ascending
        val sortedEntries = entries.sortedWith(compareBy({ it.date }, { it.odometer }))

        val cycles = mutableListOf<MileageCycle>()
        var cycleCount = 0

        var currentStartFullTankIndex: Int? = null

        for (i in sortedEntries.indices) {
            val entry = sortedEntries[i]
            if (entry.refuelType.equals("FULL", ignoreCase = true)) {
                if (currentStartFullTankIndex == null) {
                    currentStartFullTankIndex = i
                } else {
                    val startIndex = currentStartFullTankIndex
                    val endIndex = i
                    val startEntry = sortedEntries[startIndex]
                    val endEntry = sortedEntries[endIndex]

                    val distance = endEntry.odometer - startEntry.odometer

                    // Fuel used is sum of all entries from startIndex + 1 to endIndex inclusive
                    val intermediateAndEndFuel = (startIndex + 1..endIndex).map { sortedEntries[it] }
                    val fuelUsed = intermediateAndEndFuel.sumOf { it.fuelQuantity.toDouble() }.toFloat()

                    if (distance > 0 && fuelUsed > 0) {
                        cycleCount++
                        val mileage = distance / fuelUsed
                        val intermediateEntries = if (startIndex + 1 < endIndex) {
                            sortedEntries.subList(startIndex + 1, endIndex)
                        } else emptyList()

                        cycles.add(
                            MileageCycle(
                                cycleNumber = cycleCount,
                                startEntry = startEntry,
                                endEntry = endEntry,
                                intermediateEntries = intermediateEntries,
                                distanceKm = distance,
                                fuelUsedLiters = fuelUsed,
                                mileageKmPerLiter = mileage,
                                cycleStartDate = startEntry.date,
                                cycleEndDate = endEntry.date
                            )
                        )
                    }

                    // The closing full tank becomes the opening full tank for the next cycle
                    currentStartFullTankIndex = endIndex
                }
            }
        }

        if (cycles.isEmpty()) {
            // Calculate ongoing partial fuel if there's a full tank
            val lastFullTankIndex = sortedEntries.indexOfLast { it.refuelType.equals("FULL", ignoreCase = true) }
            val currentPartialFuel = if (lastFullTankIndex >= 0 && lastFullTankIndex < sortedEntries.size - 1) {
                sortedEntries.subList(lastFullTankIndex + 1, sortedEntries.size)
                    .sumOf { it.fuelQuantity.toDouble() }.toFloat()
            } else 0f

            return MileageStats(currentCyclePartialFuel = currentPartialFuel)
        }

        val currentMileage = cycles.lastOrNull()?.mileageKmPerLiter
        val averageMileage = cycles.map { it.mileageKmPerLiter }.average().toFloat()
        val bestMileage = cycles.maxOfOrNull { it.mileageKmPerLiter }
        val worstMileage = cycles.minOfOrNull { it.mileageKmPerLiter }

        val lastFiveCycles = cycles.takeLast(5)
        val lastFiveAvg = if (lastFiveCycles.isNotEmpty()) {
            lastFiveCycles.map { it.mileageKmPerLiter }.average().toFloat()
        } else null

        val totalDistance = cycles.sumOf { it.distanceKm.toDouble() }.toFloat()
        val totalFuel = cycles.sumOf { it.fuelUsedLiters.toDouble() }.toFloat()

        val totalCostAllEntries = sortedEntries.sumOf { it.totalCost.toDouble() }.toFloat()
        val costPerKm = if (totalDistance > 0) totalCostAllEntries / totalDistance else null

        // Ongoing cycle partial fuel
        val lastFullTankIndex = sortedEntries.indexOfLast { it.refuelType.equals("FULL", ignoreCase = true) }
        val currentPartialFuel = if (lastFullTankIndex >= 0 && lastFullTankIndex < sortedEntries.size - 1) {
            sortedEntries.subList(lastFullTankIndex + 1, sortedEntries.size)
                .sumOf { it.fuelQuantity.toDouble() }.toFloat()
        } else 0f

        return MileageStats(
            completedCycles = cycles.reversed(), // Newest cycles first for UI list
            currentMileage = currentMileage,
            averageMileage = averageMileage,
            bestMileage = bestMileage,
            worstMileage = worstMileage,
            lastFiveCycleAverage = lastFiveAvg,
            totalDistanceCovered = totalDistance,
            totalFuelConsumed = totalFuel,
            totalCycles = cycles.size,
            costPerKm = costPerKm,
            currentCyclePartialFuel = currentPartialFuel
        )
    }
}
