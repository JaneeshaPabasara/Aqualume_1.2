package com.example.aqualumedb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.aqualumedb.data.database.AqualumeDatabase
import com.example.aqualumedb.data.repository.AqualumeRepository
import com.example.aqualumedb.models.WaterLog
import kotlinx.coroutines.launch

class WaterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AqualumeRepository


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
    val allWaterLogs = repository.allWaterLogs.asLiveData()

    fun insertWaterLog(waterLog: WaterLog) = viewModelScope.launch {
        repository.insertWaterLog(waterLog)
    }

    fun updateWaterLog(waterLog: WaterLog) = viewModelScope.launch {
        repository.updateWaterLog(waterLog)
    }

    fun deleteWaterLog(id: Long) = viewModelScope.launch {
        repository.deleteWaterLog(id)
    }

    fun getWaterLogsByDateRange(startTime: Long, endTime: Long) =
        repository.getWaterLogsByDateRange(startTime, endTime).asLiveData()
}
