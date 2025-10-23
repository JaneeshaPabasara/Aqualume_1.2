package com.example.aqualumedb.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.aqualumedb.data.database.AqualumeDatabase
import com.example.aqualumedb.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataMigrationHelper(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("AqualumePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val database = AqualumeDatabase.getDatabase(context)

    private val migrationPrefs: SharedPreferences =
        context.getSharedPreferences("AqualumeMigration", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_SETTINGS = "user_settings"
        private const val KEY_WATER_LOGS = "water_logs"
        private const val KEY_MEDITATION_LOGS = "meditation_logs"
        private const val KEY_MOOD_LOGS = "mood_logs"
        private const val KEY_TASKS = "tasks"
        private const val KEY_CUSTOM_TASKS = "custom_tasks"
        private const val KEY_MIGRATION_COMPLETED = "migration_completed"
    }

    fun isMigrationCompleted(): Boolean {
        return migrationPrefs.getBoolean(KEY_MIGRATION_COMPLETED, false)
    }

    fun migrateDataToRoom() {
        if (isMigrationCompleted()) {
            return // Migration already done
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Migrate User Settings
                migrateUserSettings()

                // Migrate Water Logs
                migrateWaterLogs()

                // Migrate Meditation Logs
                migrateMeditationLogs()

                // Migrate Mood Logs
                migrateMoodLogs()

                // Migrate Tasks
                migrateTasks()

                // Migrate Custom Tasks
                migrateCustomTasks()

                // Mark migration as completed
                migrationPrefs.edit().putBoolean(KEY_MIGRATION_COMPLETED, true).apply()

                // Optionally clear old SharedPreferences data
                // clearOldData()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun migrateUserSettings() {
        val json = prefs.getString(KEY_USER_SETTINGS, null)
        if (json != null) {
            try {
                val settings = gson.fromJson(json, UserSettings::class.java)
                val settingsDao = database.userSettingsDao()
                val existingSettings = settingsDao.getUserSettingsSync()

                if (existingSettings == null) {
                    settingsDao.insertUserSettings(
                        com.example.aqualumedb.data.entities.UserSettingsEntity(
                            waterGoal = settings.waterGoal,
                            meditationGoal = settings.meditationGoal,
                            waterReminderInterval = settings.waterReminderInterval,
                            waterReminderEnabled = settings.waterReminderEnabled,
                            meditationReminderEnabled = settings.meditationReminderEnabled,
                            meditationReminderTime = settings.meditationReminderTime,
                            journalReminderEnabled = settings.journalReminderEnabled,
                            journalReminderTime = settings.journalReminderTime
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun migrateWaterLogs() {
        val json = prefs.getString(KEY_WATER_LOGS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<WaterLog>>() {}.type
                val waterLogs: List<WaterLog> = gson.fromJson(json, type)
                val waterLogDao = database.waterLogDao()

                waterLogs.forEach { log ->
                    waterLogDao.insertWaterLog(
                        com.example.aqualumedb.data.entities.WaterLogEntity(
                            id = 0, // Let Room auto-generate
                            amount = log.amount,
                            timestamp = log.timestamp,
                            isDailyGoalCompleted = log.isDailyGoalCompleted,
                            dailyTotal = log.dailyTotal
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun migrateMeditationLogs() {
        val json = prefs.getString(KEY_MEDITATION_LOGS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<MeditationLog>>() {}.type
                val meditationLogs: List<MeditationLog> = gson.fromJson(json, type)
                val meditationLogDao = database.meditationLogDao()

                meditationLogs.forEach { log ->
                    meditationLogDao.insertMeditationLog(
                        com.example.aqualumedb.data.entities.MeditationLogEntity(
                            id = 0, // Let Room auto-generate
                            duration = log.duration,
                            timestamp = log.timestamp,
                            isCompleted = log.isCompleted
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun migrateMoodLogs() {
        val json = prefs.getString(KEY_MOOD_LOGS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<MoodLog>>() {}.type
                val moodLogs: List<MoodLog> = gson.fromJson(json, type)
                val moodLogDao = database.moodLogDao()

                moodLogs.forEach { log ->
                    moodLogDao.insertMoodLog(
                        com.example.aqualumedb.data.entities.MoodLogEntity(
                            id = 0, // Let Room auto-generate
                            mood = log.mood,
                            moodDrawable = log.moodDrawable,
                            timestamp = log.timestamp,
                            timeString = log.timeString,
                            note = log.note,
                            emoji = log.emoji
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun migrateTasks() {
        val json = prefs.getString(KEY_TASKS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Task>>() {}.type
                val tasks: List<Task> = gson.fromJson(json, type)
                val taskDao = database.taskDao()

                tasks.forEach { task ->
                    taskDao.insertTask(
                        com.example.aqualumedb.data.entities.TaskEntity(
                            id = 0, // Let Room auto-generate
                            title = task.title,
                            isCompleted = task.isCompleted,
                            timestamp = task.timestamp
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun migrateCustomTasks() {
        val json = prefs.getString(KEY_CUSTOM_TASKS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<CustomTask>>() {}.type
                val customTasks: List<CustomTask> = gson.fromJson(json, type)
                val customTaskDao = database.customTaskDao()

                customTasks.forEach { task ->
                    customTaskDao.insertCustomTask(
                        com.example.aqualumedb.data.entities.CustomTaskEntity(
                            id = 0, // Let Room auto-generate
                            taskName = task.taskName,
                            timeDuration = task.timeDuration,
                            timestamp = task.timestamp
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun clearOldData() {
        prefs.edit().clear().apply()
    }
}