package com.example.aqualumedb.models

data class CustomTask(
    val id: Long = System.currentTimeMillis(),
    val taskName: String,
    val timeDuration: String,
    val timestamp: Long = System.currentTimeMillis()
)