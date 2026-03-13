package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.moveapp.ui.components.buttons.PrimaryButton
import mx.edu.utez.moveapp.ui.components.texts.Title
import mx.edu.utez.moveapp.viewmodel.LoginViewModel

@Composable
fun VerifyCodeScreen(
    viewModel: LoginViewModel,
    navController: NavController,
    onLoginSuccess: () -> Unit
) {


    var code by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.goToCreatePassword) {
        if (viewModel.goToCreatePassword) {
            if(viewModel.authMode == "REGISTER"){
                navController.navigate("setPassword")
            }
            if(viewModel.authMode == "RECOVERY"){
                navController.navigate("recoveryPassword")
            }

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Title("Verificar código")

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ingresa el código enviado a tu correo",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Text(
            text = viewModel.email,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(40.dp))

        OTPInput(code) {
            code = it
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton("Verificar") {
            viewModel.onCodeChange(code)
            viewModel.validateCode()
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = { navController.navigate("login") }
        ) {
            Text("Iniciar sesión")
        }
    }
}
@Composable
fun OTPInput(
    code: String,
    onCodeChange: (String) -> Unit
) {

    BasicTextField(
        value = code,
        onValueChange = {
            if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                onCodeChange(it)
            }
        },
        modifier = Modifier
            .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(20.dp),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreenPreview() {

    val navController = rememberNavController()

    VerifyCodeScreen(
        viewModel = LoginViewModel(),
        navController = navController,
        onLoginSuccess = {}
    )
}