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
                onLoginSuccess = { token ->
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
                onLoginSuccess = { /* Manejar éxito de verificación */ }
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
            RecoveryPasswordScreen(
                loginViewModel,
                navController,
                onLoginSuccess = { navController.navigate("loginUser") }
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
            route = "dashboard/{token}/{adviserId}",
            arguments = listOf(
                navArgument("token") { type = NavType.StringType },
                navArgument("adviserId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            val adviserId = backStackEntry.arguments?.getInt("adviserId") ?: 0

            DashboardScreen(
                viewModel = dashboardViewModel,
                navController = navController,
                token = token,
                adviserId = adviserId
            )
        }

        composable("settings") {
            val settingsViewModel: SettingsViewModel = viewModel()
            val loginViewModel: LoginViewModel = viewModel()

            SettingsScreen(
                viewModel = settingsViewModel,
                loginViewModel = loginViewModel,
                navController = navController
            )
        }

        composable("notifications") {
            // NotificationsScreen(navController)
        }
    }
}