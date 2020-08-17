package com.example.tasks.service.repository

import android.content.Context
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.model.TaskModel
import com.example.tasks.service.repository.remote.RetrofitClient
import com.example.tasks.service.repository.remote.TaskService
import com.google.gson.Gson

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TaskRepository(val context: Context) {

    private val mRemote = RetrofitClient.createService(TaskService::class.java)

    fun create(task: TaskModel, listener: APIListener<Boolean>) {

        val call: Call<Boolean> =
            mRemote.create(task.priorityId, task.description, task.dueData, task.complete)

        call.enqueue(object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))       // Mensagem padrão de erro (res/values/strings.xml)
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
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