package mx.edu.utez.moveapp.data.repository

import mx.edu.utez.moveapp.data.network.RetrofitInstance

class AdviserRepository {

    suspend fun getAdvisers(token: String) =
        RetrofitInstance.api.getAdvisers("Bearer $token")
}