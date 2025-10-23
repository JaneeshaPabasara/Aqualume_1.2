package com.example.aqualumedb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.aqualumedb.data.database.AqualumeDatabase
import com.example.aqualumedb.data.repository.AqualumeRepository
import com.example.aqualumedb.models.MeditationLog
import kotlinx.coroutines.launch

class MeditationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AqualumeRepository
    val allMeditationLogs = repository.allMeditationLogs.asLiveData()

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

    fun insertMeditationLog(meditationLog: MeditationLog) = viewModelScope.launch {
        repository.insertMeditationLog(meditationLog)
    }

    fun updateMeditationLog(meditationLog: MeditationLog) = viewModelScope.launch {
        repository.updateMeditationLog(meditationLog)
    }

    fun deleteMeditationLog(id: Long) = viewModelScope.launch {
        repository.deleteMeditationLog(id)
    }

    fun getMeditationLogsByDateRange(startTime: Long, endTime: Long) =
        repository.getMeditationLogsByDateRange(startTime, endTime).asLiveData()
}