package com.example.motobook.di

import android.content.Context
import com.example.motobook.data.local.database.MotoBookDatabase
import com.example.motobook.data.local.preferences.UserPreferencesDataStore
import com.example.motobook.data.repository.*
import com.example.motobook.domain.repository.*

class AppContainer(private val context: Context) {

    val database: MotoBookDatabase by lazy {
        MotoBookDatabase.getDatabase(context)
    }

    val userPreferences: UserPreferencesDataStore by lazy {
        UserPreferencesDataStore(context)
    }

    val bikeRepository: BikeRepository by lazy {
        BikeRepositoryImpl(database.bikeDao())
    }

    val fuelRepository: FuelRepository by lazy {
        FuelRepositoryImpl(database.fuelDao())
    }

    val serviceRepository: ServiceRepository by lazy {
        ServiceRepositoryImpl(database.serviceDao())
    }

    val tyrePressureRepository: TyrePressureRepository by lazy {
        TyrePressureRepositoryImpl(database.tyrePressureDao())
    }

    val washRepository: WashRepository by lazy {
        WashRepositoryImpl(database.washDao())
    }

    val chainRepository: ChainRepository by lazy {
        ChainRepositoryImpl(database.chainDao())
    }

    val backupRepository: BackupRepository by lazy {
        BackupRepositoryImpl(
            database.bikeDao(),
            database.fuelDao(),
            database.serviceDao(),
            database.tyrePressureDao(),
            database.washDao(),
            database.chainDao()
        )
    }
}
