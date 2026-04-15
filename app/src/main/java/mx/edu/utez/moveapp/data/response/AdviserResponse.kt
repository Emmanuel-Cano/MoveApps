package mx.edu.utez.moveapp.data.response

import com.google.gson.annotations.SerializedName

data class AdviserResponse(
    @SerializedName("adviserID") val id: Long,
    @SerializedName("boardID") val boardId: Int,
    val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    val status: Boolean,
    val logo: String?
) {
    val name: String get() = "$firstName $lastName"
}