package com.example.techhive_app.data.remote.retrofitbuilder

import com.example.techhive_app.data.remote.retrofit.AuthApi
import com.example.techhive_app.data.remote.retrofit.ProductApi
import com.example.techhive_app.data.remote.retrofit.ContactApi
import com.example.techhive_app.data.remote.retrofit.PedidoApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// NOTA para futuro yo:
// - Emulador: usar IP local del PC (10.0.2.2)
// - Dispositivo real :  http://10.0.2.2:PUERTO/
// - Tablet real: misma IP del PC en la red WiFi

object RemoteModule {

    // Emulador Android hablando con tu PC (localhost:8081)
    // Cambia la IP según tu red local
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


    //PRODCUTOS
    private const val PRODUCT_BASE_URL = "http://192.168.1.96:8082/"

    val productApi: ProductApi by lazy {
        Retrofit.Builder()
            .baseUrl(PRODUCT_BASE_URL)     // con slash al final
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApi::class.java)
    }

    //CONTACTO
    private const val CONTACT_BASE_URL = "http://192.168.1.96:8085/"

    val contactApi: ContactApi by lazy {
        Retrofit.Builder()
            .baseUrl(CONTACT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContactApi::class.java)
    }

    //PEDIDOS
    private const val PEDIDO_BASE_URL = "http://192.168.1.96:8084/"

    val pedidoApi: PedidoApi by lazy {
        Retrofit.Builder()
            .baseUrl(PEDIDO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PedidoApi::class.java)
    }

}
