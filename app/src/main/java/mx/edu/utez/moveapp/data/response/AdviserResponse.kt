package mx.edu.utez.moveapp.data.response

import com.google.gson.annotations.SerializedName

data class AdviserResponse(
    val id: Long,
    val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    val status: Boolean,
    val logo: String?
) {
    val name: String get() = "$firstName $lastName"
}