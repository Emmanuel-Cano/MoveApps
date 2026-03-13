package mx.edu.utez.moveapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.moveapp.ui.screens.*
import mx.edu.utez.moveapp.viewmodel.LoginViewModel

@Composable
fun Navigation(){

    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

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
                    navController.navigate("home")
                }
            )
        }

        composable("register") {
            LoginRegisterScreen(
                loginViewModel,
                navController,
                onLoginSuccess = {
                    navController.navigate("verifyCode")
                }
            )
        }

        composable("verifyCode") {
            VerifyCodeScreen(
                loginViewModel,
                navController,
                onLoginSuccess = {
                    navController.navigate("setPassword")
                }
            )
        }

        composable("setPassword") {
            LoginUserScreenPassword(
                loginViewModel,
                navController,
                onLoginSuccess = {
                    navController.navigate("home")
                }
            )
        }

        composable("recuperar") {
            RecoveryPasswordScreen(
                loginViewModel,
                navController,
                onLoginSuccess = {
                    navController.navigate("home")
                }
            )
        }

        composable("home") {

        }

    }
}