package mx.edu.utez.moveapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.utez.moveapp.data.network.RetrofitInstance
import mx.edu.utez.moveapp.data.response.AdviserResponse // <--- IMPORTANTE
import mx.edu.utez.moveapp.data.response.SessionManager

class AdviserViewModel : ViewModel() {
    var advisers by mutableStateOf(listOf<AdviserResponse>())
    var isLoading by mutableStateOf(false)

    fun loadAdvisers() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitInstance.api.getAdvisers(
                    "Bearer ${SessionManager.token}"
                )
                println("DEBUG_TOKEN: ${mx.edu.utez.moveapp.data.response.SessionManager.token}")
                if (response.isSuccessful) {
                    advisers = response.body() ?: emptyList()
                    println("Asesores cargados: ${advisers.size}")
                    advisers.forEach {
                        println("DEBUG_DATA: Nombre: ${it.name}, ID real: ${it.id}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}