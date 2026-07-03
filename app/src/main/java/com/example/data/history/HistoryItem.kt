package com.example.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "TIMER" or "STOPWATCH"
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String
) : Serializable {

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getFormattedDuration(): String {
        val totalSecs = durationMs / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        val ms = (durationMs % 1000) / 10 // For stopwatch centiseconds
        
        return if (type == "STOPWATCH") {
            String.format("%02d:%02d.%02d", mins, secs, ms)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }
}
