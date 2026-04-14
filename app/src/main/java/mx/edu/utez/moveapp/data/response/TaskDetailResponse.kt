package mx.edu.utez.moveapp.data.response

import com.google.gson.annotations.SerializedName

data class TaskDetailResponse(
    @SerializedName("taskID") val taskID: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("statusKanban") val statusKanban: String,
    @SerializedName("color") val color: String?,
    @SerializedName("attachments") val attachments: List<Any> = emptyList(),

    @SerializedName("grade") val grade: Double?,
    @SerializedName("feedback") val feedback: String?
)