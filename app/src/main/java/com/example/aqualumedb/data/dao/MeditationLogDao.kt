package com.example.aqualumedb.data.dao

import androidx.room.*
import com.example.aqualumedb.data.entities.MeditationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationLogDao {
    @Query("SELECT * FROM meditation_logs ORDER BY timestamp DESC")
    fun getAllMeditationLogs(): Flow<List<MeditationLogEntity>>

    @Query("SELECT * FROM meditation_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getMeditationLogsByDateRange(startTime: Long, endTime: Long): Flow<List<MeditationLogEntity>>

    @Query("SELECT * FROM meditation_logs WHERE id = :id")
    suspend fun getMeditationLogById(id: Long): MeditationLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeditationLog(meditationLog: MeditationLogEntity): Long

    @Update
    suspend fun updateMeditationLog(meditationLog: MeditationLogEntity)

    @Delete
    suspend fun deleteMeditationLog(meditationLog: MeditationLogEntity)

    @Query("DELETE FROM meditation_logs WHERE id = :id")
    suspend fun deleteMeditationLogById(id: Long)
}