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
import mx.edu.utez.moveapp.data.request.ChangeStatusRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class DashboardViewModel : ViewModel() {

    var tasks by mutableStateOf(listOf<Task>())
    var isLoading by mutableStateOf(false)
    var advisorName by mutableStateOf("")

    fun loadData(boardId: Int?, name: String? = null) {
        if (boardId == null || boardId <= 0) return

        name?.let { advisorName = it }

        viewModelScope.launch {
            isLoading = true
            try {

                println("DEBUG_ID: adviserId=$boardId")
                println("DEBUG_TOKEN: ${mx.edu.utez.moveapp.data.response.SessionManager.token}")

                val response = RetrofitInstance.api.getTasksByAdviser(boardId)

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

                val response = RetrofitInstance.api.submitEvidence(
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

}