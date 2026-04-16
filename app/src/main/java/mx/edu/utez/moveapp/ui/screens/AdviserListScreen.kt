package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mx.edu.utez.moveapp.viewmodel.AdviserViewModel
import mx.edu.utez.moveapp.R

@Composable
fun AdviserListScreen(viewModel: AdviserViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.loadAdvisers()
    }

    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            TopBar(
                title = "Selecciona el tablero del asesor",
                onMenuClick = { showMenu = true },
                onNotificationsClick = {navController.navigate("notifications")}
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color(0xFFD9D9D9), RoundedCornerShape(30.dp))
                    .padding(20.dp)
            ) {

                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Black
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        items(viewModel.advisers) { adviser ->
                            AdviserCard(
                                name = adviser.name,
                                logo = adviser.logo,
                                onIngresarClick = {
                                    navController.navigate("dashboard/${adviser.id}/${adviser.name}")
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showMenu) {
            CustomMenuDialog(
                onDismiss = { showMenu = false },
                onNavigate = { route ->
                    showMenu = false
                    navController.navigate(route)
                },
                onLogout = {
                    showMenu = false
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun AdviserCard(
    name: String,
    logo: String?,
    onIngresarClick: () -> Unit
) {

    val initials = name
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Asesor:",
                fontSize = 14.sp,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color(0xFF2C2C2C), CircleShape),
                    contentAlignment = Alignment.Center
                ) {

                    if (!logo.isNullOrBlank()) {

                        AsyncImage(
                            model = if (logo.startsWith("http")) {
                                logo
                            } else {
                                "data:image/*;base64,$logo"
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )

                    } else {

                        Text(
                            text = initials.ifBlank { "A" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    color = Color.Black
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

@Composable
fun TopBar(
    title: String = "MOVE",
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.move),
            contentDescription = null,
            modifier = Modifier.size(45.dp)
        )

        Text(
            text = title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            maxLines = 1
        )

        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notificaciones",
            tint = Color.White,
            modifier = Modifier
                .clickable { onNotificationsClick() }
                .padding(end = 16.dp)
        )

        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White,
            modifier = Modifier.clickable { onMenuClick() }
        )
    }
}