package mx.edu.utez.moveapp.data.response

data class ValidateCodeResponse(
    val status: Int,
    val passwordToken: String
)