package mx.edu.utez.moveapp.ui.screens

import android.Manifest
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import mx.edu.utez.moveapp.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    loginViewModel: LoginViewModel,
    navController: NavController,
    token: String,
    adviserId: Int? = null,
    adviserName: String?
) {
    LaunchedEffect(adviserId) {
        viewModel.loadData(adviserId, adviserName)
    }

    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var evidenceComment by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        showSheet = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    // Documentos y fotos
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedUri = it
        }
        showSheet = false
    }

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
                    onTaskClick = {
                        selectedTask = it
                        selectedUri = null
                        evidenceComment = ""
                    }
                )
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
                    loginViewModel.clearSession()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        selectedTask?.let { task ->
            TaskDetailDialog(
                task = task,
                commentValue = evidenceComment,
                onCommentChange = { evidenceComment = it },
                onDismiss = { selectedTask = null },
                onAddFileClick = { showSheet = true },
                selectedUri = selectedUri,
                onSendClick = {
                    selectedUri?.let { uri ->
                        viewModel.uploadEvidence(
                            task.id,
                            evidenceComment.ifBlank { "Sin comentario" },
                            uri,
                            context
                        )
                        selectedTask = null
                    }
                }
            )
        }


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
                    AttachmentOption(Icons.Default.Collections, "Seleccionar foto / archivo") {
                        fileLauncher.launch("*/*")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskDetailDialog(
    task: Task,
    commentValue: String,
    onCommentChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddFileClick: () -> Unit,
    selectedUri: Uri?,
    onSendClick: () -> Unit
) {
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
                    Text("Información de la tarea", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                    Text(task.title, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))

                    Text("Descripción", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)).padding(12.dp)) {
                        Text(task.description, fontSize = 12.sp, color = Color.Black)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Tu Comentario", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = commentValue,
                        onValueChange = onCommentChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe algo...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionPillButton(
                            if (selectedUri == null) "Añadir archivo" else "Archivo listo ✓",
                            Modifier.weight(1f)
                        ) {
                            onAddFileClick()
                        }
                    }

                    Button(
                        onClick = onSendClick,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        enabled = selectedUri != null, // BLOQUEADO SI NO HAY ARCHIVO
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Enviar evidencia", color = Color.White)
                    }

                    if (selectedUri != null) {
                        Text("Archivo: ${selectedUri.lastPathSegment}", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).offset(x = 10.dp, y = (-10).dp)) {
                    Icon(Icons.Default.Close, "Cerrar", tint = Color.Black)
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

    val indicatorColor = try {
        if (!task.color.isNullOrBlank()) Color(android.graphics.Color.parseColor(task.color))
        else Color.Gray
    } catch (e: Exception) { Color.Gray }

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
fun ActionPillButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(36.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black), shape = RoundedCornerShape(50.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
        Text(text, color = Color.White, fontSize = 10.sp)
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
fun CustomMenuDialog(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier.width(300.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuButton("Cambiar tablero") { onNavigate("advisers") }
                MenuButton("Configuración") { onNavigate("settings") }

                MenuButton("Salir") { onLogout() }
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