package mx.edu.utez.moveapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.utez.moveapp.data.network.RetrofitInstance
import mx.edu.utez.moveapp.data.request.AddPasswordRequest
import mx.edu.utez.moveapp.data.request.LoginRequest
import mx.edu.utez.moveapp.data.request.StudentRequest
import mx.edu.utez.moveapp.data.request.ValidateCodeRequest
import mx.edu.utez.moveapp.data.response.SessionManager

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var code by mutableStateOf("")
    var passwordToken by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var goToVerifyCode by mutableStateOf(false)
    var goToCreatePassword by mutableStateOf(false)
    var goToHome by mutableStateOf(false)
    var userToken: String? by mutableStateOf(null)
    var authMode by mutableStateOf("REGISTER")
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onConfirmPasswordChange(newPassword: String) {
        confirmPassword = newPassword
    }

    fun onCodeChange(newCode: String) {
        code = newCode
    }

    fun sendCode() {

        if (email.isBlank()) {
            errorMessage = "Ingresa tu correo"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.sendCode(email)
                if (response.isSuccessful) {
                    goToVerifyCode = true
                } else {
                    errorMessage =
                        "Error"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error desconocido"
            }
            isLoading = false
        }
    }

    fun validateCode() {

        if (code.length < 6) {
            errorMessage = "Ingresa el codigo completo"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.validateCode(
                    ValidateCodeRequest(email, code)
                )
                if (response.isSuccessful) {
                    passwordToken = response.body()?.passwordToken ?: ""
                    goToCreatePassword = true
                } else {
                    errorMessage =
                        "Error"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error desconocido"
            }
            isLoading = false
        }
    }

    fun addPassword() {
        if (password.length < 10) {
            errorMessage = "La contraseña debe tener al menos 10 caracteres"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addPassword(
                    AddPasswordRequest(email, password, passwordToken)
                )
                if (response.isSuccessful) {
                    login()
                } else {
                    errorMessage =
                        "Error"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error desconocido"
            }
            isLoading = false
        }
    }

    fun changePassword() {
        if (email.isBlank()) {
            errorMessage = "Ingresa tu correo"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.changePassword(email)
                if (response.isSuccessful) {
                    goToVerifyCode = true
                } else {
                    errorMessage = "Error"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
            isLoading = false
        }
    }

    fun login() {

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Ingresa correo y contraseña"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {

                val response = RetrofitInstance.api.login(
                    LoginRequest(email, password)
                )

                if (response.isSuccessful) {

                    val tokenResponse = response.body()?.token ?: ""

                    userToken = tokenResponse
                    SessionManager.token = tokenResponse

                    goToHome = true

                } else {
                    errorMessage = "Error en login"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error desconocido"
            }

            isLoading = false
        }
    }

    fun clearSession() {
        email = ""
        password = ""
        confirmPassword = ""
        code = ""
        userToken = null
        errorMessage = null
        goToVerifyCode = false
        goToCreatePassword = false
        goToHome = false
        SessionManager.token = ""
    }
}