package mx.edu.utez.moveapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.ui.components.TaskCard
import mx.edu.utez.moveapp.viewmodel.DashboardViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mx.edu.utez.moveapp.ui.components.BlankCard

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar()

        HeaderUser()

        KanbanBoard(viewModel)

    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("MOVE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)

        Spacer(modifier = Modifier.width(16.dp))

        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Buscar", color = Color.Gray) },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFE0E0E0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
    }
}
@Composable
fun HeaderUser(){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray, shape = CircleShape)
        )

        Text("Asesor: Ocampo Vargas Ricardo Naul")

    }

}

@Composable
fun KanbanBoard(viewModel: DashboardViewModel) {
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBCBCBC)) // El gris oscuro del fondo
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            KanbanColumn("Por Hacer", viewModel.todoTasks) { selectedTask = it }
        }
        item {
            KanbanColumn("En Progreso", viewModel.progressTasks) { selectedTask = it }
        }
        item {
            KanbanColumn("Finalizado", viewModel.doneTasks) { selectedTask = it }
        }
    }

    selectedTask?.let { task ->
        TaskDetailDialog(task = task, onDismiss = { selectedTask = null })
    }
}

@Composable
fun KanbanColumn(
    title: String,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(Color(0xFFD9D9D9), RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 12.dp),
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks) { task ->
                TaskCard(task, onClick = { onTaskClick(task) })
            }
        }
    }
}

@Composable
fun TaskDetailDialog(task: Task, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Información de la tarea", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text(task.title)

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DateBox("02/03/2026")
                    DateBox("30/03/2026")
                }

                Spacer(Modifier.height(16.dp))
                Text("Notas", fontWeight = FontWeight.SemiBold)

                // Cuadro de notas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(task.description, fontSize = 12.sp)
                }

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton("Añadir comentario", Modifier.weight(1f))
                    ActionButton("Añadir archivo", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DateBox(date: String) {
    Row(
        modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(4.dp)).padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(date, fontSize = 12.sp)
        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun ActionButton(text: String, modifier: Modifier) {
    Button(
        onClick = { /* Acción */ },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text, fontSize = 10.sp, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {

    val viewModel = DashboardViewModel()

    DashboardScreen(viewModel)

}