package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.edu.utez.moveapp.data.response.SessionManager
import mx.edu.utez.moveapp.viewmodel.AdviserViewModel
import mx.edu.utez.moveapp.R

@Composable
fun AdviserListScreen(viewModel: AdviserViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.loadAdvisers()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopBar(
            onMenuClick = {  },
            onNotificationsClick = { navController.navigate("notifications") }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFD9D9D9), RoundedCornerShape(30.dp))
                .padding(20.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
            } else {
                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ){
                    items(viewModel.advisers) { adviser ->
                        AdviserCard(
                            name = adviser.name,
                            onIngresarClick = {
                                val token = SessionManager.token ?: ""
                                navController.navigate("dashboard/$token/${adviser.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdviserCard(name: String, onIngresarClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Asesor:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = onIngresarClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp)
            ) {
                Text("Ingresar", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}