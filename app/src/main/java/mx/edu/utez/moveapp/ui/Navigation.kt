package mx.edu.utez.moveapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utez.moveapp.ui.screens.*
import mx.edu.utez.moveapp.viewmodel.AdviserViewModel
import mx.edu.utez.moveapp.viewmodel.LoginViewModel
import mx.edu.utez.moveapp.viewmodel.DashboardViewModel
import mx.edu.utez.moveapp.viewmodel.SettingsViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()

    val loginViewModel: LoginViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(loginViewModel, navController)
        }

        composable("loginUser") {
            LoginUserScreen(
                loginViewModel,
                navController,
                onLoginSuccess = {
                    navController.navigate("advisers") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            EmailCodeRequestScreen(
                loginViewModel,
                navController,
                onLoginSuccess = { navController.navigate("verifyCode") }
            )
        }

        composable("verifyCode") {
            VerifyCodeScreen(
                loginViewModel,
                navController,
                onLoginSuccess = { }
            )
        }

        composable("setPassword") {
            LoginUserScreenPassword(
                loginViewModel,
                navController,
                onLoginSuccess = { navController.navigate("loginUser") }
            )
        }

        composable("recoveryPassword") {
            ChangePasswordScreen(
                viewModel = loginViewModel,
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("settings") {
                        popUpTo("settings") { inclusive = true }
                    }
                }
            )
        }

        composable("advisers") {
            val adviserViewModel: AdviserViewModel = viewModel()
            AdviserListScreen(
                viewModel = adviserViewModel,
                navController = navController
            )
        }

        composable(
            route = "dashboard/{adviserId}/{adviserName}",
            arguments = listOf(
                navArgument("adviserId") { type = NavType.IntType },
                navArgument("adviserName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("adviserId") ?: 0
            val name = backStackEntry.arguments?.getString("adviserName") ?: "Asesor"

            DashboardScreen(
                viewModel = dashboardViewModel,
                loginViewModel = loginViewModel,
                navController = navController,
                token = loginViewModel.userToken ?: "",
                adviserId = id,
                adviserName = name
            )
        }

        composable("settings") {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                loginViewModel = loginViewModel,
                navController = navController
            )
        }

        composable("notifications") {
             NotificationsScreen(navController)
        }
    }
}