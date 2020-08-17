package com.example.tasks.service.repository.remote

import com.example.tasks.service.constants.TaskConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


// Constates
private const val BASE_URL = "http://devmasterteam.com/CursoAndroidAPI/"

// Padrão -  Singleton
class RetrofitClient private constructor() {

    companion object {

        private lateinit var retrofit: Retrofit
        private var personKey = ""
        private var tokenKey  = ""

        private fun getRetrofitInstance(): Retrofit {

            val httpClient = OkHttpClient.Builder()
                //.connectTimeout(60, TimeUnit.SECONDS)
                //.readTimeout(60, TimeUnit.SECONDS)
                //.build()

            // Intercepta a requisição HTTP
            httpClient.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request =
                        chain.request()
                            .newBuilder()
                            .addHeader(TaskConstants.HEADER.PERSON_KEY, personKey)
                            .addHeader(TaskConstants.HEADER.TOKEN_KEY, tokenKey)
                            .build()
                        return chain.proceed(request)
                }
            })

            if (!Companion::retrofit.isInitialized) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit
        }


        fun addHeader(token : String, personKey: String) {
            this.tokenKey = token
            this.personKey = personKey
        }


        fun <T> createService(serviceClass: Class<T>): T {
            return getRetrofitInstance()
                .create(serviceClass)
        }
    }
}