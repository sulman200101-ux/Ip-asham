package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SingingStudioScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.screens.VoiceStudioScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.data.api.GeminiApiClient.init(applicationContext)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: StudioViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.HOME
                ) {
                    composable(NavRoutes.HOME) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToVoice = { navController.navigate(NavRoutes.VOICE_STUDIO) },
                            onNavigateToSinging = { navController.navigate(NavRoutes.SINGING_STUDIO) },
                            onNavigateToVideo = { navController.navigate(NavRoutes.VIDEO_STUDIO) },
                            onNavigateToGallery = { navController.navigate(NavRoutes.GALLERY) }
                        )
                    }

                    composable(NavRoutes.VOICE_STUDIO) {
                        VoiceStudioScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                viewModel.stopAudio()
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(NavRoutes.SINGING_STUDIO) {
                        SingingStudioScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                viewModel.stopAudio()
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(NavRoutes.VIDEO_STUDIO) {
                        VideoStudioScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                viewModel.stopAudio()
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(NavRoutes.GALLERY) {
                        GalleryScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                viewModel.stopAudio()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
