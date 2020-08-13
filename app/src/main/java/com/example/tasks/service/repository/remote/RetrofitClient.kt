package com.example.tasks.service.repository.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


// Constates
private const val BASE_URL = "http://devmasterteam.com/CursoAndroidAPI/"

// Padrão -  Singleton
class RetrofitClient private constructor() {

    companion object {

        private lateinit var retrofit: Retrofit

        private fun getRetrofitInstance(): Retrofit {

            val httpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            if (!Companion::retrofit.isInitialized) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit
        }

        fun <T> createService(serviceClass: Class<T>): T {
            return getRetrofitInstance()
                .create(serviceClass)
        }
    }
}