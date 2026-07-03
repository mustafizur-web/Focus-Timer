package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FocusViewModel

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    private val viewModel: FocusViewModel by viewModels()

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleRingingIntent(intent)
    }

    private fun handleRingingIntent(intent: android.content.Intent?) {
        intent?.let {
            val alarmId = it.getIntExtra("RINGING_ALARM_ID", -1)
            if (alarmId != -1) {
                val label = it.getStringExtra("RINGING_ALARM_LABEL") ?: "Wake Up"
                val time = it.getStringExtra("RINGING_ALARM_TIME") ?: "07:00 AM"
                val snooze = it.getIntExtra("RINGING_SNOOZE_MINUTES", 5)
                
                com.example.data.alarm.AlarmRingingManager.ringingAlarmId = alarmId
                com.example.data.alarm.AlarmRingingManager.ringingAlarmLabel = label
                com.example.data.alarm.AlarmRingingManager.ringingAlarmTime = time
                com.example.data.alarm.AlarmRingingManager.snoozeMinutes = snooze
                com.example.data.alarm.AlarmRingingManager.startTimeMs = System.currentTimeMillis()
                com.example.data.alarm.AlarmRingingManager.isRinging = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Turn screen on and show when locked
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Request POST_NOTIFICATIONS permission at runtime for Android 13+ to ensure alarms trigger alerts/notifications
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        
        // Parse ringing parameters if launched from notification/receiver
        handleRingingIntent(intent)
        
        // Stop any active alarm audio immediately when the app is opened, EXCEPT if the alarm is currently ringing
        if (!com.example.data.alarm.AlarmRingingManager.isRinging) {
            com.example.data.alarm.AlarmReceiver.stopPlayingRingtone()
        }

        setContent {
            val appThemeSetting by viewModel.themeMode.collectAsState()
            val isDark = when (appThemeSetting) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                val isRinging = com.example.data.alarm.AlarmRingingManager.isRinging
                if (isRinging) {
                    RingingScreen(
                        onDismiss = { com.example.data.alarm.AlarmRingingManager.dismiss(this@MainActivity) },
                        onSnooze = { com.example.data.alarm.AlarmRingingManager.snooze(this@MainActivity) }
                    )
                } else {
                    val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Route constants
                val routeTimer = "timer"
                val routeStopwatch = "stopwatch"
                val routeAlarm = "alarm"
                val routeSettings = "settings"
                val routeHistory = "history"

                // Check if we should show the global bars
                val isSettingOrHistory = currentDestination?.route == routeSettings ||
                        currentDestination?.route == routeHistory

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentWindowInsets = WindowInsets(0.dp), // Do not consume window insets at the outer scaffold level so inner screens/appbars get status bar padding correctly
                    topBar = {
                        if (!isSettingOrHistory) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = "Focus Timer",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 20.sp
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { navController.navigate(routeSettings) },
                                        modifier = Modifier.testTag("top_bar_settings")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Settings",
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { navController.navigate(routeHistory) },
                                        modifier = Modifier.testTag("top_bar_history")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = "Activity History",
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (!isSettingOrHistory) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp,
                                modifier = Modifier.testTag("bottom_nav")
                            ) {
                                // Stopwatch Tab
                                val isStopwatchSelected = currentDestination?.hierarchy?.any { it.route == routeStopwatch } == true
                                NavigationBarItem(
                                    selected = isStopwatchSelected,
                                    onClick = {
                                        if (!isStopwatchSelected) {
                                            navController.navigate(routeStopwatch) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isStopwatchSelected) Icons.Default.Timer else Icons.Outlined.Timer,
                                            contentDescription = "Stopwatch"
                                        )
                                    },
                                    label = { Text("Stopwatch") },
                                    modifier = Modifier.testTag("tab_stopwatch")
                                )

                                // Timer Tab
                                val isTimerSelected = currentDestination?.hierarchy?.any { it.route == routeTimer } == true
                                NavigationBarItem(
                                    selected = isTimerSelected,
                                    onClick = {
                                        if (!isTimerSelected) {
                                            navController.navigate(routeTimer) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isTimerSelected) Icons.Default.HourglassFull else Icons.Outlined.HourglassEmpty,
                                            contentDescription = "Timer"
                                        )
                                    },
                                    label = { Text("Timer") },
                                    modifier = Modifier.testTag("tab_timer")
                                )

                                // Alarm Tab
                                val isAlarmSelected = currentDestination?.hierarchy?.any { it.route == routeAlarm } == true
                                NavigationBarItem(
                                    selected = isAlarmSelected,
                                    onClick = {
                                        if (!isAlarmSelected) {
                                            navController.navigate(routeAlarm) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isAlarmSelected) {
                                                // Create a solid visual fill inside the alarm clock outline when active
                                                Surface(
                                                    modifier = Modifier
                                                        .padding(top = 2.dp)
                                                        .size(13.dp),
                                                    shape = CircleShape,
                                                    color = LocalContentColor.current
                                                ) {}
                                            }
                                            Icon(
                                                imageVector = if (isAlarmSelected) Icons.Default.Alarm else Icons.Outlined.Alarm,
                                                contentDescription = "Alarm"
                                            )
                                        }
                                    },
                                    label = { Text("Alarm") },
                                    modifier = Modifier.testTag("tab_alarm")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = routeTimer, // Default start screen is Timer / Focus Mode per design priority
                        modifier = Modifier.padding(innerPadding),
                        enterTransition = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            
                            if (targetRoute == routeSettings || targetRoute == routeHistory) {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Up,
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            } else if (initialRoute == routeSettings || initialRoute == routeHistory) {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            } else {
                                val initialIndex = when (initialRoute) {
                                    routeTimer -> 0
                                    routeStopwatch -> 1
                                    routeAlarm -> 2
                                    else -> 0
                                }
                                val targetIndex = when (targetRoute) {
                                    routeTimer -> 0
                                    routeStopwatch -> 1
                                    routeAlarm -> 2
                                    else -> 0
                                }
                                val direction = if (initialIndex < targetIndex) {
                                    AnimatedContentTransitionScope.SlideDirection.Left
                                } else {
                                    AnimatedContentTransitionScope.SlideDirection.Right
                                }
                                slideIntoContainer(direction, animationSpec = tween(250)) + fadeIn(animationSpec = tween(250))
                            }
                        },
                        exitTransition = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            
                            if (initialRoute == routeSettings || initialRoute == routeHistory) {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            } else if (targetRoute == routeSettings || targetRoute == routeHistory) {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Down,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            } else {
                                val initialIndex = when (initialRoute) {
                                    routeTimer -> 0
                                    routeStopwatch -> 1
                                    routeAlarm -> 2
                                    else -> 0
                                }
                                val targetIndex = when (targetRoute) {
                                    routeTimer -> 0
                                    routeStopwatch -> 1
                                    routeAlarm -> 2
                                    else -> 0
                                }
                                val direction = if (initialIndex < targetIndex) {
                                    AnimatedContentTransitionScope.SlideDirection.Left
                                } else {
                                    AnimatedContentTransitionScope.SlideDirection.Right
                                }
                                slideOutOfContainer(direction, animationSpec = tween(250)) + fadeOut(animationSpec = tween(250))
                            }
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                        }
                    ) {
                        composable(routeTimer) {
                            TimerScreen(viewModel = viewModel)
                        }
                        composable(routeStopwatch) {
                            StopwatchScreen(viewModel = viewModel)
                        }
                        composable(routeAlarm) {
                            AlarmScreen(viewModel = viewModel)
                        }
                        composable(routeSettings) {
                            SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(routeHistory) {
                            HistoryScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

    override fun onStart() {
        super.onStart()
        if (!com.example.data.alarm.AlarmRingingManager.isRinging) {
            com.example.data.alarm.AlarmReceiver.stopPlayingRingtone()
        }
    }
}
