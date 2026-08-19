package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = TextWhite,
    primaryContainer = DarkCard,
    onPrimaryContainer = NeonVioletLight,
    secondary = NeonCyan,
    onSecondary = DarkBg,
    secondaryContainer = DarkCard,
    onSecondaryContainer = NeonCyanLight,
    tertiary = SunsetPink,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextMuted,
    outline = DarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NeonViolet,
    onPrimary = TextWhite,
    primaryContainer = LightCard,
    onPrimaryContainer = NeonViolet,
    secondary = NeonCyan,
    onSecondary = TextWhite,
    secondaryContainer = LightCard,
    onSecondaryContainer = NeonCyan,
    tertiary = SunsetPink,
    background = LightBg,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightCard,
    onSurfaceVariant = TextMuted,
    outline = LightCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek futuristic dark studio theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
