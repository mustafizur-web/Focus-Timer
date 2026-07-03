package com.example.data.history

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: HistoryItem)

    @Query("DELETE FROM history")
    suspend fun clearAllHistory()

    @Delete
    suspend fun deleteHistoryItem(item: HistoryItem)
}
