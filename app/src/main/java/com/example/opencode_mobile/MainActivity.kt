package com.example.opencode_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.opencode_mobile.ui.screens.ChatScreen
import com.example.opencode_mobile.ui.screens.ProjectsScreen
import com.example.opencode_mobile.ui.screens.ReviewScreen
import com.example.opencode_mobile.ui.screens.SessionsScreen
import com.example.opencode_mobile.ui.theme.Opencode_mobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Opencode_mobileTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "projects"
                ) {
                    composable("projects") {
                        ProjectsScreen(
                            onProjectClick = { projectId ->
                                navController.navigate("sessions/$projectId")
                            },
                            onBackClick = {
                                finish() // Exit app on back click from root projects screen
                            }
                        )
                    }
                    composable("sessions/{projectId}") { backStackEntry ->
                        val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                        SessionsScreen(
                            projectId = projectId,
                            onSessionClick = { sessionId ->
                                navController.navigate("chat/$sessionId")
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("chat/{sessionId}") { backStackEntry ->
                        val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                        ChatScreen(
                            sessionId = sessionId,
                            onReviewClick = {
                                navController.navigate("review")
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("review") {
                        ReviewScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}