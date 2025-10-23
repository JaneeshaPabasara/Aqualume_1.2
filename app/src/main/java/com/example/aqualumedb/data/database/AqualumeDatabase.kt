package com.example.aqualumedb.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aqualumedb.data.dao.*
import com.example.aqualumedb.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WaterLogEntity::class,
        MeditationLogEntity::class,
        MoodLogEntity::class,
        TaskEntity::class,
        CustomTaskEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AqualumeDatabase : RoomDatabase() {

    abstract fun waterLogDao(): WaterLogDao
    abstract fun meditationLogDao(): MeditationLogDao
    abstract fun moodLogDao(): MoodLogDao
    abstract fun taskDao(): TaskDao
    abstract fun customTaskDao(): CustomTaskDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AqualumeDatabase? = null

        fun getDatabase(context: Context): AqualumeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AqualumeDatabase::class.java,
                    "aqualume_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Initialize default settings
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val settingsDao = database.userSettingsDao()
                        val existingSettings = settingsDao.getUserSettingsSync()
                        if (existingSettings == null) {
                            settingsDao.insertUserSettings(UserSettingsEntity())
                        }
                    }
                }
            }
        }
    }
}