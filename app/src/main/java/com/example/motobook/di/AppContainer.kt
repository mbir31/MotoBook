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
        BikeRepositoryImpl(database.bikeDao(), onDataChanged = { triggerAutoBackup() })
    }

    val fuelRepository: FuelRepository by lazy {
        FuelRepositoryImpl(database.fuelDao(), onDataChanged = { triggerAutoBackup() })
    }

    val serviceRepository: ServiceRepository by lazy {
        ServiceRepositoryImpl(database.serviceDao(), onDataChanged = { triggerAutoBackup() })
    }

    val tyrePressureRepository: TyrePressureRepository by lazy {
        TyrePressureRepositoryImpl(database.tyrePressureDao(), onDataChanged = { triggerAutoBackup() })
    }

    val washRepository: WashRepository by lazy {
        WashRepositoryImpl(database.washDao(), onDataChanged = { triggerAutoBackup() })
    }

    val chainRepository: ChainRepository by lazy {
        ChainRepositoryImpl(database.chainDao(), onDataChanged = { triggerAutoBackup() })
    }

    val reminderRepository: ReminderRepository by lazy {
        ReminderRepositoryImpl(database.reminderDao(), onDataChanged = { triggerAutoBackup() })
    }


    val backupRepository: BackupRepository by lazy {
        BackupRepositoryImpl(database)
    }

    suspend fun triggerAutoBackup() {
        com.example.motobook.data.backup.AutoBackupManager.triggerAutoBackup(
            context = context,
            database = database,
            userPreferences = userPreferences
        )
    }
}

