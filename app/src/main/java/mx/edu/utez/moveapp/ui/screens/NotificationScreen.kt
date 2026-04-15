package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import mx.edu.utez.moveapp.ui.components.card.NotificationCard
import mx.edu.utez.moveapp.viewmodel.DashboardViewModel
import mx.edu.utez.moveapp.data.response.Notification
import androidx.compose.foundation.lazy.items

@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: DashboardViewModel,
    token: String
) {

    LaunchedEffect(Unit) {
        viewModel.loadNotifications(token)
    }

    val notifications = viewModel.notifications

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }

            Text(
                text = "Notificaciones",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notifications) { item ->
                NotificationCard(
                    title = "Notificación",
                    message = item.message,
                    isRead = item.read,
                    onClick = {
                        viewModel.markAsRead(token, item.id)
                    }
                )
            }
        }
    }
}