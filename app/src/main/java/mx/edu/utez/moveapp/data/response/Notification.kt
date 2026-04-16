package mx.edu.utez.moveapp.data.response

import com.google.gson.annotations.SerializedName


data class Notification(
    @SerializedName("notificationID")
    val id: Long,

    @SerializedName("message")
    val message: String,

    @SerializedName("isRead")
    val read: Boolean,

    val taskId: Long
)

data class NotificationResponse(
    val status: Int,
    val data: List<Notification>
)