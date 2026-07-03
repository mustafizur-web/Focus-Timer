package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.pressScale
import com.example.ui.viewmodel.FocusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val durationSecs by viewModel.timerDurationSecs.collectAsState()
    val remainingSecs by viewModel.timerRemainingSecs.collectAsState()
    val isRunning by viewModel.timerIsRunning.collectAsState()
    val ecoMode by viewModel.timerEcoMode.collectAsState()
    val estEndTime by viewModel.timerEstEndTimeStr.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    val progressFraction = if (durationSecs > 0) {
        remainingSecs.toFloat() / durationSecs.toFloat()
    } else {
        1f
    }

    // Animate progress smoothly unless Eco Mode is enabled!
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = if (ecoMode) tween(1000) else tween(150),
        label = "TimerProgress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Focus Timer",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Deep work sessions with custom duration",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Circular Dial Visualization
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            // Neumorphic dial card container
            NeumorphicCard(
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 140.dp,
                isGlassy = false
            ) {}

            // Circular progress rings
            val progressColor = MaterialTheme.colorScheme.primary
            val ringBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Background Track
                drawCircle(
                    color = ringBgColor,
                    style = Stroke(width = 8.dp.toPx())
                )

                // Foreground Progress ring
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Digital MM:SS format readout
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val mins = remainingSecs / 60
                val secs = remainingSecs % 60
                val timeString = String.format("%02d:%02d", mins, secs)

                Text(
                    text = timeString,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 64.sp,
                        letterSpacing = (-0.02).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("timer_display")
                )
                Text(
                    text = "FOCUS MODE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Digital Time Input Picker trigger Card
        NeumorphicCard(
            modifier = Modifier
                .width(320.dp)
                .pressScale()
                .clickable { if (!isRunning) showEditDialog = true },
            cornerRadius = 24.dp,
            isGlassy = false
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val inputMins = durationSecs / 60
                val inputSecs = durationSecs % 60

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = String.format("%02d", inputMins),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "MIN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 42.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = String.format("%02d", inputSecs),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "SEC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier.width(320.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Start/Pause Button (filled, pill-shaped)
            Button(
                onClick = { viewModel.startTimer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("timer_start_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = if (isRunning) "Pause" else "Start",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Reset Button (neumorphic pill-shape)
            NeumorphicCard(
                modifier = Modifier
                    .width(140.dp)
                    .height(48.dp)
                    .pressScale()
                    .clickable { viewModel.resetTimer() }
                    .testTag("timer_reset_button"),
                cornerRadius = 24.dp,
                isGlassy = false
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Reset",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Info Cards (Est. End & Eco Mode)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Est. End card
            NeumorphicCard(
                modifier = Modifier.weight(1f).pressScale(),
                cornerRadius = 24.dp,
                isGlassy = false
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Estimated End Time",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Est. End",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = estEndTime.ifEmpty { "14:45 PM" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Eco Mode card
            NeumorphicCard(
                modifier = Modifier
                    .weight(1f)
                    .pressScale()
                    .clickable { viewModel.toggleTimerEcoMode() },
                cornerRadius = 24.dp,
                isGlassy = false
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Eco Mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Eco Mode",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = if (ecoMode) "Enabled" else "Disabled",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Direct Time Picker Dialog
    if (showEditDialog) {
        var minInput by remember { mutableStateOf((durationSecs / 60).toString()) }
        var secInput by remember { mutableStateOf((durationSecs % 60).toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Set Duration") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = minInput,
                        onValueChange = { minInput = it.take(2) },
                        label = { Text("Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                        singleLine = true
                    )
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    OutlinedTextField(
                        value = secInput,
                        onValueChange = { secInput = it.take(2) },
                        label = { Text("Sec") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val m = minInput.toIntOrNull() ?: 0
                        val s = secInput.toIntOrNull() ?: 0
                        viewModel.setTimerDuration(m.coerceIn(0, 99), s.coerceIn(0, 59))
                        showEditDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
