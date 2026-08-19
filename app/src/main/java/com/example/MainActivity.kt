package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.ads.AdMobManager
import com.example.ui.navigation.Screen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelsSelectScreen
import com.example.ui.screens.MemoryMatchScreen
import com.example.ui.screens.PhysicsTowerScreen
import com.example.ui.screens.PuzzleGameScreen
import com.example.ui.screens.SandboxStudioScreen
import com.example.ui.screens.TrophyRoomScreen
import com.example.ui.theme.SmartKidsBuilderTheme
import com.example.ui.viewmodel.KidsGameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: KidsGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AdMobManager.initialize(this)
        setContent {
            SmartKidsBuilderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SmartKidsBuilderApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SmartKidsBuilderApp(viewModel: KidsGameViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.LevelsSelect.route) {
            LevelsSelectScreen(
                viewModel = viewModel,
                onSelectLevel = { levelId ->
                    navController.navigate(Screen.PuzzleGame.createRoute(levelId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PuzzleGame.route,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
            PuzzleGameScreen(
                levelId = levelId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNextLevel = { nextId ->
                    navController.popBackStack()
                    navController.navigate(Screen.PuzzleGame.createRoute(nextId))
                }
            )
        }

        composable(Screen.SandboxStudio.route) {
            SandboxStudioScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PhysicsTower.route) {
            PhysicsTowerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MemoryMatch.route) {
            MemoryMatchScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TrophyRoom.route) {
            TrophyRoomScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
