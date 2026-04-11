package mx.edu.utez.moveapp.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import mx.edu.utez.moveapp.utils.uriToFile
import mx.edu.utez.moveapp.viewmodel.LoginViewModel
import mx.edu.utez.moveapp.viewmodel.SettingsViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    loginViewModel: LoginViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(loginViewModel.goToVerifyCode) {
        if (loginViewModel.goToVerifyCode) {
            navController.navigate("verifyCode")
            loginViewModel.goToVerifyCode = false
        }
    }

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            if (file != null) viewModel.uploadProfileImage(file)
        }
        showSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = bitmapToFile(context, it)
            if (file != null) viewModel.uploadProfileImage(file)
        }
        showSheet = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) cameraLauncher.launch()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFD9D9D9), RoundedCornerShape(30.dp))
                .padding(20.dp)
        ) {
            if (viewModel.isLoading || loginViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.Black)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text("Perfil", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }

                    item {
                        SettingsCard(title = "Imagen de perfil") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(60.dp).background(Color.White, CircleShape).border(1.dp, Color.Black, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (viewModel.profileImage != null && viewModel.profileImage!!.isNotEmpty()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = viewModel.profileImage),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp))
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                TextButton(onClick = { showSheet = true }) {
                                    Text("Cambiar", color = Color.Gray)
                                }
                            }
                        }
                    }

                    item {
                        SettingsCard(title = "Nombre y apellido") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    ProfileTextField("Nombre(s):", viewModel.firstName) { viewModel.firstName = it }
                                    ProfileTextField("Apellidos:", viewModel.lastName) { viewModel.lastName = it }
                                }
                                TextButton(onClick = { viewModel.updateName() }) {
                                    Text("Cambiar", color = Color.Gray)
                                }
                            }
                        }
                    }

                    item {
                        SettingsCard(title = "Seguridad") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ProfileTextField("Correo:", viewModel.email, enabled = false)
                                ProfileTextField("Contraseña:", "**********", enabled = false)
                                Text(
                                    "Cambiar contraseña",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        loginViewModel.email = viewModel.email
                                        loginViewModel.authMode = "RECOVERY"
                                        loginViewModel.changePassword()
                                    }
                                )
                            }
                        }
                    }

                    item {
                        SettingsCard(title = "Notificaciones") {
                            Column {
                                NotificationItem("Permitir notificaciones dentro de la app")
                                NotificationItem("Permitir notificaciones en el dispositivo")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }, sheetState = sheetState, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 40.dp)) {
                Text("Adjuntar", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(vertical = 16.dp))
                SettingsAttachmentOption(Icons.Default.AddAPhoto, "Tomar foto") {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
                SettingsAttachmentOption(Icons.Default.Collections, "Seleccionar foto") {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }
    }
}

@Composable
fun SettingsAttachmentOption(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Black, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, color = Color.Black)
    }
}

@Composable
fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
            content()
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, enabled: Boolean = true, onValueChange: (String) -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 12.sp, modifier = Modifier.width(80.dp))
        BasicTextField(value = value, onValueChange = onValueChange, enabled = enabled, modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black).padding(4.dp))
    }
}

@Composable
fun NotificationItem(text: String) {
    var checked by remember { mutableStateOf(true) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { checked = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color.Black))
    }
}

fun bitmapToFile(context: Context, bitmap: Bitmap): File? {
    val file = File(context.cacheDir, "camera_image.jpg")
    return try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}