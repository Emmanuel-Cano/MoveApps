package mx.edu.utez.moveapp.data.response

import com.google.gson.annotations.SerializedName

data class AttachmentDto(
    @SerializedName("fileName") val fileName: String,
    @SerializedName("url") val url: String
)