package mx.edu.utez.moveapp.data.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val status: TaskStatus
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE
}