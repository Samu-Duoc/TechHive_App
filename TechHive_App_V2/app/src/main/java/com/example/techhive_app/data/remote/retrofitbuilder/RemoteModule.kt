package com.example.techhive_app.data.remote.retrofitbuilder

import com.example.techhive_app.data.remote.retrofit.AuthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteModule {

    //PARA EMULADOR ANDROID: Si el backend corre en emulador usar: "http://10.0.2.2:8081/"

    // Emulador Android hablando con tu PC y dispositivo (localhost:8081)
    private const val BASE_URL = "http://192.168.1.96:8081/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}
