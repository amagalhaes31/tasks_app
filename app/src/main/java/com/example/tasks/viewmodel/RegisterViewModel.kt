package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.local.SecurityPreferences

class RegisterViewModel(application: Application) : AndroidViewModel(application) {


    private val mPersonRepository = PersonRepository(application)
    private val mSharedPreferences =
        SecurityPreferences(application)                                                            // Declaração do SharedPrefences

    // Observa o valor de mLogin (Verificação do acesso a API)
    private val mCreate = MutableLiveData<ValidationListener>()
    var create: LiveData<ValidationListener> = mCreate

    fun create(name: String, email: String, password: String) {

        mPersonRepository.create(name, email,password, object : APIListener{
            override fun onSuccess(model: HeaderModel) {

                // Armazena os dados de login do usuário
                mSharedPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                mCreate.value = ValidationListener()
            }

            override fun onFailure(message: String) {
                // Falha ao logar na API
                mCreate.value = ValidationListener(message)
            }
        })

    }

}