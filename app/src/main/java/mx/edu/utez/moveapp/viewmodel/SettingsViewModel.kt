package mx.edu.utez.moveapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.utez.moveapp.data.network.RetrofitInstance
import mx.edu.utez.moveapp.data.request.UpdateNameRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import android.util.Base64

class SettingsViewModel : ViewModel() {
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var profileImage by mutableStateOf<ByteArray?>(null)
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitInstance.api.getStudentProfile()
                if (response.isSuccessful) {
                    response.body()?.let {

                        println("IMAGE FROM BACKEND: ${it.logo}")

                        firstName = it.firstName
                        lastName = it.lastName
                        email = it.email
                        profileImage = if (it.logo.isNullOrEmpty()) {
                            null
                        } else {
                            Base64.decode(it.logo, Base64.DEFAULT)
                        }
                    }
                }
            } catch (e: Exception) {
                message = "Error al cargar perfil"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateName() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateStudentName(
                    UpdateNameRequest(firstName, lastName)
                )
                if (response.isSuccessful) {
                    message = "¡Nombre actualizado!"
                    loadProfile()
                }
            } catch (e: Exception) {
                message = "Error al actualizar nombre"
            }
        }
    }

    fun uploadProfileImage(file: File) {
        println("ENTRÓ A UPLOAD")

        viewModelScope.launch {
            try {
                println("ARCHIVO: ${file.name}")

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = RetrofitInstance.api.uploadLogo(body)

                println("RESPONSE CODE: ${response.code()}")

                if (response.isSuccessful) {
                    println("SUBIDA EXITOSA")
                    loadProfile()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}