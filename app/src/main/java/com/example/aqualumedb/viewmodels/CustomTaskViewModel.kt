package com.example.aqualumedb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.aqualumedb.data.database.AqualumeDatabase
import com.example.aqualumedb.data.repository.AqualumeRepository
import com.example.aqualumedb.models.CustomTask
import kotlinx.coroutines.launch

class CustomTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AqualumeRepository
    val allCustomTasks = repository.allCustomTasks.asLiveData()

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

    fun insertCustomTask(customTask: CustomTask) = viewModelScope.launch {
        repository.insertCustomTask(customTask)
    }

    fun updateCustomTask(customTask: CustomTask) = viewModelScope.launch {
        repository.updateCustomTask(customTask)
    }

    fun deleteCustomTask(id: Long) = viewModelScope.launch {
        repository.deleteCustomTask(id)
    }
}