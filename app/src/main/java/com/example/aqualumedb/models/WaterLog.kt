package com.example.aqualumedb.models


data class WaterLog(
    val id: Long = System.currentTimeMillis(),
    val amount: Int, // in ml
    val timestamp: Long = System.currentTimeMillis(),
    val isDailyGoalCompleted: Boolean = false,
    val dailyTotal: Int = 0
)
