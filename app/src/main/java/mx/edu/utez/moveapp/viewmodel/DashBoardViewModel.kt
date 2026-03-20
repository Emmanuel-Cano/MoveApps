package mx.edu.utez.moveapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.model.TaskStatus

class DashboardViewModel : ViewModel() {


    var advisorName by mutableStateOf("Cargando asesor...")
    var advisorImage by mutableStateOf("")
    var tasks by mutableStateOf(listOf<Task>())
        private set

    init {
        loadMockData()
    }

    private fun loadMockData() {
        tasks = listOf(
            Task(1, "Corregir base de datos", "La base de datos no esta correcta", TaskStatus.TODO),
            Task(2, "Terminar aplicacion web", "Faltan cosas sencillas", TaskStatus.IN_PROGRESS),
            Task(3, "Login funcional", "Ya conecta todo con Retrofit", TaskStatus.DONE)
        )
        advisorName = "Ocampo Vargas Ricardo Naul"
    }

    val todoTasks
        get() = tasks.filter { it.status == TaskStatus.TODO }

    val progressTasks
        get() = tasks.filter { it.status == TaskStatus.IN_PROGRESS }

    val doneTasks
        get() = tasks.filter { it.status == TaskStatus.DONE }


    fun moveTask(taskId: Int, newStatus: TaskStatus) {
        tasks = tasks.map { task ->
            if (task.id == taskId) {
                task.copy(status = newStatus)
            } else {
                task
            }
        }
    }



}