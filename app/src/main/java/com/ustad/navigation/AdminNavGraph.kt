package com.ustad.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ustad.presentation.admin.*

private sealed class AdminNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Overview : AdminNavItem(Screen.AdminOverview.route, "Overview", Icons.Rounded.Analytics)
    object Verifications : AdminNavItem(Screen.AdminVerificationQueue.route, "Verifications", Icons.Rounded.VerifiedUser)
    object Reports : AdminNavItem(Screen.AdminReports.route, "Reports", Icons.Rounded.Analytics)
}

fun NavGraphBuilder.adminNavGraph(
    rootNavController: NavHostController,
    onNavigateToDevMenu: () -> Unit
) {
    navigation(
        startDestination = "admin_root_scaffold",
        route = Screen.AdminGraph.route
    ) {
        composable("admin_root_scaffold") {
            AdminScaffoldScreen(
                onNavigateToDevMenu = onNavigateToDevMenu,
                onLogout = { rootNavController.navigate(Screen.AuthGraph.route) }
            )
        }
    }
}

@Composable
fun AdminScaffoldScreen(
    onNavigateToDevMenu: () -> Unit,
    onLogout: () -> Unit
) {
    val nestedNavController = rememberNavController()
    val viewModel: AdminViewModel = hiltViewModel()

    val navItems = listOf(
        AdminNavItem.Overview,
        AdminNavItem.Verifications,
        AdminNavItem.Reports
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            nestedNavController.navigate(item.route) {
                                popUpTo(nestedNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nestedNavController,
            startDestination = Screen.AdminOverview.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.AdminOverview.route) {
                AdminOverviewScreen(
                    viewModel = viewModel,
                    onNavigateToQueue = {
                        nestedNavController.navigate(Screen.AdminVerificationQueue.route)
                    },
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
            composable(Screen.AdminVerificationQueue.route) {
                VerificationQueueScreen(
                    viewModel = viewModel,
                    onSelectWorker = {
                        nestedNavController.navigate(Screen.AdminWorkerDetail.route)
                    },
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
            composable(Screen.AdminWorkerDetail.route) {
                WorkerVerificationDetailScreen(
                    viewModel = viewModel,
                    onBack = { nestedNavController.popBackStack() }
                )
            }
            composable(Screen.AdminReports.route) {
                AdminReportsScreen(
                    adminViewModel = viewModel,
                    onNavigateBack = { nestedNavController.popBackStack() }
                )
            }
        }
    }
}

