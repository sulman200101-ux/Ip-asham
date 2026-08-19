package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PlayfulPrimary,
    onPrimary = PlayfulOnPrimary,
    primaryContainer = Color(0xFF7F1D1D),
    onPrimaryContainer = Color(0xFFFFDADA),
    secondary = PlayfulSecondary,
    onSecondary = PlayfulOnSecondary,
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFD3E4FD),
    tertiary = PlayfulTertiary,
    onTertiary = PlayfulOnTertiary,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFFEEB2),
    background = PlayfulBackgroundDark,
    surface = PlayfulSurfaceDark,
    surfaceVariant = PlayfulSurfaceVariantDark,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
    primary = PlayfulPrimary,
    onPrimary = PlayfulOnPrimary,
    primaryContainer = PlayfulPrimaryContainer,
    onPrimaryContainer = PlayfulOnPrimaryContainer,
    secondary = PlayfulSecondary,
    onSecondary = PlayfulOnSecondary,
    secondaryContainer = PlayfulSecondaryContainer,
    onSecondaryContainer = PlayfulOnSecondaryContainer,
    tertiary = PlayfulTertiary,
    onTertiary = PlayfulOnTertiary,
    tertiaryContainer = PlayfulTertiaryContainer,
    onTertiaryContainer = PlayfulOnTertiaryContainer,
    background = PlayfulBackgroundLight,
    surface = PlayfulSurfaceLight,
    surfaceVariant = PlayfulSurfaceVariantLight,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun SmartKidsBuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use intentional colorful theme by default
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
