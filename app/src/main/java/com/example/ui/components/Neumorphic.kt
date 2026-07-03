package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

/**
 * A custom modifier that provides a pure visual tactile press scaling effect.
 * Safe to combine with standard Compose click listeners (clickable, buttons, etc.).
 */
@Composable
fun Modifier.pressScale(): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_scale"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    try {
                        tryAwaitRelease()
                    } finally {
                        isPressed = false
                    }
                }
            )
        }
}

/**
 * A custom modifier that provides a premium tactile press bounce animation.
 * Mimics high-end hover/interaction animations with custom damping ratio and spring stiffness.
 */
@Composable
fun Modifier.bounceClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tactile_bounce"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(
                onPress = {
                    isPressed = true
                    try {
                        tryAwaitRelease()
                    } finally {
                        isPressed = false
                    }
                },
                onTap = { onClick() }
            )
        }
}

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    isSunken: Boolean = false,
    isGlassy: Boolean = false, // Translucent 3D glassy neumorphic card
    backgroundColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    
    // Base surface/card background color
    val baseColor = backgroundColor ?: if (isGlassy) {
        if (isDark) {
            Color(0x332C2C2E) // Translucent dark glass
        } else {
            Color(0x66FFFFFF) // Translucent light white glass
        }
    } else if (isDark) {
        MaterialTheme.colorScheme.surface
    } else {
        SoftBackground // Off-white F5F5F7 matches the background exactly for soft neumorphism!
    }

    val darkShadow = if (isDark) DarkThemeDarkShadow else DarkShadowColor
    val lightShadow = if (isDark) DarkThemeLightShadow else LightShadowColor

    if (isSunken) {
        // Sunken/recessed look
        Box(
            modifier = modifier
                .background(
                    color = if (isDark) DarkBackground else Color(0xFFEBEBEF),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF121212) else Color(0xFFD2D2D7),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clip(RoundedCornerShape(cornerRadius))
        ) {
            content()
        }
    } else {
        // Elegant floating/extruded soft neumorphism
        Box(
            modifier = modifier
        ) {
            // Dark Shadow: offset bottom-right
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 6.dp, y = 6.dp)
                    .background(
                        color = darkShadow.copy(alpha = if (isDark) 0.5f else 0.4f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            )
            // Light Shadow: offset top-left
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = (-6).dp, y = (-6).dp)
                    .background(
                        color = lightShadow.copy(alpha = if (isDark) 0.12f else 0.85f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            )
            
            // Main content card
            if (isGlassy) {
                // Highly polished 3D glassy appearance with bevel gradients
                val bevelBrush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    }
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = baseColor,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .border(
                            width = 1.2.dp,
                            brush = bevelBrush,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .clip(RoundedCornerShape(cornerRadius))
                ) {
                    content()
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(
                            color = baseColor,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .border(
                            width = 0.5.dp,
                            color = if (isDark) Color(0xFF3A3A3C) else Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .clip(RoundedCornerShape(cornerRadius))
                ) {
                    content()
                }
            }
        }
    }
}
