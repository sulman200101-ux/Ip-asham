package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OpenAIGreen,
    onPrimary = Color.Black,
    primaryContainer = OpenAIGreenDark,
    onPrimaryContainer = Color.White,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D5A),
    onSecondaryContainer = CyanAccent,
    tertiary = AmberAccent,
    onTertiary = Color.Black,
    background = ObsidianBlack,
    onBackground = ObsidianText,
    surface = ObsidianSurface,
    onSurface = ObsidianText,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = ObsidianSubtext,
    outline = ObsidianBorder,
    outlineVariant = Color(0xFF38404A)
)

private val LightColorScheme = lightColorScheme(
    primary = OpenAIGreenDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = OpenAIGreenDark,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = TechWhite,
    onBackground = TechText,
    surface = TechSurface,
    onSurface = TechText,
    surfaceVariant = TechCard,
    onSurfaceVariant = TechSubtext,
    outline = TechBorder,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep OpenAI distinctive emerald styling
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
