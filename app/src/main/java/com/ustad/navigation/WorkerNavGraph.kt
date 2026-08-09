package com.ustad.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.ustad.presentation.worker.*

private sealed class WorkerNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : WorkerNavItem(Screen.WorkerDashboard.route, "Dashboard", Icons.Rounded.Dashboard)
    object Requests : WorkerNavItem(Screen.WorkerRequests.route, "Requests", Icons.Rounded.ListAlt)
    object ActiveJob : WorkerNavItem(Screen.WorkerActiveJob.route, "My Job", Icons.Rounded.Build)
    object Profile : WorkerNavItem(Screen.WorkerProfile.route, "Profile", Icons.Rounded.Person)
}

fun NavGraphBuilder.workerNavGraph(
    rootNavController: NavHostController,
    onNavigateToDevMenu: () -> Unit
) {
    navigation(
        startDestination = "worker_root_scaffold",
        route = Screen.WorkerGraph.route
    ) {
        composable("worker_root_scaffold") {
            WorkerScaffoldScreen(
                onNavigateToDevMenu = onNavigateToDevMenu,
                onLogout = { rootNavController.navigate(Screen.AuthGraph.route) }
            )
        }
    }
}

@Composable
fun WorkerScaffoldScreen(
    onNavigateToDevMenu: () -> Unit,
    onLogout: () -> Unit
) {
    val nestedNavController = rememberNavController()
    val viewModel: WorkerViewModel = hiltViewModel()

    val navItems = listOf(
        WorkerNavItem.Dashboard,
        WorkerNavItem.Requests,
        WorkerNavItem.ActiveJob,
        WorkerNavItem.Profile
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
            startDestination = Screen.WorkerDashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.WorkerDashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToActiveJob = {
                        nestedNavController.navigate(Screen.WorkerActiveJob.route)
                    },
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
            composable(Screen.WorkerRequests.route) {
                RequestsScreen(
                    viewModel = viewModel,
                    onNavigateToActiveJob = {
                        nestedNavController.navigate(Screen.WorkerActiveJob.route)
                    },
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
            composable(Screen.WorkerActiveJob.route) {
                ActiveJobScreen(
                    viewModel = viewModel,
                    onJobCompleted = {
                        nestedNavController.navigate(Screen.WorkerDashboard.route)
                    },
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
            composable(Screen.WorkerVerification.route) {
                WorkerVerificationScreen(
                    viewModel = viewModel,
                    onBack = { nestedNavController.popBackStack() }
                )
            }
            composable(Screen.WorkerProfile.route) {
                WorkerProfileScreen(
                    viewModel = viewModel,
                    onNavigateToVerification = {
                        nestedNavController.navigate(Screen.WorkerVerification.route)
                    },
                    onLogout = onLogout,
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
        }
    }
}
