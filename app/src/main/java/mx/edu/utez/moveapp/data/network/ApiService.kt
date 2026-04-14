package mx.edu.utez.moveapp.data.network

import mx.edu.utez.moveapp.data.model.Task
import mx.edu.utez.moveapp.data.request.AddPasswordRequest
import mx.edu.utez.moveapp.data.request.ChangePasswordRequest
import mx.edu.utez.moveapp.data.request.ChangeStatusRequest
import mx.edu.utez.moveapp.data.request.LoginRequest
import mx.edu.utez.moveapp.data.request.StudentRequest
import mx.edu.utez.moveapp.data.request.UpdateNameRequest
import mx.edu.utez.moveapp.data.request.ValidateCodeRequest
import mx.edu.utez.moveapp.data.response.AdviserResponse
import mx.edu.utez.moveapp.data.response.LoginResponse
import mx.edu.utez.moveapp.data.response.StudentProfileResponse
import mx.edu.utez.moveapp.data.response.SuccessResponse
import mx.edu.utez.moveapp.data.response.ValidateCodeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/user/login") suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/user/sendCode") suspend fun sendCode(@Query("email") email: String): Response<Unit>

    @POST("api/user/validateCode") suspend fun validateCode(@Body request: ValidateCodeRequest): Response<ValidateCodeResponse>

    @POST("api/user/addPassword") suspend fun addPassword(@Body request: AddPasswordRequest): Response<Unit>

    @POST("api/student") suspend fun saveStudentInformation(@Body request: StudentRequest): Response<Unit>

    @POST("api/user/changePassword") suspend fun changePassword(@Query("email") email: String): Response<SuccessResponse>

    @GET("api/student/advisers") suspend fun getAdvisers(@Header("Authorization") token: String): Response<List<AdviserResponse>>

    @GET("api/student/profile") suspend fun getStudentProfile(): Response<StudentProfileResponse>


    @PUT("api/student/updateName") suspend fun updateStudentName(@Body request: UpdateNameRequest): Response<SuccessResponse>

    @Multipart @POST("api/student/uploadLogo") suspend fun uploadLogo(@Part file: MultipartBody.Part): Response<SuccessResponse>

    @GET("api/student/tasks/{boardID}") suspend fun getTasksByAdviser(@Path("boardID") adviserId: Int): Response<List<Task>>

    @Multipart @POST("api/student/submitEvidence") suspend fun submitEvidence(@Part("taskID") taskID: RequestBody, @Part("comment") comment: RequestBody, @Part files: List<MultipartBody.Part>): Response<SuccessResponse>

    @PUT("api/student/task/{taskID}/status") suspend fun changeTaskStatus(@Header("Authorization") token: String, @Path("taskID") taskId: Int, @Body request: ChangeStatusRequest): Response<SuccessResponse>

}