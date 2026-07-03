package com.example.ui.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.data.alarm.Alarm
import com.example.data.alarm.AlarmScheduler
import com.example.data.database.AppDatabase
import com.example.data.history.HistoryItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class FocusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val alarmDao = db.alarmDao()
    private val historyDao = db.historyDao()
    private val alarmScheduler = AlarmScheduler(application)

    // ==========================================
    // ALARMS STATE
    // ==========================================
    val alarms: StateFlow<List<Alarm>> = alarmDao.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val id = alarmDao.insertAlarm(alarm).toInt()
            val newAlarm = alarm.copy(id = id)
            if (newAlarm.isEnabled) {
                alarmScheduler.schedule(newAlarm)
            }
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmDao.updateAlarm(alarm)
            if (alarm.isEnabled) {
                alarmScheduler.schedule(alarm)
            } else {
                alarmScheduler.cancel(alarm)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm)
            alarmDao.deleteAlarm(alarm)
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        val updated = alarm.copy(isEnabled = !alarm.isEnabled)
        updateAlarm(updated)
    }

    // ==========================================
    // HISTORY STATE
    // ==========================================
    val history: StateFlow<List<HistoryItem>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addHistoryItem(type: String, durationMs: Long, details: String) {
        viewModelScope.launch {
            historyDao.insertHistoryItem(
                HistoryItem(type = type, durationMs = durationMs, details = details)
            )
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }

    // ==========================================
    // SETTINGS STATE
    // ==========================================
    private val prefs = application.getSharedPreferences("focus_timer_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "LIGHT") ?: "LIGHT")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _defaultTimerDuration = MutableStateFlow(prefs.getInt("default_timer_duration", 8))
    val defaultTimerDuration: StateFlow<Int> = _defaultTimerDuration.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean("vibration_enabled", true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _ecoModeDefault = MutableStateFlow(prefs.getBoolean("eco_mode_default", false))
    val ecoModeDefault: StateFlow<Boolean> = _ecoModeDefault.asStateFlow()

    fun updateThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
    }

    fun updateDefaultTimerDuration(mins: Int) {
        _defaultTimerDuration.value = mins
        prefs.edit().putInt("default_timer_duration", mins).apply()
    }

    fun updateSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        _vibrationEnabled.value = enabled
        prefs.edit().putBoolean("vibration_enabled", enabled).apply()
    }

    fun updateEcoModeDefault(enabled: Boolean) {
        _ecoModeDefault.value = enabled
        prefs.edit().putBoolean("eco_mode_default", enabled).apply()
        // Synchronize with current timer session if not running
        if (!_timerIsRunning.value) {
            _timerEcoMode.value = enabled
        }
    }

    // ==========================================
    // STOPWATCH STATE & LOGIC
    // ==========================================
    private var stopwatchJob: Job? = null
    private var stopwatchStartTime = 0L
    private var stopwatchAccumulatedTime = 0L

    private val _stopwatchElapsedTime = MutableStateFlow(0L)
    val stopwatchElapsedTime: StateFlow<Long> = _stopwatchElapsedTime.asStateFlow()

    private val _stopwatchIsRunning = MutableStateFlow(false)
    val stopwatchIsRunning: StateFlow<Boolean> = _stopwatchIsRunning.asStateFlow()

    // Laps lists
    data class LapInfo(
        val number: Int,
        val lapTimeMs: Long,
        val deltaMs: Long? = null // difference compared to previous lap
    )

    private val _stopwatchLaps = MutableStateFlow<List<LapInfo>>(emptyList())
    val stopwatchLaps: StateFlow<List<LapInfo>> = _stopwatchLaps.asStateFlow()

    val stopwatchAverageLapTime: StateFlow<Double> = _stopwatchLaps
        .map { laps ->
            if (laps.isEmpty()) 0.0
            else laps.map { it.lapTimeMs }.average()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun startStopwatch() {
        if (_stopwatchIsRunning.value) {
            // Pause
            _stopwatchIsRunning.value = false
            stopwatchJob?.cancel()
            stopwatchAccumulatedTime += SystemClock.elapsedRealtime() - stopwatchStartTime
        } else {
            // Start
            _stopwatchIsRunning.value = true
            stopwatchStartTime = SystemClock.elapsedRealtime()
            stopwatchJob = viewModelScope.launch(Dispatchers.Main) {
                while (isActive) {
                    val currentElapsed = (SystemClock.elapsedRealtime() - stopwatchStartTime) + stopwatchAccumulatedTime
                    _stopwatchElapsedTime.value = currentElapsed
                    delay(10) // Smooth millisecond updates
                }
            }
        }
    }

    fun recordStopwatchLap() {
        val totalElapsed = _stopwatchElapsedTime.value
        val lapsCount = _stopwatchLaps.value.size
        
        // Sum of previous laps
        val prevLapsSum = _stopwatchLaps.value.sumOf { it.lapTimeMs }
        val currentLapTime = totalElapsed - prevLapsSum

        val delta = if (lapsCount > 0) {
            val prevLapTime = _stopwatchLaps.value.first().lapTimeMs
            currentLapTime - prevLapTime
        } else {
            null
        }

        val newLap = LapInfo(
            number = lapsCount + 1,
            lapTimeMs = currentLapTime,
            deltaMs = delta
        )

        _stopwatchLaps.value = listOf(newLap) + _stopwatchLaps.value
    }

    fun resetStopwatch() {
        val finalTime = _stopwatchElapsedTime.value
        val lapCount = _stopwatchLaps.value.size
        
        if (finalTime > 0) {
            addHistoryItem(
                type = "STOPWATCH",
                durationMs = finalTime,
                details = "Stopwatch run recorded with $lapCount lap(s)."
            )
        }

        stopwatchJob?.cancel()
        _stopwatchIsRunning.value = false
        _stopwatchElapsedTime.value = 0L
        stopwatchAccumulatedTime = 0L
        _stopwatchLaps.value = emptyList()
    }

    // ==========================================
    // TIMER STATE & LOGIC
    // ==========================================
    private var timerJob: Job? = null
    
    private val _timerIsRunning = MutableStateFlow(false)
    val timerIsRunning: StateFlow<Boolean> = _timerIsRunning.asStateFlow()

    private val _timerDurationSecs = MutableStateFlow(8 * 60L) // Default 8 mins in secs
    val timerDurationSecs: StateFlow<Long> = _timerDurationSecs.asStateFlow()

    private val _timerRemainingSecs = MutableStateFlow(8 * 60L)
    val timerRemainingSecs: StateFlow<Long> = _timerRemainingSecs.asStateFlow()

    private val _timerEcoMode = MutableStateFlow(prefs.getBoolean("eco_mode_default", false))
    val timerEcoMode: StateFlow<Boolean> = _timerEcoMode.asStateFlow()

    private val _timerEstEndTimeStr = MutableStateFlow("")
    val timerEstEndTimeStr: StateFlow<String> = _timerEstEndTimeStr.asStateFlow()

    init {
        // Initialize estimated end time
        recalculateEstEndTime()
        // Seed default alarms if database is empty or needs updated seeding
        viewModelScope.launch {
            val seeded = prefs.getBoolean("alarms_preset_v3_seeded", false)
            if (!seeded) {
                // Clear any old alarms to start fresh with perfect default alarms
                val existing = alarmDao.getAllAlarms().first()
                existing.forEach { alarm ->
                    alarmScheduler.cancel(alarm)
                    alarmDao.deleteAlarm(alarm)
                }

                val defaultAlarms = listOf(
                    Alarm(
                        hour = 6,
                        minute = 30,
                        customLabel = "Morning Yoga",
                        days = "Weekdays",
                        isEnabled = true,
                        soundName = "Cosmic Bell",
                        isVibrationEnabled = true,
                        snoozeMinutes = 5
                    ),
                    Alarm(
                        hour = 8,
                        minute = 0,
                        customLabel = "Weekend Sleep",
                        days = "Weekends",
                        isEnabled = false,
                        soundName = "Zen Lotus",
                        isVibrationEnabled = true,
                        snoozeMinutes = 5
                    ),
                    Alarm(
                        hour = 23,
                        minute = 15,
                        customLabel = "Night Routine",
                        days = "Weekdays",
                        isEnabled = true,
                        soundName = "Ocean Waves",
                        isVibrationEnabled = true,
                        snoozeMinutes = 5
                    )
                )

                for (alarm in defaultAlarms) {
                    val id = alarmDao.insertAlarm(alarm).toInt()
                    val savedAlarm = alarm.copy(id = id)
                    if (savedAlarm.isEnabled) {
                        alarmScheduler.schedule(savedAlarm)
                    }
                }
                prefs.edit().putBoolean("alarms_preset_v3_seeded", true).apply()
            }
        }
    }

    fun toggleTimerEcoMode() {
        _timerEcoMode.value = !_timerEcoMode.value
    }

    fun setTimerDuration(minutes: Int, seconds: Int) {
        val totalSecs = (minutes * 60 + seconds).toLong()
        _timerDurationSecs.value = totalSecs
        if (!_timerIsRunning.value) {
            _timerRemainingSecs.value = totalSecs
            recalculateEstEndTime()
        }
    }

    fun startTimer() {
        if (_timerIsRunning.value) {
            // Pause
            _timerIsRunning.value = false
            timerJob?.cancel()
        } else {
            if (_timerRemainingSecs.value <= 0) {
                _timerRemainingSecs.value = _timerDurationSecs.value
            }
            
            _timerIsRunning.value = true
            recalculateEstEndTime()

            timerJob = viewModelScope.launch(Dispatchers.Main) {
                while (isActive && _timerRemainingSecs.value > 0) {
                    delay(1000)
                    _timerRemainingSecs.value -= 1
                    recalculateEstEndTime()
                }

                if (_timerRemainingSecs.value == 0L) {
                    onTimerCompleted()
                }
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerIsRunning.value = false
        _timerRemainingSecs.value = _timerDurationSecs.value
        recalculateEstEndTime()
    }

    private fun recalculateEstEndTime() {
        val futureTime = System.currentTimeMillis() + (_timerRemainingSecs.value * 1000)
        val sdf = SimpleDateFormat("HH:mm a", Locale.getDefault())
        _timerEstEndTimeStr.value = sdf.format(Date(futureTime))
    }

    private fun onTimerCompleted() {
        _timerIsRunning.value = false
        val originalDuration = _timerDurationSecs.value
        
        // Log to history
        addHistoryItem(
            type = "TIMER",
            durationMs = originalDuration * 1000,
            details = "Focus session of ${originalDuration / 60}m completed successfully."
        )

        // Trigger alarm alert (notification, sound, vibration)
        triggerTimerCompletedNotification()
    }

    private fun triggerTimerCompletedNotification() {
        val context = getApplication<Application>()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "timer_completed_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Completion",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when your focus timer completes"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Focus Session Complete!")
            .setContentText("Great job staying focused. Take a well-deserved break.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .build()

        notificationManager.notify(999, notification)

        // Vibrate if allowed in settings
        if (_vibrationEnabled.value) {
            try {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }

                vibrator?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(1000)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
