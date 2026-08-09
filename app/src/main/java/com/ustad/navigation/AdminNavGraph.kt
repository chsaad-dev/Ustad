package com.ustad.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ustad.presentation.theme.Spacing

private sealed class AdminNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Overview : AdminNavItem(Screen.AdminOverview.route, "Overview", Icons.Rounded.Analytics)
    object Verifications : AdminNavItem(Screen.AdminVerificationQueue.route, "Verifications", Icons.Rounded.VerifiedUser)
    object Jobs : AdminNavItem(Screen.AdminJobMonitor.route, "Jobs", Icons.Rounded.Assignment)
    object Reports : AdminNavItem(Screen.AdminReports.route, "Reports", Icons.Rounded.Flag)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffoldScreen(
    onNavigateToDevMenu: () -> Unit,
    onLogout: () -> Unit
) {
    val nestedNavController = rememberNavController()
    val navItems = listOf(
        AdminNavItem.Overview,
        AdminNavItem.Verifications,
        AdminNavItem.Jobs,
        AdminNavItem.Reports
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustad Admin Panel (Debug Shortcut Access)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                actions = {
                    TextButton(onClick = onNavigateToDevMenu) {
                        Text("Dev Menu")
                    }
                }
            )
        },
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
                AdminPlaceholderContent(
                    title = "Admin Overview Dashboard",
                    subtitle = "Stats: Total Users, Active Jobs, Pending Verifications",
                    actionText = "View Verification Queue",
                    onAction = { nestedNavController.navigate(Screen.AdminVerificationQueue.route) }
                )
            }
            composable(Screen.AdminVerificationQueue.route) {
                AdminPlaceholderContent(
                    title = "Worker Verification Queue",
                    subtitle = "List of workers pending verification",
                    actionText = "Review Sample Worker",
                    onAction = { nestedNavController.navigate(Screen.AdminWorkerDetail.createRoute("worker_456")) }
                )
            }
            composable(
                route = Screen.AdminWorkerDetail.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
                AdminPlaceholderContent(
                    title = "Worker Detail & Verification",
                    subtitle = "Reviewing Worker ID: $workerId\nCNIC front/back viewer & Approve/Reject actions",
                    actionText = "Back to Queue",
                    onAction = { nestedNavController.popBackStack() }
                )
            }
            composable(Screen.AdminJobMonitor.route) {
                AdminPlaceholderContent(
                    title = "Admin Job Monitor",
                    subtitle = "Filter all platform jobs by city, category, and status",
                    actionText = "View Reports",
                    onAction = { nestedNavController.navigate(Screen.AdminReports.route) }
                )
            }
            composable(Screen.AdminReports.route) {
                AdminPlaceholderContent(
                    title = "Abuse & Dispute Reports Queue",
                    subtitle = "Review customer & worker reports",
                    actionText = "Sign Out Admin Session",
                    onAction = onLogout
                )
            }
        }
    }
}

@Composable
private fun AdminPlaceholderContent(
    title: String,
    subtitle: String,
    actionText: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(Spacing.xl))
        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(actionText)
        }
    }
}
