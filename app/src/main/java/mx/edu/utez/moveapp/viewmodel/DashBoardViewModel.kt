package mx.edu.utez.moveapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.model.TaskStatus
import mx.edu.utez.moveapp.data.network.RetrofitInstance

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

                val response = RetrofitInstance.api.getTasksByAdviser(boardId)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        tasks = body

                        println("DEBUG_MOVE: ¡Petición Exitosa! Tareas: ${body.size}")
                        body.forEach { task ->
                            println("DEBUG_TASK: ID=${task.id}, Nombre=${task.title}, Inicio=${task.startDate}, Fin=${task.limitDate}")
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
        it.status.equals("ToDo", ignoreCase = true) || it.status.equals("TODO", ignoreCase = true)
    }

    val progressTasks get() = tasks.filter {
        it.status.equals("Doing", ignoreCase = true) || it.status.equals("IN_PROGRESS", ignoreCase = true)
    }

    val doneTasks get() = tasks.filter {
        it.status.equals("Done", ignoreCase = true) || it.status.equals("DONE", ignoreCase = true)
    }

    fun moveTask(taskId: Int, newStatus: TaskStatus) {
        tasks = tasks.map { task ->
            if (task.id == taskId) {
                task.copy(status = newStatus.name)
            } else {
                task
            }
        }
    }
}