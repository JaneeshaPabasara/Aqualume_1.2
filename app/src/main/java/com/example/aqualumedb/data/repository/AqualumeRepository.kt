
package com.example.aqualumedb.data.repository

import com.example.aqualumedb.data.dao.*
import com.example.aqualumedb.data.entities.*
import com.example.aqualumedb.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AqualumeRepository(
    private val waterLogDao: WaterLogDao,
    private val meditationLogDao: MeditationLogDao,
    private val moodLogDao: MoodLogDao,
    private val taskDao: TaskDao,
    private val customTaskDao: CustomTaskDao,
    private val userSettingsDao: UserSettingsDao
) {

    // Water Logs
    val allWaterLogs: Flow<List<WaterLog>> = waterLogDao.getAllWaterLogs().map { entities ->
        entities.map { it.toModel() }
    }

    fun getWaterLogsByDateRange(startTime: Long, endTime: Long): Flow<List<WaterLog>> {
        return waterLogDao.getWaterLogsByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun insertWaterLog(waterLog: WaterLog): Long {
        return waterLogDao.insertWaterLog(waterLog.toEntity())
    }

    suspend fun updateWaterLog(waterLog: WaterLog) {
        waterLogDao.updateWaterLog(waterLog.toEntity())
    }

    suspend fun deleteWaterLog(id: Long) {
        waterLogDao.deleteWaterLogById(id)
    }

    // Meditation Logs
    val allMeditationLogs: Flow<List<MeditationLog>> = meditationLogDao.getAllMeditationLogs().map { entities ->
        entities.map { it.toModel() }
    }

    fun getMeditationLogsByDateRange(startTime: Long, endTime: Long): Flow<List<MeditationLog>> {
        return meditationLogDao.getMeditationLogsByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun insertMeditationLog(meditationLog: MeditationLog): Long {
        return meditationLogDao.insertMeditationLog(meditationLog.toEntity())
    }

    suspend fun updateMeditationLog(meditationLog: MeditationLog) {
        meditationLogDao.updateMeditationLog(meditationLog.toEntity())
    }

    suspend fun deleteMeditationLog(id: Long) {
        meditationLogDao.deleteMeditationLogById(id)
    }

    // Mood Logs
    val allMoodLogs: Flow<List<MoodLog>> = moodLogDao.getAllMoodLogs().map { entities ->
        entities.map { it.toModel() }
    }

    fun getMoodLogsByDateRange(startTime: Long, endTime: Long): Flow<List<MoodLog>> {
        return moodLogDao.getMoodLogsByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun insertMoodLog(moodLog: MoodLog): Long {
        return moodLogDao.insertMoodLog(moodLog.toEntity())
    }

    suspend fun updateMoodLog(moodLog: MoodLog) {
        moodLogDao.updateMoodLog(moodLog.toEntity())
    }

    suspend fun deleteMoodLog(id: Long) {
        moodLogDao.deleteMoodLogById(id)
    }

    // Tasks
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toModel() }
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    // Custom Tasks
    val allCustomTasks: Flow<List<CustomTask>> = customTaskDao.getAllCustomTasks().map { entities ->
        entities.map { it.toModel() }
    }

    suspend fun insertCustomTask(customTask: CustomTask): Long {
        return customTaskDao.insertCustomTask(customTask.toEntity())
    }

    suspend fun updateCustomTask(customTask: CustomTask) {
        customTaskDao.updateCustomTask(customTask.toEntity())
    }

    suspend fun deleteCustomTask(id: Long) {
        customTaskDao.deleteCustomTaskById(id)
    }

    // User Settings
    val userSettings: Flow<UserSettings?> = userSettingsDao.getUserSettings().map { entity ->
        entity?.toModel()
    }

    suspend fun getUserSettingsSync(): UserSettings {
        return userSettingsDao.getUserSettingsSync()?.toModel() ?: UserSettings()
    }

    suspend fun updateUserSettings(settings: UserSettings) {
        userSettingsDao.updateUserSettings(settings.toEntity())
    }

    // Extension functions for mapping
    private fun WaterLogEntity.toModel() = WaterLog(
        id = id,
        amount = amount,
        timestamp = timestamp,
        isDailyGoalCompleted = isDailyGoalCompleted,
        dailyTotal = dailyTotal
    )

    private fun WaterLog.toEntity() = WaterLogEntity(
        id = id,
        amount = amount,
        timestamp = timestamp,
        isDailyGoalCompleted = isDailyGoalCompleted,
        dailyTotal = dailyTotal
    )

    private fun MeditationLogEntity.toModel() = MeditationLog(
        id = id,
        duration = duration,
        timestamp = timestamp,
        isCompleted = isCompleted
    )

    private fun MeditationLog.toEntity() = MeditationLogEntity(
        id = id,
        duration = duration,
        timestamp = timestamp,
        isCompleted = isCompleted
    )

    private fun MoodLogEntity.toModel() = MoodLog(
        id = id,
        mood = mood,
        moodDrawable = moodDrawable,
        timestamp = timestamp,
        timeString = timeString,
        note = note,
        emoji = emoji
    )

    private fun MoodLog.toEntity() = MoodLogEntity(
        id = id,
        mood = mood,
        moodDrawable = moodDrawable,
        timestamp = timestamp,
        timeString = timeString,
        note = note,
        emoji = emoji
    )

    private fun TaskEntity.toModel() = Task(
        id = id,
        title = title,
        isCompleted = isCompleted,
        timestamp = timestamp
    )

    private fun Task.toEntity() = TaskEntity(
        id = id,
        title = title,
        isCompleted = isCompleted,
        timestamp = timestamp
    )

    private fun CustomTaskEntity.toModel() = CustomTask(
        id = id,
        taskName = taskName,
        timeDuration = timeDuration,
        timestamp = timestamp
    )

    private fun CustomTask.toEntity() = CustomTaskEntity(
        id = id,
        taskName = taskName,
        timeDuration = timeDuration,
        timestamp = timestamp
    )

    private fun UserSettingsEntity.toModel() = UserSettings(
        waterGoal = waterGoal,
        meditationGoal = meditationGoal,
        waterReminderInterval = waterReminderInterval,
        waterReminderEnabled = waterReminderEnabled,
        meditationReminderEnabled = meditationReminderEnabled,
        meditationReminderTime = meditationReminderTime,
        journalReminderEnabled = journalReminderEnabled,
        journalReminderTime = journalReminderTime
    )

    private fun UserSettings.toEntity() = UserSettingsEntity(
        waterGoal = waterGoal,
        meditationGoal = meditationGoal,
        waterReminderInterval = waterReminderInterval,
        waterReminderEnabled = waterReminderEnabled,
        meditationReminderEnabled = meditationReminderEnabled,
        meditationReminderTime = meditationReminderTime,
        journalReminderEnabled = journalReminderEnabled,
        journalReminderTime = journalReminderTime
    )
}