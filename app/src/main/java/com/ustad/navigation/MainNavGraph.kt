package com.ustad.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.AuthGraph.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController = navController,
            onNavigateToCustomer = {
                navController.navigate(Screen.CustomerGraph.route) {
                    popUpTo(Screen.AuthGraph.route) { inclusive = true }
                }
            },
            onNavigateToWorker = {
                navController.navigate(Screen.WorkerGraph.route) {
                    popUpTo(Screen.AuthGraph.route) { inclusive = true }
                }
            },
            onNavigateToDevMenu = {
                navController.navigate(Screen.DevMenu.route)
            }
        )

        customerNavGraph(
            rootNavController = navController,
            onNavigateToDevMenu = {
                navController.navigate(Screen.DevMenu.route)
            }
        )

        workerNavGraph(
            rootNavController = navController,
            onNavigateToDevMenu = {
                navController.navigate(Screen.DevMenu.route)
            }
        )

        adminNavGraph(
            rootNavController = navController,
            onNavigateToDevMenu = {
                navController.navigate(Screen.DevMenu.route)
            }
        )

        composable(Screen.DevMenu.route) {
            DevMenuScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.AuthGraph.route) {
                        popUpTo(0)
                    }
                },
                onNavigateToCustomer = {
                    navController.navigate(Screen.CustomerGraph.route) {
                        popUpTo(0)
                    }
                },
                onNavigateToWorker = {
                    navController.navigate(Screen.WorkerGraph.route) {
                        popUpTo(0)
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminGraph.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
