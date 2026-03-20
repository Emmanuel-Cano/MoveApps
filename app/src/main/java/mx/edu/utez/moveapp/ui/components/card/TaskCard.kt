package mx.edu.utez.moveapp.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.model.TaskStatus

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .width(35.dp)
                    .height(4.dp)
                    .background(
                        color = when(task.status) {
                            TaskStatus.TODO -> Color(0xFFE57373)
                            TaskStatus.IN_PROGRESS -> Color(0xFF64B5F6)
                            TaskStatus.DONE -> Color(0xFF81C784)
                        },
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            Text(text = task.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
        }
    }
}