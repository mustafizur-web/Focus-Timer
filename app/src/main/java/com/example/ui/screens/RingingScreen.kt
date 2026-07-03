package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.alarm.AlarmRingingManager
import kotlinx.coroutines.delay

@Composable
fun RingingScreen(
    onDismiss: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alarmLabel = AlarmRingingManager.ringingAlarmLabel
    val alarmTime = AlarmRingingManager.ringingAlarmTime
    val snoozeMinutes = AlarmRingingManager.snoozeMinutes

    var secondsElapsed by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        val start = AlarmRingingManager.startTimeMs
        while (true) {
            val now = System.currentTimeMillis()
            secondsElapsed = ((now - start) / 1000).toInt().coerceAtLeast(0)
            delay(1000)
        }
    }

    val ringTimeText = if (secondsElapsed < 60) {
        "Ringing for $secondsElapsed seconds"
    } else {
        val mins = secondsElapsed / 60
        val secs = secondsElapsed % 60
        if (secs == 0) {
            "Ringing for $mins ${if (mins == 1) "minute" else "minutes"}"
        } else {
            "Ringing for $mins m $secs s"
        }
    }

    // Gradient background: soft, inviting, pastel peach/white/blue gradient as seen in the screenshot
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEBF1FF), // Soft pastel blue at top
                        Color(0xFFFFFFFF), // White in the middle
                        Color(0xFFFFF1EB)  // Soft warm peach at the bottom
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ChronoSoft",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp
                )
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.Gray.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Circular Alarm Icon (overlapping or centered near the top)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .size(90.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = "Alarm Ringing",
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        // Content Column in the center
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Alarm custom label in spaced-out blue capitals
            Text(
                text = alarmLabel.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Big Bold Time Text
            Text(
                text = alarmTime,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 58.sp,
                    color = Color(0xFF1E293B) // Dark charcoal
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Soft Rounded Pill Badge
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFF1F5F9).copy(alpha = 0.8f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = ringTimeText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF64748B), // Slate gray
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Bottom Controls Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SNOOZE button card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clickable { onSnooze() }
                    .testTag("ringing_snooze_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Snooze",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (snoozeMinutes > 0) "$snoozeMinutes Minutes" else "Snooze Off",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // SWIPE TO STOP slider
            SwipeToStopButton(
                onSwipeComplete = onDismiss,
                modifier = Modifier.fillMaxWidth().testTag("ringing_dismiss_swipe")
            )
        }
    }
}

@Composable
fun SwipeToStopButton(
    onSwipeComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                color = Color(0xFFFFF1EB), // Soft peach/rose track background from the screenshot
                shape = CircleShape
            )
            .padding(6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val width = constraints.maxWidth
        val buttonSize = 60.dp
        val buttonSizePx = with(density) { buttonSize.toPx() }
        val maxAllowedDrag = width - buttonSizePx - with(density) { 12.dp.toPx() }

        // SWIPE TO STOP background text
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SWIPE TO STOP",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF94A3B8), // slate-400
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp
                )
            )
        }

        // Draggable Blue Button with Arrow Forward
        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffsetX.toInt(), 0) }
                .size(buttonSize)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffsetX >= maxAllowedDrag * 0.75f) {
                                // Swipe success!
                                dragOffsetX = maxAllowedDrag
                                onSwipeComplete()
                            } else {
                                // Snap back
                                dragOffsetX = 0f
                            }
                        },
                        onDragCancel = {
                            dragOffsetX = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            dragOffsetX = (dragOffsetX + dragAmount).coerceIn(0f, maxAllowedDrag)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Swipe to Stop Arrow",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
