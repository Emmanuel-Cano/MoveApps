package mx.edu.utez.moveapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.model.TaskStatus
import mx.edu.utez.moveapp.data.network.RetrofitInstance
import mx.edu.utez.moveapp.data.network.RetrofitInstance.api
import mx.edu.utez.moveapp.data.request.ChangeStatusRequest
import mx.edu.utez.moveapp.data.response.Notification
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import mx.edu.utez.moveapp.data.response.TaskDetailResponse

class DashboardViewModel : ViewModel() {

    var tasks by mutableStateOf(listOf<Task>())
    var isLoading by mutableStateOf(false)
    var advisorName by mutableStateOf("")

    var selectedTaskDetail by mutableStateOf<TaskDetailResponse?>(null)

    // 1. Cambiamos boardId: Int? por adviserId: Long?
    fun loadData(adviserId: Long?, name: String? = null) {

        // 2. Comparamos con 0L (cero tipo Long)
        if (adviserId == null || adviserId <= 0L) return

        name?.let { advisorName = it }

        viewModelScope.launch {
            isLoading = true
            try {

                println("DEBUG_ID: adviserId=$adviserId")
                println("DEBUG_TOKEN: ${mx.edu.utez.moveapp.data.response.SessionManager.token}")

                // 3. Pasamos el adviserId correcto
                val response = RetrofitInstance.api.getTasksByAdviser(
                    "Bearer ${mx.edu.utez.moveapp.data.response.SessionManager.token}",
                    adviserId
                )

                println("DEBUG_RESPONSE_CODE: ${response.code()}")
                println("DEBUG_RESPONSE_BODY: ${response.body()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        tasks = body

                        println("DEBUG_MOVE: ¡Petición Exitosa! Tareas: ${body.size}")
                        body.forEach { task ->
                            println("DEBUG_TASK: ID=${task.id}, Nombre=${task.title}, Status=${task.status}")
                        }
                    } else {
                        println("DEBUG_MOVE: El cuerpo de la respuesta es NULO")
                        tasks = emptyList()
                    }
                } else {
                    println("DEBUG_MOVE: Error del servidor: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("DEBUG_MOVE: EXCEPCIÓN AL CARGAR: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    val todoTasks get() = tasks.filter {
        it.status.equals("TODO", ignoreCase = true)
    }

    val progressTasks get() = tasks.filter {
        it.status.equals("Doing", ignoreCase = true)
    }

    val doneTasks get() = tasks.filter {
        it.status.equals("Done", ignoreCase = true)
    }

    fun moveTask(taskId: Int, newStatus: TaskStatus) {

        val oldTasks = tasks
        tasks = tasks.map { task ->
            if (task.id == taskId) {
                task.copy(status = newStatus.value)
            } else task
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.changeTaskStatus(
                    "Bearer ${mx.edu.utez.moveapp.data.response.SessionManager.token}",
                    taskId,
                    ChangeStatusRequest(newStatus.value)
                )

                if (response.isSuccessful) {
                    println("STATUS ACTUALIZADO EN BD")
                } else {
                    tasks = oldTasks
                }

            } catch (e: Exception) {
                e.printStackTrace()

                tasks = oldTasks
            }
        }
    }

    fun uploadEvidence(taskId: Int, comment: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@launch
                inputStream.close()

                val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData(
                    "files",
                    "evidencia.jpg",
                    requestFile
                )

                val taskIDBody = taskId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val commentBody = comment.toRequestBody("text/plain".toMediaTypeOrNull())

                // ASÍ DEBE QUEDAR (Copia y pega este bloque):
                val response = RetrofitInstance.api.submitEvidence(
                    "Bearer ${mx.edu.utez.moveapp.data.response.SessionManager.token}", // 👇 ESTA ES LA LÍNEA MÁGICA
                    taskIDBody,
                    commentBody,
                    listOf(filePart)
                )

                if (response.isSuccessful) {
                    println("DEBUG_UPLOAD: ¡Éxito! ${response.body()?.message}")
                } else {
                    println("DEBUG_UPLOAD_ERROR: ${response.code()} - ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadTaskDetail(taskId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTaskDetail(
                    "Bearer ${mx.edu.utez.moveapp.data.response.SessionManager.token}",
                    taskId
                )

                println("DETAIL_CODE: ${response.code()}")

                val raw = response.errorBody()?.string()
                println("DETAIL_ERROR: $raw")

                val body = response.body()
                println("DETAIL_BODY: $body")

                if (response.isSuccessful && body != null) {
                    selectedTaskDetail = body
                } else {
                    println("NO SE PUDO PARSEAR LA RESPUESTA")
                }

            } catch (e: Exception) {
                println("EXCEPCION: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun clearTaskDetail() {
        selectedTaskDetail = null
    }

    private fun getFileName(contentResolver: android.content.ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
    }


    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set

    fun loadNotifications(token: String) {
        viewModelScope.launch {
            val response = api.getNotifications("Bearer $token")
            if (response.isSuccessful) {
                notifications = response.body()?.data ?: emptyList()
            }
        }
    }
    fun markAsRead(token: String, id: Long) {
        viewModelScope.launch {
            api.markNotificationAsRead("Bearer $token", id)
            loadNotifications(token)
        }
    }


}