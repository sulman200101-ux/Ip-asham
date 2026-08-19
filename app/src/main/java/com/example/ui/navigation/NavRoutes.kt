package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object LevelsSelect : Screen("levels_select")
    object PuzzleGame : Screen("puzzle_game/{levelId}") {
        fun createRoute(levelId: Int) = "puzzle_game/$levelId"
    }
    object SandboxStudio : Screen("sandbox_studio")
    object PhysicsTower : Screen("physics_tower")
    object MemoryMatch : Screen("memory_match")
    object TrophyRoom : Screen("trophy_room")
}
