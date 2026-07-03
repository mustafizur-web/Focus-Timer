package com.example.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val alarmLabel = intent.getStringExtra("ALARM_LABEL") ?: "Wake Up!"
        val alarmTime = intent.getStringExtra("ALARM_TIME") ?: ""
        val snoozeMinutes = intent.getIntExtra("SNOOZE_MINUTES", 5)

        // Handle Dismiss Action
        if (action == "ACTION_DISMISS") {
            AlarmRingingManager.dismiss(context)
            return
        }

        // Handle Snooze Action
        if (action == "ACTION_SNOOZE") {
            AlarmRingingManager.snooze(context)
            return
        }

        // --- Trigger Alarm Ringing ---
        
        // Reschedule recurring alarms or disable single-occurrence ("Once") alarms
        if (alarmId != -1) {
            val db = com.example.data.database.AppDatabase.getDatabase(context)
            val alarmDao = db.alarmDao()
            val alarmScheduler = AlarmScheduler(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val alarmsList = alarmDao.getAllAlarms().first()
                    val alarm = alarmsList.find { it.id == alarmId }
                    if (alarm != null) {
                        if (alarm.days == "Once") {
                            alarmDao.updateAlarm(alarm.copy(isEnabled = false))
                        } else {
                            alarmScheduler.schedule(alarm)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        // 1. Start Audio & Vibration via AlarmRingingManager
        AlarmRingingManager.startRinging(context, alarmId, alarmLabel, alarmTime, snoozeMinutes)

        // 2. Build intent to launch MainActivity
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("RINGING_ALARM_ID", alarmId)
            putExtra("RINGING_ALARM_LABEL", alarmLabel)
            putExtra("RINGING_ALARM_TIME", alarmTime)
            putExtra("RINGING_SNOOZE_MINUTES", snoozeMinutes)
        }

        val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            android.app.ActivityOptions.makeBasic().apply {
                setPendingIntentBackgroundActivityStartMode(android.app.ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
            }
        } else {
            null
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            options?.toBundle()
        )

        // 3. Create Notification Channel if SDK 26+
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Focus Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channels for wake-up alarms and reminders"
                enableVibration(true)
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 4. Build notification with Dismiss/Snooze action buttons and setFullScreenIntent
        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = "ACTION_DISMISS"
            putExtra("ALARM_ID", alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId + 30000,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = "ACTION_SNOOZE"
            putExtra("ALARM_ID", alarmId)
            putExtra("ALARM_LABEL", alarmLabel)
            putExtra("SNOOZE_MINUTES", snoozeMinutes)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId + 40000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Focus Alarm: $alarmTime")
            .setContentText(alarmLabel)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true) // High Priority: Pops up on screen when locked or active
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)

        if (snoozeMinutes > 0) {
            notificationBuilder.addAction(android.R.drawable.ic_popup_sync, "Snooze ($snoozeMinutes m)", snoozePendingIntent)
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(alarmId, notification)

        // 5. Try starting the activity immediately
        try {
            context.startActivity(launchIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun stopPlayingRingtone() {
            AlarmRingingManager.stopRinging()
        }
    }
}
