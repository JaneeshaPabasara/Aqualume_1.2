package com.example.aqualumedb.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isDailyGoalCompleted: Boolean = false,
    val dailyTotal: Int = 0
)