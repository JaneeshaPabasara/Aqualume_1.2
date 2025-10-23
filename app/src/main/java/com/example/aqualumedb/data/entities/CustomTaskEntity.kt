package com.example.aqualumedb.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_tasks")
data class CustomTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskName: String,
    val timeDuration: String,
    val timestamp: Long = System.currentTimeMillis()
)