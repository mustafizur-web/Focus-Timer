package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeumorphicCard
import com.example.ui.components.pressScale
import com.example.ui.viewmodel.FocusViewModel
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StopwatchScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val elapsedTime by viewModel.stopwatchElapsedTime.collectAsState()
    val isRunning by viewModel.stopwatchIsRunning.collectAsState()
    val laps by viewModel.stopwatchLaps.collectAsState()
    val avgLapTime by viewModel.stopwatchAverageLapTime.collectAsState()

    // Formatting MM:SS.ms
    val formattedTime = remember(elapsedTime) {
        val totalSecs = elapsedTime / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        val ms = (elapsedTime % 1000) / 10
        String.format("%02d:%02d.%02d", mins, secs, ms)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Section Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Stopwatch",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Track high-precision split intervals",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Circular Dial Display
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            NeumorphicCard(
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 130.dp,
                isGlassy = false
            ) {}

            // Visual Progress Ring (just loops/animates continuously while running)
            val ringProgress = if (isRunning) {
                (elapsedTime % 3000).toFloat() / 3000f
            } else {
                0f
            }

            val progressColor = MaterialTheme.colorScheme.primary

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                drawCircle(
                    color = progressColor.copy(alpha = 0.15f),
                    style = Stroke(width = 6.dp.toPx())
                )
                if (isRunning) {
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * ringProgress,
                        useCenter = false,
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        letterSpacing = (-1).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("stopwatch_display")
                )
                Text(
                    text = "STOPWATCH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Side-by-Side Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lap Button (outlined, pill-shaped)
            OutlinedButton(
                onClick = { if (isRunning) viewModel.recordStopwatchLap() },
                enabled = isRunning,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("stopwatch_lap_button"),
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    disabledContentColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Lap",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lap", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            // Start/Pause Button (pill-shaped, premium look)
            Button(
                onClick = { viewModel.startStopwatch() },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("stopwatch_start_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isRunning) "Pause" else "Start", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Dedicated Clear/Reset action button if they don't know about long-press
        if (!isRunning && elapsedTime > 0) {
            TextButton(
                onClick = { viewModel.resetStopwatch() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Stopwatch")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset Stopwatch")
            }
        }

        // Recent Laps Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Laps",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            val formattedAvg = remember(avgLapTime) {
                val totalSecs = avgLapTime / 1000.0
                String.format("AVG: %.1fs", totalSecs)
            }
            Text(
                text = if (laps.isNotEmpty()) formattedAvg else "AVG: --",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // Lap History scrollable list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(laps) { lap ->
                val isMostRecent = laps.firstOrNull()?.number == lap.number

                // Lap card
                NeumorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left visual accent bar for most recent lap
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isMostRecent) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(36.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.shapes.small
                                        )
                                )
                            }
                            
                            Column {
                                Text(
                                    text = String.format("Lap %02d", lap.number),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                
                                val lapSecs = lap.lapTimeMs / 1000
                                val lapMins = lapSecs / 60
                                val lapSecsLeft = lapSecs % 60
                                val lapMs = (lap.lapTimeMs % 1000) / 10
                                val lapFormatted = String.format("%02d:%02d.%02d", lapMins, lapSecsLeft, lapMs)

                                Text(
                                    text = lapFormatted,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Right delta display
                        if (lap.deltaMs != null) {
                            val isFaster = lap.deltaMs < 0
                            val deltaSecs = Math.abs(lap.deltaMs) / 1000.0
                            val prefix = if (isFaster) "-" else "+"
                            val color = if (isFaster) Color(0xFFBA1A1A) else MaterialTheme.colorScheme.primary

                            Text(
                                text = String.format("%s%.1fs", prefix, deltaSecs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = color
                            )
                        } else {
                            Text(
                                text = "--",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
