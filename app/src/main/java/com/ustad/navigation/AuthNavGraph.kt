package com.ustad.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ustad.presentation.auth.*

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onNavigateToCustomer: () -> Unit,
    onNavigateToWorker: () -> Unit,
    onNavigateToDevMenu: () -> Unit
) {
    navigation(
        startDestination = Screen.Login.route,
        route = Screen.AuthGraph.route
    ) {
        composable(Screen.Login.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AuthGraph.route)
            }
            val viewModel: AuthViewModel = hiltViewModel(parentEntry)

            LoginScreen(
                viewModel = viewModel,
                onOtpSent = { navController.navigate(Screen.OtpVerification.route) },
                onNavigateToDevMenu = onNavigateToDevMenu
            )
        }
        composable(Screen.OtpVerification.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AuthGraph.route)
            }
            val viewModel: AuthViewModel = hiltViewModel(parentEntry)

            OtpVerificationScreen(
                viewModel = viewModel,
                onVerified = { navController.navigate(Screen.ProfileSetup.route) }
            )
        }
        composable(Screen.ProfileSetup.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AuthGraph.route)
            }
            val viewModel: AuthViewModel = hiltViewModel(parentEntry)

            ProfileSetupScreen(
                viewModel = viewModel,
                onProfileSet = { navController.navigate(Screen.RoleSelect.route) }
            )
        }
        composable(Screen.RoleSelect.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AuthGraph.route)
            }
            val viewModel: AuthViewModel = hiltViewModel(parentEntry)

            RoleSelectScreen(
                viewModel = viewModel,
                onRoleConfirmed = { role ->
                    if (role == "worker") {
                        onNavigateToWorker()
                    } else {
                        onNavigateToCustomer()
                    }
                }
            )
        }
    }
}
