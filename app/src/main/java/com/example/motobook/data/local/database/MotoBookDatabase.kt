package com.example.motobook.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.motobook.data.local.dao.*
import com.example.motobook.data.local.entity.*

@Database(
    entities = [
        BikeEntity::class,
        FuelEntity::class,
        ServiceEntity::class,
        TyrePressureEntity::class,
        WashEntity::class,
        ChainEntity::class,
        ReminderEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MotoBookDatabase : RoomDatabase() {
    abstract fun bikeDao(): BikeDao
    abstract fun fuelDao(): FuelDao
    abstract fun serviceDao(): ServiceDao
    abstract fun tyrePressureDao(): TyrePressureDao
    abstract fun washDao(): WashDao
    abstract fun chainDao(): ChainDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "motobook_database"

        @Volatile
        private var INSTANCE: MotoBookDatabase? = null

        fun getDatabase(context: Context): MotoBookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MotoBookDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
