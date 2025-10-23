package com.example.aqualumedb.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meditation_logs")
data class MeditationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val duration: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)