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
import mx.edu.utez.moveapp.ui.components.buttons.SecundaryButton
import mx.edu.utez.moveapp.ui.components.texts.Title
import mx.edu.utez.moveapp.viewmodel.LoginViewModel
import mx.edu.utez.moveapp.R
import mx.edu.utez.moveapp.ui.components.inputs.PasswordField
import mx.edu.utez.moveapp.ui.components.inputs.UserInputField
import mx.edu.utez.moveapp.ui.components.texts.Link

@Composable
fun LoginUserScreenPassword(viewModel: LoginViewModel, navController: NavController,
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

            Text(
                text = "Establecer contraseña",
                fontSize = 30.sp,
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


            Text(
                text = "Confirmar contraseña",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )

            PasswordField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = "Confirmar contraseña"
            )


            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            PrimaryButton("Confirmar") {
                viewModel.addPassword()
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
fun LoginUserScreenPasswordPreview() {

    val navController = rememberNavController()

    LoginUserScreenPassword(
        viewModel = LoginViewModel(),
        navController = navController,
        onLoginSuccess = {}
    )
}