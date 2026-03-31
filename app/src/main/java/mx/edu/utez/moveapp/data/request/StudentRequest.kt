package mx.edu.utez.moveapp.data.request

data class StudentRequest(

    val firstname: String,
    val lastname: String,
    val gender: String,
    val email: String,
    val logo: String? = null

)