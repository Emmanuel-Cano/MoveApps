package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.moveapp.ui.components.buttons.PrimaryButton
import mx.edu.utez.moveapp.ui.components.inputs.PasswordField
import mx.edu.utez.moveapp.ui.components.inputs.UserInputField
import mx.edu.utez.moveapp.ui.components.texts.Link
import mx.edu.utez.moveapp.ui.components.texts.Title
import mx.edu.utez.moveapp.viewmodel.LoginViewModel
import mx.edu.utez.moveapp.R



val DarkBackground = Color(0xFF36454F)
val AccentButton = Color(0xFF987723)
val LightText = Color.White

@Composable
fun LoginUserScreen(viewModel: LoginViewModel, navController: NavController,
                    onLoginSuccess: () -> Unit){
    LaunchedEffect(viewModel.goToHome) {
        if (viewModel.goToHome) {
            onLoginSuccess()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
        ) {
            Title("Iniciar sesión")

            Text(
                text = "Bienvenido a MOVE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF464646),
                modifier =  Modifier.align(Alignment.CenterHorizontally)
            )

            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Imagen bienvenida",
                modifier = Modifier
                    .padding(top = 30.dp)
                    .padding(bottom = 80.dp)
                    .size(150.dp)
            )

            Text(
                text = "Correo",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )

            UserInputField(
                viewModel = viewModel,
                label = "Correo",
            )

            Text(
                text = "Contraseña",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )

            PasswordField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Contraseña"
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            PrimaryButton("Iniciar") {
                viewModel.login()
            }
            Link("¿Olvidaste tu contraseña?") {
                viewModel.authMode = "RECOVERY"
                navController.navigate("register")
            }

            TextButton(onClick = {
                navController.popBackStack()
            }) {
                Text("Cancelar", color = Color.Black)
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginUserScreenPreview() {
    LoginUserScreen(
        viewModel = LoginViewModel(),
        navController = rememberNavController(),
        onLoginSuccess = {}
    )
}
