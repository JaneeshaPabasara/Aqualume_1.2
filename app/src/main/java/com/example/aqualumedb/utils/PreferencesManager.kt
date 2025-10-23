package com.example.aqualumedb.utils

import android.content.Context
import com.example.aqualumedb.data.database.AqualumeDatabase
import com.example.aqualumedb.data.repository.AqualumeRepository
import com.example.aqualumedb.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PreferencesManager(context: Context) {

    private val database = AqualumeDatabase.getDatabase(context)
    private val repository = AqualumeRepository(
        waterLogDao = database.waterLogDao(),
        meditationLogDao = database.meditationLogDao(),
        moodLogDao = database.moodLogDao(),
        taskDao = database.taskDao(),
        customTaskDao = database.customTaskDao(),
        userSettingsDao = database.userSettingsDao()
    )

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // User Settings
    fun getUserSettings(): UserSettings = runBlocking {
        repository.getUserSettingsSync()
    }

    fun saveUserSettings(settings: UserSettings) {
        coroutineScope.launch {
            repository.updateUserSettings(settings)
        }
    }

    // Water Logs
    fun getWaterLogs(): List<WaterLog> = runBlocking {
        repository.allWaterLogs.first()
    }

    fun saveWaterLog(waterLog: WaterLog) {
        coroutineScope.launch {
            repository.insertWaterLog(waterLog)
        }
    }

    fun updateWaterLog(waterLog: WaterLog) {
        coroutineScope.launch {
            repository.updateWaterLog(waterLog)
        }
    }

    fun deleteWaterLog(logId: Long) {
        coroutineScope.launch {
            repository.deleteWaterLog(logId)
        }
    }

    // Meditation Logs
    fun getMeditationLogs(): List<MeditationLog> = runBlocking {
        repository.allMeditationLogs.first()
    }

    fun saveMeditationLog(meditationLog: MeditationLog) {
        coroutineScope.launch {
            repository.insertMeditationLog(meditationLog)
        }
    }

    fun updateMeditationLog(logId: Long, duration: Int, completed: Boolean) {
        coroutineScope.launch {
            val logs = repository.allMeditationLogs.first()
            val log = logs.find { it.id == logId }
            log?.let {
                repository.updateMeditationLog(it.copy(duration = duration, isCompleted = completed))
            }
        }
    }

    fun deleteMeditationLog(logId: Long) {
        coroutineScope.launch {
            repository.deleteMeditationLog(logId)
        }
    }

    // Mood Logs
    fun getMoodLogs(): List<MoodLog> = runBlocking {
        repository.allMoodLogs.first()
    }

    fun saveMoodLog(moodLog: MoodLog) {
        coroutineScope.launch {
            repository.insertMoodLog(moodLog)
        }
    }

    fun updateMoodLog(logId: Long, moodName: String, moodEmoji: Int) {
        coroutineScope.launch {
            val logs = repository.allMoodLogs.first()
            val log = logs.find { it.id == logId }
            log?.let {
                repository.updateMoodLog(it.copy(mood = moodName, moodDrawable = moodEmoji))
            }
        }
    }

    fun deleteMoodLog(logId: Long) {
        coroutineScope.launch {
            repository.deleteMoodLog(logId)
        }
    }

    // Tasks
    fun getAllTasks(): List<Task> = runBlocking {
        repository.allTasks.first()
    }

    fun saveTask(task: Task) {
        coroutineScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        coroutineScope.launch {
            val tasks = repository.allTasks.first()
            val task = tasks.find { it.id == taskId }
            task?.let {
                repository.updateTask(it.copy(isCompleted = isCompleted))
            }
        }
    }

    fun clearAllTasks() {
        coroutineScope.launch {
            repository.deleteAllTasks()
        }
    }

    // Meditation Goal
    fun getMeditationGoal(): Int {
        return getUserSettings().meditationGoal
    }

    fun setMeditationGoal(goal: Int) {
        val settings = getUserSettings()
        saveUserSettings(settings.copy(meditationGoal = goal))
    }

    // Custom Tasks
    fun getAllCustomTasks(): List<CustomTask> = runBlocking {
        repository.allCustomTasks.first()
    }

    fun saveCustomTask(customTask: CustomTask) {
        coroutineScope.launch {
            repository.insertCustomTask(customTask)
        }
    }

    fun updateCustomTask(customTask: CustomTask) {
        coroutineScope.launch {
            repository.updateCustomTask(customTask)
        }
    }

    fun deleteCustomTask(taskId: Long) {
        coroutineScope.launch {
            repository.deleteCustomTask(taskId)
        }
    }

    // Get repository for Flow-based operations (for ViewModels)
    fun getRepository(): AqualumeRepository = repository
}