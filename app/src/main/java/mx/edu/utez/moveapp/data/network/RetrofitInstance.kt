package mx.edu.utez.moveapp.data.network

import mx.edu.utez.moveapp.data.response.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

     private const val BASE_URL = "http://192.168.111.105:8080/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->

            val request = chain.request().newBuilder()

            SessionManager.token?.let {
                request.addHeader("Authorization", "Bearer $it")
            }

            chain.proceed(request.build())
        })
        .build()


    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}