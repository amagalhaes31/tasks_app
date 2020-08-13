package com.example.tasks.service.repository.remote

import com.example.tasks.service.HeaderModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PersonService {

    @POST("Authentication/Login")                                                             // Usar o método POST e o endereço de acesso
    @FormUrlEncoded                                                                                 // Formato do body
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<HeaderModel>                                                                            // Tipo do retorno
}