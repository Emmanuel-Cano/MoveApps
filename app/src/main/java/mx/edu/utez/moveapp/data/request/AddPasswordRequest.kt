package mx.edu.utez.moveapp.data.request

data class AddPasswordRequest(
    val email: String,
    val password: String,
    val passwordToken: String
)