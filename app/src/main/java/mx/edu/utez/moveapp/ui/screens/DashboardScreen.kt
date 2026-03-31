package mx.edu.utez.moveapp.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.model.TaskStatus
import mx.edu.utez.moveapp.viewmodel.DashboardViewModel
import kotlin.math.roundToInt
import mx.edu.utez.moveapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    navController: NavController,
    token: String,
    adviserId: Int? = null,
    adviserName: String?
) {
    // Carga de datos real usando el nombre que viene de la navegación
    LaunchedEffect(adviserId) {
        viewModel.loadData(adviserId, adviserName)
    }

    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    // Launchers para archivos y cámara
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { showSheet = false }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { showSheet = false }
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { showSheet = false }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFBCBCBC))) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                onMenuClick = { showMenu = true },
                onNotificationsClick = { navController.navigate("notifications") }
            )

            HeaderUser(viewModel.advisorName)

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                KanbanBoard(
                    viewModel = viewModel,
                    onTaskClick = { selectedTask = it }
                )
            }
        }

        // Diálogo de menú lateral/configuración
        if (showMenu) {
            CustomMenuDialog(
                onDismiss = { showMenu = false },
                onNavigate = { route ->
                    showMenu = false
                    navController.navigate(route)
                }
            )
        }

        // DIÁLOGO DE DETALLE (PROTOTIPO)
        selectedTask?.let { task ->
            TaskDetailDialog(
                task = task,
                onDismiss = { selectedTask = null },
                onAddFileClick = { showSheet = true } // Abrir el ModalBottomSheet
            )
        }

        // MENÚ DE ADJUNTAR ARCHIVOS
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 40.dp)) {
                    Text("Adjuntar Evidencia", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(vertical = 16.dp))
                    AttachmentOption(Icons.Default.AddAPhoto, "Tomar foto") {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    AttachmentOption(Icons.Default.Collections, "Seleccionar foto") {
                        galleryLauncher.launch("image/*")
                    }
                    AttachmentOption(Icons.Default.Description, "Subir archivo") {
                        fileLauncher.launch("*/*")
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanBoard(viewModel: DashboardViewModel, onTaskClick: (Task) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            KanbanColumn("Por Hacer", viewModel.todoTasks, onTaskClick,
                onDrag = { t, x -> if (x > 300f) viewModel.moveTask(t.id, TaskStatus.IN_PROGRESS) })
        }
        item {
            KanbanColumn("En Progreso", viewModel.progressTasks, onTaskClick,
                onDrag = { t, x ->
                    if (x > 300f) viewModel.moveTask(t.id, TaskStatus.DONE)
                    if (x < -300f) viewModel.moveTask(t.id, TaskStatus.TODO)
                })
        }
        item {
            KanbanColumn("Finalizado", viewModel.doneTasks, onTaskClick,
                onDrag = { t, x -> if (x < -300f) viewModel.moveTask(t.id, TaskStatus.IN_PROGRESS) })
        }
    }
}

@Composable
fun KanbanColumn(title: String, tasks: List<Task>, onTaskClick: (Task) -> Unit, onDrag: (Task, Float) -> Unit) {
    Column(modifier = Modifier.width(300.dp).fillMaxHeight().background(Color(0xFFD9D9D9), RoundedCornerShape(20.dp)).padding(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp), color = Color.Black)
        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                DraggableTaskCard(task, onClick = { onTaskClick(task) }, onDragEnd = { onDrag(task, it) })
            }
        }
    }
}

@Composable
fun DraggableTaskCard(task: Task, onClick: () -> Unit, onDragEnd: (Float) -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val elevation by animateFloatAsState(if (isDragging) 8f else 2f)

    val indicatorColor = remember(task.color, task.status) {
        try {
            if (!task.color.isNullOrBlank()) {
                Color(android.graphics.Color.parseColor(task.color))
            } else {
                when {
                    task.status.equals("TODO", ignoreCase = true) -> Color(0xFFE57373)
                    task.status.contains("PROGRESO", ignoreCase = true) -> Color(0xFF64B5F6)
                    task.status.contains("FINALIZADO", ignoreCase = true) -> Color(0xFF81C784)
                    else -> Color.Gray
                }
            }
        } catch (e: Exception) { Color.Gray }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDrag = { change, drag -> change.consume(); offsetX += drag.x },
                    onDragEnd = { isDragging = false; onDragEnd(offsetX); offsetX = 0f },
                    onDragCancel = { isDragging = false; offsetX = 0f }
                )
            }
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(Modifier.width(35.dp).height(4.dp).background(indicatorColor, RoundedCornerShape(2.dp)))
            Spacer(Modifier.height(8.dp))
            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Text(task.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
        }
    }
}

@Composable
fun TaskDetailDialog(task: Task, onDismiss: () -> Unit, onAddFileClick: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(40.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Información de la tarea",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    Text(
                        text = task.title,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    Text(
                        "Notas",
                        modifier = Modifier.align(Alignment.Start),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = task.description,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionPillButton("Añadir comentario", Modifier.weight(1f)) {
                        }
                        ActionPillButton("Añadir archivo", Modifier.weight(1f)) {
                            onAddFileClick()
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionPillButton("Ver comentario", Modifier.weight(0.4f)) {
                        }

                        FileAttachmentCard(
                            name = task.fileName ?: "Sin archivos adjuntos",
                            type = "Documento",
                            modifier = Modifier.weight(0.6f)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 10.dp, y = (-10).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}


@Composable
fun ActionPillButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(50.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
        Text(text, color = Color.White, fontSize = 10.sp)
    }
}

@Composable
fun FileAttachmentCard(name: String, type: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, fontSize = 9.sp, maxLines = 1, fontWeight = FontWeight.Bold)
            Text(type, fontSize = 8.sp, color = Color.Gray)
        }
        Box(Modifier.size(28.dp).background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        }
    }
}

@Composable
fun HeaderUser(name: String) {
    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).background(Color.DarkGray, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, tint = Color.White)
        }
        Spacer(Modifier.width(10.dp))
        Text("Asesor: $name", fontWeight = FontWeight.Medium, color = Color.Black)
    }
}

@Composable
fun TopBar(onMenuClick: () -> Unit, onNotificationsClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color.Black).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = R.drawable.move), contentDescription = null, modifier = Modifier.size(45.dp))
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.clickable { onNotificationsClick() }.padding(end = 16.dp))
        Icon(Icons.Default.Menu, null, tint = Color.White, modifier = Modifier.clickable { onMenuClick() })
    }
}

@Composable
fun AttachmentOption(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Black, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, color = Color.Black)
    }
}

@Composable
fun CustomMenuDialog(onDismiss: () -> Unit, onNavigate: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = Color.White, modifier = Modifier.width(300.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MenuButton("Cambiar tablero") { onNavigate("advisers") }
                MenuButton("Configuración") { onNavigate("settings") }
                MenuButton("Salir") { onNavigate("login") }
            }
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(28.dp)) {
        Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}