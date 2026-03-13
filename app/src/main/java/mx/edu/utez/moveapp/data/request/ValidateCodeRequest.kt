package mx.edu.utez.moveapp.data.request

data class ValidateCodeRequest(
    val email: String,
    val code: String
)