package mx.edu.utez.moveapp.data.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val startDate: String = "01/01/2026",
    val endDate: String = "01/01/2026"
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}