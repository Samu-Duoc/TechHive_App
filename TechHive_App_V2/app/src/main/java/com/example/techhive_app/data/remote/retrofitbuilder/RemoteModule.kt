package com.example.techhive_app.data.remote.retrofitbuilder

import com.example.techhive_app.data.remote.retrofit.AuthApi
import com.example.techhive_app.data.remote.retrofit.ProductApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteModule {

    // Emulador Android hablando con tu PC (localhost:8081)
    // ⚠️ Cambia la IP según tu red local
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

    // AUTH
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    // 👇 NUEVO: API de productos + categorías (tu MS productos_categorias)
    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }

    // NOTA para futuro yo:
    // - Emulador: usar IP local del PC (10.0.2.2)
    // - Dispositivo real :  http://10.0.2.2:PUERTO/
    // - Tablet real: misma IP del PC en la red WiFi
}
