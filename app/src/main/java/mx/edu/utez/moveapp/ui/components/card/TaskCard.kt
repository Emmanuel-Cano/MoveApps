package mx.edu.utez.moveapp.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.moveapp.data.model.Task

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(6.dp)
                    .background(
                        color = when {
                            task.status.equals("TODO", ignoreCase = true) -> Color(0xFFE57373)
                            task.status.equals("IN_PROGRESS", ignoreCase = true) ||
                                    task.status.contains("PROGRESO", ignoreCase = true) -> Color(0xFF64B5F6)
                            task.status.equals("DONE", ignoreCase = true) ||
                                    task.status.contains("FINALIZADO", ignoreCase = true) -> Color(0xFF81C784)
                            else -> Color.LightGray
                        },
                        shape = RoundedCornerShape(3.dp)
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                color = Color.Black
            )

            Text(
                text = task.description,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}