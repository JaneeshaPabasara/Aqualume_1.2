package com.example.aqualumedb.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.aqualumedb.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("AqualumePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER_SETTINGS = "user_settings"
        private const val KEY_WATER_LOGS = "water_logs"
        private const val KEY_MEDITATION_LOGS = "meditation_logs"
        private const val KEY_MOOD_LOGS = "mood_logs"
        private const val KEY_TASKS = "tasks"

        private const val KEY_CUSTOM_TASKS = "custom_tasks"
        private const val KEY_MEDITATION_GOAL = "meditation_goal"
    }

    // User Settings
    fun getUserSettings(): UserSettings {
        val json = prefs.getString(KEY_USER_SETTINGS, null)
        return if (json != null) {
            gson.fromJson(json, UserSettings::class.java)
        } else {
            UserSettings() // Return default settings
        }
    }

    fun saveUserSettings(settings: UserSettings) {
        val json = gson.toJson(settings)
        prefs.edit().putString(KEY_USER_SETTINGS, json).apply()
    }

    // Water Logs
    fun getWaterLogs(): List<WaterLog> {
        val json = prefs.getString(KEY_WATER_LOGS, null) ?: return emptyList()
        val type = object : TypeToken<List<WaterLog>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveWaterLog(waterLog: WaterLog) {
        val logs = getWaterLogs().toMutableList()
        logs.add(waterLog)
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_WATER_LOGS, json).apply()
    }

    fun updateWaterLog(waterLog: WaterLog) {
        val logs = getWaterLogs().toMutableList()
        val index = logs.indexOfFirst { it.id == waterLog.id }
        if (index != -1) {
            logs[index] = waterLog
            val json = gson.toJson(logs)
            prefs.edit().putString(KEY_WATER_LOGS, json).apply()
        }
    }

    fun deleteWaterLog(logId: Long) {
        val logs = getWaterLogs().toMutableList()
        logs.removeAll { it.id == logId }
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_WATER_LOGS, json).apply()
    }

    // Meditation Logs
    fun getMeditationLogs(): List<MeditationLog> {
        val json = prefs.getString(KEY_MEDITATION_LOGS, null) ?: return emptyList()
        val type = object : TypeToken<List<MeditationLog>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveMeditationLog(meditationLog: MeditationLog) {
        val logs = getMeditationLogs().toMutableList()
        logs.add(meditationLog)
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_MEDITATION_LOGS, json).apply()
    }

    fun updateMeditationLog(logId: Long, duration: Int, completed: Boolean) {
        val logs = getMeditationLogs().toMutableList()
        val index = logs.indexOfFirst { it.id == logId }
        if (index != -1) {
            logs[index] = logs[index].copy(duration = duration, isCompleted = completed)
            val json = gson.toJson(logs)
            prefs.edit().putString(KEY_MEDITATION_LOGS, json).apply()
        }
    }

    fun deleteMeditationLog(logId: Long) {
        val logs = getMeditationLogs().toMutableList()
        logs.removeAll { it.id == logId }
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_MEDITATION_LOGS, json).apply()
    }

    // Mood Logs
    fun getMoodLogs(): List<MoodLog> {
        val json = prefs.getString(KEY_MOOD_LOGS, null) ?: return emptyList()
        val type = object : TypeToken<List<MoodLog>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveMoodLog(moodLog: MoodLog) {
        val logs = getMoodLogs().toMutableList()
        logs.add(moodLog)
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_MOOD_LOGS, json).apply()
    }

    fun updateMoodLog(logId: Long, moodName: String, moodEmoji: Int) {
        val logs = getMoodLogs().toMutableList()
        val index = logs.indexOfFirst { it.id == logId }
        if (index != -1) {
            logs[index] = logs[index].copy(mood = moodName, moodDrawable = moodEmoji)
            val json = gson.toJson(logs)
            prefs.edit().putString(KEY_MOOD_LOGS, json).apply()
        }
    }

    fun deleteMoodLog(logId: Long) {
        val logs = getMoodLogs().toMutableList()
        logs.removeAll { it.id == logId }
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_MOOD_LOGS, json).apply()
    }

    // Tasks
    fun getAllTasks(): List<Task> {
        val json = prefs.getString(KEY_TASKS, null) ?: return emptyList()
        val type = object : TypeToken<List<Task>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveTask(task: Task) {
        val tasks = getAllTasks().toMutableList()
        tasks.add(task)
        val json = gson.toJson(tasks)
        prefs.edit().putString(KEY_TASKS, json).apply()
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        val tasks = getAllTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            tasks[index] = tasks[index].copy(isCompleted = isCompleted)
            val json = gson.toJson(tasks)
            prefs.edit().putString(KEY_TASKS, json).apply()
        }
    }

    fun clearAllTasks() {
        prefs.edit().remove(KEY_TASKS).apply()
    }

    // Meditation Goal
    fun getMeditationGoal(): Int {
        return prefs.getInt(KEY_MEDITATION_GOAL, 20) // Default 20 minutes
    }

    fun setMeditationGoal(goal: Int) {
        prefs.edit().putInt(KEY_MEDITATION_GOAL, goal).apply()
    }


    // Custom Tasks
    fun getAllCustomTasks(): List<CustomTask> {
        val json = prefs.getString(KEY_CUSTOM_TASKS, null) ?: return emptyList()
        val type = object : TypeToken<List<CustomTask>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveCustomTask(customTask: CustomTask) {
        val tasks = getAllCustomTasks().toMutableList()
        tasks.add(customTask)
        val json = gson.toJson(tasks)
        prefs.edit().putString(KEY_CUSTOM_TASKS, json).apply()
    }

    fun updateCustomTask(customTask: CustomTask) {
        val tasks = getAllCustomTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == customTask.id }
        if (index != -1) {
            tasks[index] = customTask
            val json = gson.toJson(tasks)
            prefs.edit().putString(KEY_CUSTOM_TASKS, json).apply()
        }
    }

    fun deleteCustomTask(taskId: Long) {
        val tasks = getAllCustomTasks().toMutableList()
        tasks.removeAll { it.id == taskId }
        val json = gson.toJson(tasks)
        prefs.edit().putString(KEY_CUSTOM_TASKS, json).apply()
    }
}