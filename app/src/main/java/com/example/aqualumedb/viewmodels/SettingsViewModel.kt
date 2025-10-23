package com.example.aqualumedb.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.aqualumedb.data.database.AqualumeDatabase
import com.example.aqualumedb.data.repository.AqualumeRepository
import com.example.aqualumedb.models.UserSettings
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AqualumeRepository
    val userSettings = repository.userSettings.asLiveData()

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

    fun updateUserSettings(settings: UserSettings) = viewModelScope.launch {
        repository.updateUserSettings(settings)
    }
}