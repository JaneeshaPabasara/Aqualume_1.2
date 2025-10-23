package com.example.aqualumedb.data.dao

import androidx.room.*
import com.example.aqualumedb.data.entities.CustomTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomTaskDao {
    @Query("SELECT * FROM custom_tasks ORDER BY timestamp DESC")
    fun getAllCustomTasks(): Flow<List<CustomTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomTask(customTask: CustomTaskEntity): Long

    @Update
    suspend fun updateCustomTask(customTask: CustomTaskEntity)

    @Delete
    suspend fun deleteCustomTask(customTask: CustomTaskEntity)

    @Query("DELETE FROM custom_tasks WHERE id = :id")
    suspend fun deleteCustomTaskById(id: Long)
}