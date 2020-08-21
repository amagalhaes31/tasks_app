package com.example.tasks.service.repository

import android.content.Context
import com.example.tasks.R
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.repository.remote.PersonService
import com.example.tasks.service.repository.remote.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonRepository (val context: Context) : BaseRepository(context) {

    private val mRemote = RetrofitClient.createService(PersonService::class.java)

    fun login(email: String, password: String, listener: APIListener<HeaderModel>){

        // Verifica conexão com a internet
        if(!isConnectionAvailable(context)) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        val call: Call<HeaderModel> = mRemote.login(email, password)

        // Chamada assincrona da API
        call.enqueue(object : Callback<HeaderModel> {
            override fun onFailure(call: Call<HeaderModel>, t: Throwable) {                         // Se não conseguiu acesso a API (falha na comunicação)
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))                    // Mensagem padrão de erro (res/values/strings.xml)
            }

            override fun onResponse(call: Call<HeaderModel>, response: Response<HeaderModel>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {                                            // Falha ao realizar o login na API
                    val status = Gson().fromJson(response.errorBody()!!.string(), String::class.java)   // Conversão do Json do arquivo recebido
                    listener.onFailure(status)

                }
                else {                                                                              // Sucesso ao realizar login na API
                    response.body()?.let { listener.onSuccess(it) }
                }
            }
        })
    }

    fun create(name: String, email: String, password: String, listener: APIListener<HeaderModel>){

        // Verifica conexão com a internet
        if(!isConnectionAvailable(context)) {
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        val call: Call<HeaderModel> = mRemote.create(name, email, password, true)

        // Chamada assincrona da API
        call.enqueue(object : Callback<HeaderModel> {
            override fun onFailure(call: Call<HeaderModel>, t: Throwable) {                         // Se não conseguiu acesso a API (falha na comunicação)
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))       // Mensagem padrão de erro (res/values/strings.xml)
            }

            override fun onResponse(call: Call<HeaderModel>, response: Response<HeaderModel>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {                                            // Falha ao realizar o login na API
                    val status = Gson().fromJson(response.errorBody()!!.string(), String::class.java)   // Conversão do Json do arquivo recebido
                    listener.onFailure(status)

                }
                else {                                                                              // Sucesso ao realizar login na API
                    response.body()?.let { listener.onSuccess(it) }
                }
            }
        })
    }
}