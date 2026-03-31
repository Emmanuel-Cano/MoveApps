package mx.edu.utez.moveapp.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.utez.moveapp.data.network.RetrofitInstance
import mx.edu.utez.moveapp.data.repository.AdviserRepository
import mx.edu.utez.moveapp.data.response.AdviserResponse
import mx.edu.utez.moveapp.data.response.SessionManager

class AdviserViewModel : ViewModel() {
    private val repository = AdviserRepository()
    var advisers by mutableStateOf<List<AdviserResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)

    fun loadAdvisers() {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenHeader = "Bearer ${SessionManager.token}"
                val response = RetrofitInstance.api.getAdvisers(tokenHeader)

                if (response.isSuccessful) {
                    advisers = response.body() ?: emptyList()
                    println("Asesores cargados: ${advisers.size}")
                } else {
                    println("Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Error de red: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}