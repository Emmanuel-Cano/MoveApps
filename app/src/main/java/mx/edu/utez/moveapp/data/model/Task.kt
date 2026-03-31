package mx.edu.utez.moveapp.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("taskID") val id: Int,
    @SerializedName("name") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("statusKanban") val status: String,
    @SerializedName("color") val color: String? = "#FFFFFF",

    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("limitDate") val limitDate: String? = null,

    @SerializedName("priority") val priority: String? = null,
    @SerializedName("fileName") val fileName: String? = null
)

enum class TaskStatus {
    @SerializedName("ToDo") TODO,
    @SerializedName("Doing") IN_PROGRESS,
    @SerializedName("Done") DONE
}