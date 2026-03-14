package mx.edu.utez.moveapp.data.repository

import mx.edu.utez.moveapp.data.network.RetrofitInstance
import mx.edu.utez.moveapp.data.request.*

class AuthRepository {

    suspend fun sendCode(email: String) =
        RetrofitInstance.api.sendCode(email)

    suspend fun validateCode(email: String, code: String) =
        RetrofitInstance.api.validateCode(
            ValidateCodeRequest(email, code)
        )

    suspend fun addPassword(
        email: String,
        password: String,
        passwordToken: String
    ) =
        RetrofitInstance.api.addPassword(
            AddPasswordRequest(email, password, passwordToken)
        )

    suspend fun login(email: String, password: String) =
        RetrofitInstance.api.login(
            LoginRequest(email, password)
        )
}