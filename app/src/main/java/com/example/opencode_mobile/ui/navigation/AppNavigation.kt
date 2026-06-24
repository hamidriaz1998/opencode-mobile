package com.example.opencode_mobile.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.opencode_mobile.data.api.ApiServiceProvider
import com.example.opencode_mobile.data.local.ConnectionManager
import com.example.opencode_mobile.data.local.ConnectionStore
import com.example.opencode_mobile.ui.screens.home.HomeScreen
import com.example.opencode_mobile.ui.screens.chat.ChatScreen
import com.example.opencode_mobile.ui.screens.projects.ProjectsScreen
import com.example.opencode_mobile.ui.screens.review.ReviewScreen
import com.example.opencode_mobile.ui.screens.sessions.SessionsScreen
import com.example.opencode_mobile.ui.screens.settings.SettingsScreen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Connections", Icons.Default.Cable, Routes.HOME),
    BottomNavItem("Settings", Icons.Default.Settings, Routes.SETTINGS)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    apiServiceProvider: ApiServiceProvider,
    connectionStore: ConnectionStore,
    connectionManager: ConnectionManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(Routes.HOME, Routes.SETTINGS)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF0F0E0D),
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFEDB2F1),
                                selectedTextColor = Color(0xFFEDB2F1),
                                unselectedIconColor = Color(0xFF998D97),
                                unselectedTextColor = Color(0xFF998D97),
                                indicatorColor = Color(0xFF383433)
                            )
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF161312)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onConnect = { connection ->
                        val baseUrl = "http://${connection.address}:${connection.port}"
                        apiServiceProvider.init(baseUrl)
                        connectionManager.setConnection(connection)
                        navController.navigate(Routes.projects(connection.id))
                    }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.PROJECTS,
                arguments = listOf(navArgument("connectionId") { type = NavType.StringType })
            ) {
                ProjectsScreen(
                    onProjectClick = { projectId, projectWorktree ->
                        navController.navigate(Routes.sessions(projectId, projectWorktree))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.SESSIONS,
                arguments = listOf(
                    navArgument("projectId") { type = NavType.StringType },
                    navArgument("projectWorktree") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val projectWorktree = backStackEntry.arguments?.getString("projectWorktree") ?: ""
                SessionsScreen(
                    projectWorktree = projectWorktree,
                    onSessionClick = { sessionId ->
                        navController.navigate(Routes.chat(sessionId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                ChatScreen(
                    sessionId = sessionId,
                    onReviewClick = { navController.navigate(Routes.review(sessionId)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.REVIEW,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                ReviewScreen(
                    sessionId = sessionId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F0E0D),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF161312)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("$title screen goes here", color = Color(0xFF998D97), fontSize = 16.sp)
        }
    }
}
