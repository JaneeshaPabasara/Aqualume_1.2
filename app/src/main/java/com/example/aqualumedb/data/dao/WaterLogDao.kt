package com.example.aqualumedb.data.dao

import androidx.room.*
import com.example.aqualumedb.data.entities.WaterLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllWaterLogs(): Flow<List<WaterLogEntity>>

    @Query("SELECT * FROM water_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getWaterLogsByDateRange(startTime: Long, endTime: Long): Flow<List<WaterLogEntity>>

    @Query("SELECT * FROM water_logs WHERE id = :id")
    suspend fun getWaterLogById(id: Long): WaterLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLogEntity): Long

    @Update
    suspend fun updateWaterLog(waterLog: WaterLogEntity)

    @Delete
    suspend fun deleteWaterLog(waterLog: WaterLogEntity)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLogById(id: Long)
}