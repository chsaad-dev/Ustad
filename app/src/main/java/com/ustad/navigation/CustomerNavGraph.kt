package com.ustad.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.ustad.presentation.components.UstadPrimaryButton
import com.ustad.presentation.customer.*
import com.ustad.presentation.theme.Spacing

private sealed class CustomerNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : CustomerNavItem(Screen.CustomerHome.route, "Home", Icons.Rounded.Home)
    object MyJobs : CustomerNavItem(Screen.JobTracking.route, "My Jobs", Icons.Rounded.Work)
    object History : CustomerNavItem(Screen.CustomerHistory.route, "History", Icons.Rounded.History)
    object Profile : CustomerNavItem(Screen.CustomerProfile.route, "Profile", Icons.Rounded.Person)
}

fun NavGraphBuilder.customerNavGraph(
    rootNavController: NavHostController,
    onNavigateToDevMenu: () -> Unit
) {
    navigation(
        startDestination = "customer_root_scaffold",
        route = Screen.CustomerGraph.route
    ) {
        composable("customer_root_scaffold") {
            CustomerScaffoldScreen(
                onNavigateToDevMenu = onNavigateToDevMenu,
                onLogout = { rootNavController.navigate(Screen.AuthGraph.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScaffoldScreen(
    onNavigateToDevMenu: () -> Unit,
    onLogout: () -> Unit
) {
    val nestedNavController = rememberNavController()
    val viewModel: CustomerViewModel = hiltViewModel()

    val navItems = listOf(
        CustomerNavItem.Home,
        CustomerNavItem.MyJobs,
        CustomerNavItem.History,
        CustomerNavItem.Profile
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
            startDestination = Screen.CustomerHome.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.CustomerHome.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onCategorySelected = { category ->
                        nestedNavController.navigate(Screen.CreateJob.route)
                    },
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
            composable(Screen.CreateJob.route) {
                CreateJobScreen(
                    viewModel = viewModel,
                    onJobCreated = { jobId ->
                        nestedNavController.navigate(Screen.FindingUstad.route)
                    },
                    onBack = { nestedNavController.popBackStack() }
                )
            }
            composable(Screen.FindingUstad.route) {
                FindingUstadScreen(
                    viewModel = viewModel,
                    onWorkerBooked = {
                        nestedNavController.navigate(Screen.JobTracking.route)
                    },
                    onBack = { nestedNavController.popBackStack() }
                )
            }
            composable(Screen.JobTracking.route) {
                JobTrackingScreen(
                    viewModel = viewModel,
                    onNavigateToRating = {
                        nestedNavController.navigate("customer_rating")
                    },
                    onBackToHome = {
                        nestedNavController.navigate(Screen.CustomerHome.route)
                    }
                )
            }
            composable("customer_rating") {
                RatingScreen(
                    viewModel = viewModel,
                    onSubmitted = {
                        nestedNavController.navigate(Screen.CustomerHome.route) {
                            popUpTo(Screen.CustomerHome.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.CustomerHistory.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
            composable("available_workers_map") {
                AvailableWorkersMapScreen(
                    customerViewModel = viewModel,
                    onNavigateBack = { nestedNavController.popBackStack() },
                    onSelectWorker = { worker ->
                        nestedNavController.navigate(Screen.CreateJob.route)
                    }
                )
            }
            composable(Screen.CustomerProfile.route) {
                CustomerProfileScreen(
                    onLogout = onLogout,
                    onNavigateToDevMenu = onNavigateToDevMenu
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerProfileScreen(
    onLogout: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf("English") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Profile") },
                actions = {
                    TextButton(onClick = onNavigateToDevMenu) {
                        Text("Dev Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ali (Customer Account)", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text("Phone: +92 300 1234567 | City: Sahiwal", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(Spacing.lg))

            // Language Selector Chip
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("App Language: ", style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.width(Spacing.xs))
                FilterChip(
                    selected = (selectedLanguage == "English"),
                    onClick = { selectedLanguage = "English" },
                    label = { Text("English") }
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                FilterChip(
                    selected = (selectedLanguage == "Urdu"),
                    onClick = { selectedLanguage = "Urdu (اردو)" },
                    label = { Text("اردو") }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            UstadPrimaryButton(
                text = "Sign Out",
                onClick = onLogout
            )
        }
    }
}

