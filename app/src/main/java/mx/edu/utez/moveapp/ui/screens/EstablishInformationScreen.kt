package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import mx.edu.utez.moveapp.R
import mx.edu.utez.moveapp.ui.components.buttons.PrimaryButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import mx.edu.utez.moveapp.viewmodel.LoginViewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults


@Composable
fun EstablishInformationScreen(viewModel: LoginViewModel, navController: NavController,
                           onLoginSuccess: () -> Unit){
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
                text = "Establecer información",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF464646),
                modifier =  Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Ingresa tus datos",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF918F8F),
                modifier =  Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Nombre(s)",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = viewModel.firstname,
                onValueChange = { viewModel.firstname = it },
                label = { Text("Nombre(s):") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "Apellidos:",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = viewModel.lastname,
                onValueChange = { viewModel.lastname = it },
                label = { Text("Apellidos:") },
                modifier = Modifier.fillMaxWidth(),
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            GenderDropdown(
                selectedGender = viewModel.gender,
                onGenderSelected = { viewModel.gender = it }
            )

            


            PrimaryButton("Continuar") {
                viewModel.saveStudentInformation()

                onLoginSuccess()
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val options = listOf("Masculino", "Femenino", "Otro", "Prefiero no decirlo")

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = "Género",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = selectedGender,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Seleccionar") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                options.forEach { gender ->

                    DropdownMenuItem(
                        text = { Text(gender) },
                        onClick = {
                            onGenderSelected(gender)
                            expanded = false
                        }
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EstablishInformationScreenPreview() {

    val navController = rememberNavController()

    EstablishInformationScreen(
        viewModel = LoginViewModel(),
        navController = navController,
        onLoginSuccess = {}
    )
}