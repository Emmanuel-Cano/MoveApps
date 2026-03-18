package mx.edu.utez.moveapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.model.TaskStatus

class DashboardViewModel : ViewModel() {


    var tasks by mutableStateOf(listOf<Task>())

    val todoTasks
        get() = tasks.filter { it.status == TaskStatus.TODO }

    val progressTasks
        get() = tasks.filter { it.status == TaskStatus.IN_PROGRESS }

    val doneTasks
        get() = tasks.filter { it.status == TaskStatus.DONE }
}