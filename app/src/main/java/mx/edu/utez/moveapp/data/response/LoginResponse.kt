package mx.edu.utez.moveapp.data.response

data class LoginResponse(
    val token: String,
    val user: UserDTO
)

data class UserDTO(
    val email: String,
    val rol: String
)
object SessionManager {
    var token: String? = null
}