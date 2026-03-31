package mx.edu.utez.moveapp.data.response

data class StudentProfileResponse(
    val firstName: String,
    val lastName: String,
    val email: String,
    val logo: String? = null
)