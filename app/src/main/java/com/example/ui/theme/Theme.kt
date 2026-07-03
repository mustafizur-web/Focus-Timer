package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// CompositionLocal to securely share dark mode state with components (e.g., NeumorphicCard)
val LocalIsDarkTheme = staticCompositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    secondary = SecondaryGray,
    outline = OutlineGray,
    error = ErrorRed,
    surfaceVariant = Color(0xFF2C2C2E)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    background = SoftBackground,
    surface = SoftBackground,
    onPrimary = Color.White,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    secondary = SecondaryGray,
    outline = OutlineGray,
    outlineVariant = OutlineVariantGray,
    error = ErrorRed,
    surfaceVariant = Color(0xFFE9E7EB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
