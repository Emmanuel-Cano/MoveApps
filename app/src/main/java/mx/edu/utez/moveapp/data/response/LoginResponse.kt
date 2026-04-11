package mx.edu.utez.moveapp.data.response

data class LoginResponse(
    val token: String
)

object SessionManager {
    var token: String? = null
}