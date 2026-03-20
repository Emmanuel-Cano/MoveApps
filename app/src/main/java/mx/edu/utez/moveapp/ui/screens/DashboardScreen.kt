package mx.edu.utez.moveapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.model.TaskStatus
import mx.edu.utez.moveapp.viewmodel.DashboardViewModel
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFBCBCBC))) {
        TopBar()
        HeaderUser(viewModel.advisorName)
        KanbanBoard(viewModel)
    }
}

@Composable
fun KanbanBoard(viewModel: DashboardViewModel) {
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            KanbanColumn("Por Hacer", viewModel.todoTasks,
                onTaskClick = { selectedTask = it },
                onDragTask = { task, dragX ->
                    if (dragX > 250f) viewModel.moveTask(task.id, TaskStatus.IN_PROGRESS)
                }
            )
        }
        item {
            KanbanColumn("En Progreso", viewModel.progressTasks,
                onTaskClick = { selectedTask = it },
                onDragTask = { task, dragX ->
                    if (dragX > 250f) viewModel.moveTask(task.id, TaskStatus.DONE)
                    if (dragX < -250f) viewModel.moveTask(task.id, TaskStatus.TODO)
                }
            )
        }
        item {
            KanbanColumn("Finalizado", viewModel.doneTasks,
                onTaskClick = { selectedTask = it },
                onDragTask = { task, dragX ->
                    if (dragX < -250f) viewModel.moveTask(task.id, TaskStatus.IN_PROGRESS)
                }
            )
        }
    }

    selectedTask?.let { task ->
        TaskDetailDialog(
            task = task,
            onDismiss = { selectedTask = null },
            onMove = { id, newStatus -> viewModel.moveTask(id, newStatus) }
        )
    }
}

@Composable
fun KanbanColumn(
    title: String,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onDragTask: (Task, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(Color(0xFFD9D9D9), RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                DraggableTaskCard(
                    task = task,
                    onClick = { onTaskClick(task) },
                    onDragEnd = { x -> onDragTask(task, x) }
                )
            }
        }
    }
}

@Composable
fun DraggableTaskCard(task: Task, onClick: () -> Unit, onDragEnd: (Float) -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val elevation by animateFloatAsState(if (isDragging) 8f else 2f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnd(offsetX)
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier.width(35.dp).height(4.dp)
                    .background(
                        color = when(task.status) {
                            TaskStatus.TODO -> Color(0xFFE57373)
                            TaskStatus.IN_PROGRESS -> Color(0xFF64B5F6)
                            else -> Color(0xFF81C784)
                        },
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(Modifier.height(8.dp))
            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(task.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
        }
    }
}


@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.Black).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("MOVE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Spacer(Modifier.width(16.dp))
        TextField(value = "", onValueChange = {}, placeholder = { Text("Buscar") }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(24.dp))
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Default.Notifications, null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Default.Menu, null, tint = Color.White)
    }
}

@Composable
fun HeaderUser(name: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).background(Color.LightGray, CircleShape))
        Spacer(Modifier.width(10.dp))
        Text("Asesor: $name")
    }
}

@Composable
fun TaskDetailDialog(task: Task, onDismiss: () -> Unit, onMove: (Int, TaskStatus) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = Color.White, modifier = Modifier.padding(16.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("Detalles", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(task.title)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { onMove(task.id, TaskStatus.DONE); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Finalizar Tarea")
                }
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Cerrar")
                }
            }
        }
    }
}