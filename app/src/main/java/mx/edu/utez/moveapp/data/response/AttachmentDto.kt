package mx.edu.utez.moveapp.data.response

import com.google.gson.annotations.SerializedName

data class AttachmentDto(
    val id: Int,
    val fileName: String,
    val fileType: String,
    val file: String
)