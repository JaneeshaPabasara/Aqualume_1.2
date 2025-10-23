package com.example.aqualumedb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.aqualumedb.data.database.AqualumeDatabase
import com.example.aqualumedb.data.repository.AqualumeRepository
import com.example.aqualumedb.models.MoodLog
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AqualumeRepository
    val allMoodLogs = repository.allMoodLogs.asLiveData()

    init {
        val database = AqualumeDatabase.getDatabase(application)
        repository = AqualumeRepository(
            waterLogDao = database.waterLogDao(),
            meditationLogDao = database.meditationLogDao(),
            moodLogDao = database.moodLogDao(),
            taskDao = database.taskDao(),
            customTaskDao = database.customTaskDao(),
            userSettingsDao = database.userSettingsDao()
        )
    }

    fun insertMoodLog(moodLog: MoodLog) = viewModelScope.launch {
        repository.insertMoodLog(moodLog)
    }

    fun updateMoodLog(moodLog: MoodLog) = viewModelScope.launch {
        repository.updateMoodLog(moodLog)
    }

    fun deleteMoodLog(id: Long) = viewModelScope.launch {
        repository.deleteMoodLog(id)
    }

    fun getMoodLogsByDateRange(startTime: Long, endTime: Long) =
        repository.getMoodLogsByDateRange(startTime, endTime).asLiveData()
}