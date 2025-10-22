package com.example.aqualumedb.models

data class UserSettings(
    val waterGoal: Int = 2000, // ml per day
    val meditationGoal: Int = 20, // minutes per day
    val waterReminderInterval: Long = 3600000, // 1 hour in milliseconds
    val waterReminderEnabled: Boolean = false,
    val meditationReminderEnabled: Boolean = true,
    val meditationReminderTime: String = "09:00 AM",
    val journalReminderEnabled: Boolean = true,
    val journalReminderTime: String = "09:00 AM"
)