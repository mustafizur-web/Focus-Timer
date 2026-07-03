package com.example.data.alarm

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Calendar

object AlarmRingingManager {
    var isRinging by mutableStateOf(false)
    var ringingAlarmId by mutableStateOf(-1)
    var ringingAlarmLabel by mutableStateOf("Wake Up")
    var ringingAlarmTime by mutableStateOf("07:00 AM")
    var snoozeMinutes by mutableStateOf(5)
    var startTimeMs by mutableStateOf(0L)

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private val handler = Handler(Looper.getMainLooper())
    private var stopRunnable: Runnable? = null

    fun startRinging(context: Context, alarmId: Int, label: String, time: String, snooze: Int) {
        ringingAlarmId = alarmId
        ringingAlarmLabel = label.ifEmpty { "Wake Up" }
        ringingAlarmTime = time
        snoozeMinutes = snooze
        startTimeMs = System.currentTimeMillis()
        isRinging = true

        // Stop any currently playing ringtone/vibrator
        stopRinging()

        // 1. Play Ringtone
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        try {
            val r = RingtoneManager.getRingtone(context, soundUri)
            if (r != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    r.audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                }
                r.play()
                ringtone = r
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Start Vibration
        try {
            val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vib?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val pattern = longArrayOf(0, 500, 500)
                    it.vibrate(VibrationEffect.createWaveform(pattern, 0)) // Repeat from index 0
                } else {
                    @Suppress("DEPRECATION")
                    val pattern = longArrayOf(0, 500, 500)
                    it.vibrate(pattern, 0)
                }
                vibrator = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Auto-stop after 2 minutes for safety and device resource conservation
        stopRunnable?.let { handler.removeCallbacks(it) }
        val runnable = Runnable {
            stopRinging()
        }
        stopRunnable = runnable
        handler.postDelayed(runnable, 120000)
    }

    fun dismiss(context: Context) {
        val id = ringingAlarmId
        isRinging = false
        stopRinging()
        
        // Cancel notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    fun snooze(context: Context) {
        val id = ringingAlarmId
        val label = ringingAlarmLabel
        val snoozeMin = snoozeMinutes
        
        isRinging = false
        stopRinging()

        // Cancel notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)

        if (snoozeMin > 0) {
            val alarmScheduler = AlarmScheduler(context)
            val calendar = Calendar.getInstance().apply {
                add(Calendar.MINUTE, snoozeMin)
            }
            val snoozeAlarm = Alarm(
                id = id + 20000, // Safe unique ID range for snooze alarms
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE),
                customLabel = "Snoozed: $label",
                days = "Once",
                isEnabled = true,
                snoozeMinutes = 0 // No cascading snoozes
            )
            alarmScheduler.schedule(snoozeAlarm)
        }
    }

    fun stopRinging() {
        stopRunnable?.let {
            handler.removeCallbacks(it)
            stopRunnable = null
        }
        try {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
            ringtone = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            vibrator?.let {
                it.cancel()
            }
            vibrator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
