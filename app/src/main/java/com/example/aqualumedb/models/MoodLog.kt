package com.example.aqualumedb.models

data class MoodLog(
    val id: Long = System.currentTimeMillis(),
    val mood: String, // "Happy", "Sad", "Angry", "Calm", etc.
    val moodDrawable: Int, // Resource ID for the mood image (e.g., R.drawable.happy)
    val timestamp: Long = System.currentTimeMillis(),
    val timeString: String,
    val note: String,
    val emoji: String
)