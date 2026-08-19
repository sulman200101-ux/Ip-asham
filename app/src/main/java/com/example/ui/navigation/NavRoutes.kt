package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Playground : Screen("playground", "المحادثة", Icons.Filled.Chat, Icons.Outlined.Chat)
    object StrategyAndAnalysis : Screen("strategy_analysis", "الاستراتيجيات والأخبار", Icons.Filled.Analytics, Icons.Outlined.Analytics)
    object ImageStudio : Screen("image_studio", "توليد الصور", Icons.Filled.Palette, Icons.Outlined.Palette)
    object AudioStudio : Screen("audio_studio", "الصوتيات", Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq)
    object PythonSdk : Screen("python_sdk", "بايثون SDK", Icons.Filled.Code, Icons.Outlined.Code)
    object Models : Screen("models", "النماذج", Icons.Filled.Memory, Icons.Outlined.Memory)
    object Settings : Screen("settings", "الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val BottomNavScreens = listOf(
    Screen.Playground,
    Screen.StrategyAndAnalysis,
    Screen.ImageStudio,
    Screen.AudioStudio,
    Screen.PythonSdk,
    Screen.Models,
    Screen.Settings
)
