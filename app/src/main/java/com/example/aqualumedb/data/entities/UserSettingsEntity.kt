package com.example.aqualumedb.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for settings
    val waterGoal: Int = 2000,
    val meditationGoal: Int = 20,
    val waterReminderInterval: Long = 3600000,
    val waterReminderEnabled: Boolean = false,
    val meditationReminderEnabled: Boolean = true,
    val meditationReminderTime: String = "09:00 AM",
    val journalReminderEnabled: Boolean = true,
    val journalReminderTime: String = "09:00 AM"
)