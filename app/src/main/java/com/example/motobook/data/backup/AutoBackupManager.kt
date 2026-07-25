package com.example.motobook.data.backup

import android.content.Context
import android.os.Environment
import com.example.motobook.data.local.database.MotoBookDatabase
import com.example.motobook.data.local.entity.*
import com.example.motobook.data.local.preferences.UserPreferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class MotoBookBackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val bikes: List<BikeEntity> = emptyList(),
    val fuelEntries: List<FuelEntity> = emptyList(),
    val serviceEntries: List<ServiceEntity> = emptyList(),
    val tyreEntries: List<TyrePressureEntity> = emptyList(),
    val washEntries: List<WashEntity> = emptyList(),
    val chainEntries: List<ChainEntity> = emptyList(),
    val reminders: List<ReminderEntity> = emptyList()
)

object AutoBackupManager {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(MotoBookBackupData::class.java)

    /**
     * Public Accessible Backup Directory in Phone's Internal Storage
     * /storage/emulated/0/Documents/MotoBook_Backups/
     */
    fun getPublicBackupDirectory(context: Context): File {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val backupDir = File(documentsDir, "MotoBook_Backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir
    }

    /**
     * App External Files Directory Fallback
     */
    fun getAppExternalBackupDirectory(context: Context): File? {
        val extDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return null
        val backupDir = File(extDir, "MotoBook_Backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir
    }

    /**
     * Generates a full JSON string backup of all database tables.
     */
    suspend fun generateBackupJson(database: MotoBookDatabase): String = withContext(Dispatchers.IO) {
        val bikes = database.bikeDao().getBikeCount().let {
            // We can fetch via flow first or add sync queries
            // Fetching sync list:
            val list = mutableListOf<BikeEntity>()
            database.bikeDao().getAllBikes() // flow
            // To get sync list without collect, we can query DAO
            list
        }

        val allBikes = database.bikeDao().getAllBikesSync()
        val allFuel = database.fuelDao().getAllFuelEntriesSync()
        val allServices = database.serviceDao().getAllServicesSync()
        val allTyres = database.tyrePressureDao().getAllTyresSync()
        val allWashes = database.washDao().getAllWashesSync()
        val allChains = database.chainDao().getAllChainsSync()
        val allReminders = database.reminderDao().getAllRemindersSync()

        val backupData = MotoBookBackupData(
            version = 1,
            timestamp = System.currentTimeMillis(),
            bikes = allBikes,
            fuelEntries = allFuel,
            serviceEntries = allServices,
            tyreEntries = allTyres,
            washEntries = allWashes,
            chainEntries = allChains,
            reminders = allReminders
        )

        adapter.indent("  ").toJson(backupData)
    }

    /**
     * Automatically triggers a backup to internal storage on every data input.
     */
    suspend fun triggerAutoBackup(
        context: Context,
        database: MotoBookDatabase,
        userPreferences: UserPreferencesDataStore? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonString = generateBackupJson(database)

            val publicDir = getPublicBackupDirectory(context)
            val latestFile = File(publicDir, "motobook_auto_backup_latest.json")
            latestFile.writeText(jsonString)

            // Also keep a dated snapshot
            val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val timestampedFile = File(publicDir, "motobook_backup_$dateStr.json")
            timestampedFile.writeText(jsonString)

            // Fallback app external dir
            val appExtDir = getAppExternalBackupDirectory(context)
            appExtDir?.let { dir ->
                File(dir, "motobook_auto_backup_latest.json").writeText(jsonString)
            }

            userPreferences?.setLastBackupTime(System.currentTimeMillis())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Restores database content from JSON backup string.
     */
    suspend fun restoreFromBackupJson(
        database: MotoBookDatabase,
        jsonString: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val data = adapter.fromJson(jsonString) ?: return@withContext false

            // Insert restored entities
            data.bikes.forEach { database.bikeDao().insertBike(it) }
            data.fuelEntries.forEach { database.fuelDao().insertFuelEntry(it) }
            data.serviceEntries.forEach { database.serviceDao().insertServiceEntry(it) }
            data.tyreEntries.forEach { database.tyrePressureDao().insertTyrePressureEntry(it) }
            data.washEntries.forEach { database.washDao().insertWashEntry(it) }
            data.chainEntries.forEach { database.chainDao().insertChainEntry(it) }
            data.reminders.forEach { database.reminderDao().insertReminder(it) }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
