package com.example.aqualumedb.data.dao

import androidx.room.*
import com.example.aqualumedb.data.entities.MoodLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodLogDao {
    @Query("SELECT * FROM mood_logs ORDER BY timestamp DESC")
    fun getAllMoodLogs(): Flow<List<MoodLogEntity>>

    @Query("SELECT * FROM mood_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getMoodLogsByDateRange(startTime: Long, endTime: Long): Flow<List<MoodLogEntity>>

    @Query("SELECT * FROM mood_logs WHERE id = :id")
    suspend fun getMoodLogById(id: Long): MoodLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodLog(moodLog: MoodLogEntity): Long

    @Update
    suspend fun updateMoodLog(moodLog: MoodLogEntity)

    @Delete
    suspend fun deleteMoodLog(moodLog: MoodLogEntity)

    @Query("DELETE FROM mood_logs WHERE id = :id")
    suspend fun deleteMoodLogById(id: Long)
}