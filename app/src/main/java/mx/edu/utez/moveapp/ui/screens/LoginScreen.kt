package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController){
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
            Title("MOVE")

            Text(
                text = "Bienvenido",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF484848),
                modifier =  Modifier.align(Alignment.CenterHorizontally)
            )

            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Imagen bienvenida",
                modifier = Modifier
                    .padding(top = 70.dp)
                    .padding(bottom = 160.dp)
                    .size(150.dp)
            )

            PrimaryButton("Iniciar Sesión") {
                navController.navigate("loginUser")
            }


            SecundaryButton("Registrarse") {
                navController.navigate("register")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        viewModel = LoginViewModel(),
        navController = rememberNavController()
    )
}

