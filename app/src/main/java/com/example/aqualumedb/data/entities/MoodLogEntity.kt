package com.example.aqualumedb.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_logs")
data class MoodLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mood: String,
    val moodDrawable: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val timeString: String,
    val note: String,
    val emoji: String
)