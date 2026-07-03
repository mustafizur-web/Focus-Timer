package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.alarm.Alarm
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.pressScale
import com.example.ui.viewmodel.FocusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val alarms by viewModel.alarms.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedAlarm by remember { mutableStateOf<Alarm?>(null) }
    var tick by remember { mutableStateOf(0L) }

    val context = androidx.compose.ui.platform.LocalContext.current
    var hasOverlayPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                android.provider.Settings.canDrawOverlays(context)
            } else {
                true
            }
        )
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasOverlayPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    android.provider.Settings.canDrawOverlays(context)
                } else {
                    true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            tick = System.currentTimeMillis()
            kotlinx.coroutines.delay(10000) // Update countdown precisely every 10 seconds
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Screen Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Alarms",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage your wake-up routines",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (!hasOverlayPermission) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Full Screen Alarm Permission",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Text(
                            text = "To allow the alarm to ring immediately on your screen instead of only showing in the background, please enable \"Display over other apps\".",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        )
                        Button(
                            onClick = {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    val intent = android.content.Intent(
                                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        android.net.Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Grant Permission", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (alarms.isEmpty()) {
                // Empty State Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AlarmOff,
                            contentDescription = "No Alarms",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No Alarms Created",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Tap the + button below to add a reminder.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                // Alarm list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(alarms) { alarm ->
                        val itemAlpha by animateFloatAsState(
                            targetValue = if (alarm.isEnabled) 1.0f else 0.55f,
                            animationSpec = tween(durationMillis = 200),
                            label = "alarm_alpha"
                        )

                        NeumorphicCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer { alpha = itemAlpha }
                                .pressScale()
                                .clickable {
                                    selectedAlarm = alarm
                                    showAddEditDialog = true
                                },
                            cornerRadius = 24.dp,
                            isGlassy = false
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            text = alarm.getDisplayTimeOnly(),
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 32.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = alarm.getAmPm(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                    }
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = alarm.getSubtitle(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        
                                        DaysOfWeekIndicator(days = alarm.days)
                                    }

                                    if (alarm.isEnabled) {
                                        val remainingTimeText = remember(alarm, tick) { getRemainingTimeText(alarm) }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Alarm,
                                                contentDescription = "Time remaining",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = remainingTimeText,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    // Professional Decorated Badges (Sound, Vibration, Snooze)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Sound badge
                                        SuggestionChip(
                                            onClick = {},
                                            label = {
                                                Text(
                                                    alarm.soundName,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            },
                                            shape = CircleShape,
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                iconContentColor = MaterialTheme.colorScheme.primary
                                            ),
                                            border = null
                                        )

                                        // Vibration badge
                                        if (alarm.isVibrationEnabled) {
                                            SuggestionChip(
                                                onClick = {},
                                                label = {
                                                    Text(
                                                        "Vibrate",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                },
                                                icon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Vibration,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                },
                                                shape = CircleShape,
                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    iconContentColor = MaterialTheme.colorScheme.secondary
                                                ),
                                                border = null
                                            )
                                        }

                                        // Snooze badge
                                        SuggestionChip(
                                            onClick = {},
                                            label = {
                                                Text(
                                                    if (alarm.snoozeMinutes > 0) "${alarm.snoozeMinutes}m Snooze" else "Snooze Off",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            shape = CircleShape,
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                            ),
                                            border = null
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PremiumAlarmToggle(
                                        checked = alarm.isEnabled,
                                        onCheckedChange = { viewModel.toggleAlarm(alarm) },
                                        modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                                    )

                                    // Quick Delete Button
                                    IconButton(
                                        onClick = { viewModel.deleteAlarm(alarm) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Alarm",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Alarm (with tactile scale effect)
        FloatingActionButton(
            onClick = {
                selectedAlarm = null
                showAddEditDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
                .pressScale()
                .testTag("add_alarm_fab"),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Alarm",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    // Add / Edit Alarm Dialog (Enhanced and highly decorated)
    if (showAddEditDialog) {
        var hour by remember(selectedAlarm) { mutableStateOf(selectedAlarm?.hour ?: 7) }
        var minute by remember(selectedAlarm) { mutableStateOf(selectedAlarm?.minute ?: 0) }
        var customLabel by remember(selectedAlarm) { mutableStateOf(selectedAlarm?.customLabel ?: "") }
        var days by remember(selectedAlarm) { mutableStateOf(selectedAlarm?.days ?: "Weekdays") }
        
        // Premium fields
        var soundName by remember(selectedAlarm) { mutableStateOf(selectedAlarm?.soundName ?: "Cosmic Bell") }
        var isVibrationEnabled by remember(selectedAlarm) { mutableStateOf(selectedAlarm?.isVibrationEnabled ?: true) }
        var snoozeMinutes by remember(selectedAlarm) { mutableStateOf(selectedAlarm?.snoozeMinutes ?: 5) }

        val repeatOptions = listOf("Once", "Daily", "Weekdays", "Weekends", "Saturday", "Sunday")
        val soundOptions = listOf("Cosmic Bell", "Zen Lotus", "Ocean Waves", "Digital Pulse")
        val snoozeOptions = listOf(0, 5, 10, 15)

        // Lists for the wheel scroll picker
        val hour12List = (1..12).map { it.toString() }
        val minuteList = (0..59).map { String.format("%02d", it) }
        val amPmList = listOf("AM", "PM")

        val initialHour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val initialIsPm = hour >= 12

        var selectedHour12Index by remember(selectedAlarm) { mutableStateOf(initialHour12 - 1) }
        var selectedMinuteIndex by remember(selectedAlarm) { mutableStateOf(minute) }
        var selectedAmPmIndex by remember(selectedAlarm) { mutableStateOf(if (initialIsPm) 1 else 0) }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            shape = RoundedCornerShape(28.dp),
            title = { 
                Text(
                    text = if (selectedAlarm == null) "New Alarm" else "Edit Alarm",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                ) 
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Premium Scrollable Time Picker Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scroll to Set Time",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Hour Wheel Column
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "HOUR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                ScrollableWheelPicker(
                                    items = hour12List,
                                    selectedIndex = selectedHour12Index,
                                    onItemSelected = { idx ->
                                        selectedHour12Index = idx
                                        val finalHour12 = hour12List[idx].toInt()
                                        val finalIsPm = selectedAmPmIndex == 1
                                        hour = if (finalIsPm) {
                                            if (finalHour12 == 12) 12 else finalHour12 + 12
                                        } else {
                                            if (finalHour12 == 12) 0 else finalHour12
                                        }
                                    }
                                )
                            }

                            // Separator
                            Text(
                                text = ":",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 18.dp)
                            )

                            // Minute Wheel Column
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "MINUTE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                ScrollableWheelPicker(
                                    items = minuteList,
                                    selectedIndex = selectedMinuteIndex,
                                    onItemSelected = { idx ->
                                        selectedMinuteIndex = idx
                                        minute = idx
                                    }
                                )
                            }

                            // AM / PM Wheel Column
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "AM/PM",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                ScrollableWheelPicker(
                                    items = amPmList,
                                    selectedIndex = selectedAmPmIndex,
                                    onItemSelected = { idx ->
                                        selectedAmPmIndex = idx
                                        val finalHour12 = hour12List[selectedHour12Index].toInt()
                                        val finalIsPm = idx == 1
                                        hour = if (finalIsPm) {
                                            if (finalHour12 == 12) 12 else finalHour12 + 12
                                        } else {
                                            if (finalHour12 == 12) 0 else finalHour12
                                        }
                                    }
                                )
                            }
                        }
                    }
 
                    // Label Input Field
                    OutlinedTextField(
                        value = customLabel,
                        onValueChange = { customLabel = it },
                        label = { Text("Label (e.g. Yoga, Meds)") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
 
                    // Repeating Days Selector
                    Column {
                        Text(
                            "Repeat Schedule", 
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            repeatOptions.forEach { option ->
                                val isSelected = days == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { days = option },
                                    label = { Text(option, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Sound Selection (New Option)
                    Column {
                        Text(
                            "Alarm Sound Theme", 
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            soundOptions.forEach { option ->
                                val isSelected = soundName == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { soundName = option },
                                    label = { Text(option, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Snooze Picker (New Option)
                    Column {
                        Text(
                            "Snooze Duration", 
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            snoozeOptions.forEach { option ->
                                val isSelected = snoozeMinutes == option
                                val labelText = if (option == 0) "Off" else "${option}m"
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { snoozeMinutes = option },
                                    label = { Text(labelText, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Vibration Switch (New Option)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    "Vibrate on Alarm",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Add rhythmic pulses to alerts",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Switch(
                            checked = isVibrationEnabled,
                            onCheckedChange = { isVibrationEnabled = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val alarm = Alarm(
                            id = selectedAlarm?.id ?: 0,
                            hour = hour,
                            minute = minute,
                            customLabel = customLabel,
                            days = days,
                            isEnabled = selectedAlarm?.isEnabled ?: true,
                            soundName = soundName,
                            isVibrationEnabled = isVibrationEnabled,
                            snoozeMinutes = snoozeMinutes
                        )
                        if (selectedAlarm == null) {
                            viewModel.insertAlarm(alarm)
                        } else {
                            viewModel.updateAlarm(alarm)
                        }
                        showAddEditDialog = false
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddEditDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}

@Composable
fun PremiumAlarmToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium),
        label = "ThumbOffset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0),
        animationSpec = tween(durationMillis = 200),
        label = "TrackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = Color.White,
        animationSpec = tween(durationMillis = 200),
        label = "ThumbColor"
    )

    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(trackColor, CircleShape)
            .then(
                if (!checked) {
                    Modifier.border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(24.dp)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .background(thumbColor, CircleShape)
        )
    }
}

@Composable
fun ScrollableWheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val coroutineScope = rememberCoroutineScope()
    
    // Smooth auto-centering when dragging stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val center = layoutInfo.viewportEndOffset / 2
                val closest = visibleItems.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - center) }
                closest?.let {
                    listState.animateScrollToItem(it.index)
                    onItemSelected(it.index)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .height(130.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Highlight middle row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 44.dp), // Height 130.dp -> padding 44.dp leaves exactly 42.dp center
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = listState.firstVisibleItemIndex == index
                
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                                onItemSelected(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (isSelected) 20.sp else 15.sp
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekIndicator(days: String, modifier: Modifier = Modifier) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    val activeDays = when (days) {
        "Daily" -> listOf(true, true, true, true, true, true, true)
        "Weekdays" -> listOf(true, true, true, true, true, false, false)
        "Weekends" -> listOf(false, false, false, false, false, true, true)
        "Once" -> listOf(false, false, false, false, false, false, false)
        else -> {
            val lower = days.lowercase()
            listOf(
                lower.contains("mon") || lower.contains("m"),
                lower.contains("tue") || lower.contains("t"),
                lower.contains("wed") || lower.contains("w"),
                lower.contains("thu") || lower.contains("t"),
                lower.contains("fri") || lower.contains("f"),
                lower.contains("sat") || lower.contains("s"),
                lower.contains("sun") || lower.contains("s")
            )
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dayLabels.forEachIndexed { index, label ->
            val isActive = activeDays.getOrElse(index) { false }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.width(14.dp),
                style = androidx.compose.ui.text.TextStyle(
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )
        }
    }
}

fun getRemainingTimeText(alarm: Alarm): String {
    val nextTrigger = alarm.getNextTriggerTimeMs()
    val diffMs = nextTrigger - System.currentTimeMillis()
    if (diffMs <= 0) return "Ringing now"

    val diffSeconds = diffMs / 1000
    val diffMinutes = diffSeconds / 60
    val diffHours = diffMinutes / 60
    val days = diffHours / 24

    val remainingHours = diffHours % 24
    val remainingMinutes = diffMinutes % 60

    return buildString {
        append("Rings in ")
        if (days > 0) {
            append("${days}d ")
        }
        if (remainingHours > 0 || days > 0) {
            append("${remainingHours}h ")
        }
        append("${remainingMinutes}m")
    }
}
