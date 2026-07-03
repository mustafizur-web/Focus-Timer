package com.example.data.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val customLabel: String,
    val days: String, // "Once", "Daily", "Weekdays", "Weekends", or comma-separated days
    val isEnabled: Boolean = true,
    val soundName: String = "Cosmic Bell", // e.g., "Cosmic Bell", "Zen Lotus", "Ocean Waves", "Digital Pulse"
    val isVibrationEnabled: Boolean = true,
    val snoozeMinutes: Int = 5 // Snooze in minutes (0 means disabled)
) : Serializable {
    
    fun getFormattedTime(): String {
        val h = if (hour == 0 || hour == 12) 12 else hour % 12
        val amPm = if (hour < 12) "AM" else "PM"
        return String.format("%02d:%02d %s", h, minute, amPm)
    }

    fun getDisplayTimeOnly(): String {
        val h = if (hour == 0 || hour == 12) 12 else hour % 12
        return String.format("%02d:%02d", h, minute)
    }

    fun getAmPm(): String {
        return if (hour < 12) "AM" else "PM"
    }

    fun getSubtitle(): String {
        val dayStr = when (days) {
            "Once" -> "Once"
            "Daily" -> "Daily"
            "Weekdays" -> "Weekdays"
            "Weekends" -> "Weekends"
            else -> days
        }
        return if (customLabel.isNotEmpty()) {
            "$dayStr • $customLabel"
        } else {
            dayStr
        }
    }

    fun isDayActive(dayOfWeek: Int): Boolean {
        return when (days) {
            "Once" -> true
            "Daily" -> true
            "Weekdays" -> dayOfWeek != java.util.Calendar.SATURDAY && dayOfWeek != java.util.Calendar.SUNDAY
            "Weekends" -> dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY
            else -> {
                val lower = days.lowercase()
                when (dayOfWeek) {
                    java.util.Calendar.MONDAY -> lower.contains("mon") || lower.contains("monday")
                    java.util.Calendar.TUESDAY -> lower.contains("tue") || lower.contains("tuesday")
                    java.util.Calendar.WEDNESDAY -> lower.contains("wed") || lower.contains("wednesday")
                    java.util.Calendar.THURSDAY -> lower.contains("thu") || lower.contains("thursday")
                    java.util.Calendar.FRIDAY -> lower.contains("fri") || lower.contains("friday")
                    java.util.Calendar.SATURDAY -> lower.contains("sat") || lower.contains("saturday")
                    java.util.Calendar.SUNDAY -> lower.contains("sun") || lower.contains("sunday")
                    else -> false
                }
            }
        }
    }

    fun getNextTriggerTimeMs(): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        val now = java.util.Calendar.getInstance()
        if (calendar.timeInMillis <= now.timeInMillis) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        if (days == "Once" || days == "Daily") {
            return calendar.timeInMillis
        }

        // Loop up to 7 days to find the next active day
        for (i in 0..7) {
            val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
            if (isDayActive(dayOfWeek)) {
                return calendar.timeInMillis
            }
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }
}
