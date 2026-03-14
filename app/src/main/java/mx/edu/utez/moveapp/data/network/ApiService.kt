package mx.edu.utez.moveapp.data.network

import mx.edu.utez.moveapp.data.request.AddPasswordRequest
import mx.edu.utez.moveapp.data.request.ChangePasswordRequest
import mx.edu.utez.moveapp.data.request.LoginRequest
import mx.edu.utez.moveapp.data.request.StudentRequest
import mx.edu.utez.moveapp.data.request.ValidateCodeRequest
import mx.edu.utez.moveapp.data.response.LoginResponse
import mx.edu.utez.moveapp.data.response.SuccessResponse
import mx.edu.utez.moveapp.data.response.ValidateCodeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/user/login") suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/user/sendCode") suspend fun sendCode(@Query("email") email: String): Response<Unit>

    @POST("api/user/validateCode") suspend fun validateCode(@Body request: ValidateCodeRequest): Response<ValidateCodeResponse>

    @POST("api/user/addPassword") suspend fun addPassword(@Body request: AddPasswordRequest): Response<Unit>

    @POST("api/student") suspend fun saveStudentInformation(@Body request: StudentRequest): Response<Unit>

    @POST("api/user/changePassword") suspend fun changePassword(@Query("email") email: String): Response<SuccessResponse>
}