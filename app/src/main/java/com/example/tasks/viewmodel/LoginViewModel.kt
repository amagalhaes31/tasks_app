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
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.local.SecurityPreferences

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // Atributos
    private val mPersonRepository = PersonRepository(application)
    private val mPriorityRepository = PriorityRepository(application)
    private val mSharedPreferences = SecurityPreferences(application)                               // Declaração do SharedPrefences

    // Observa o valor de mLogin (Verificação do acesso a API)
    private val mLogin = MutableLiveData<ValidationListener>()
    var login: LiveData<ValidationListener> = mLogin

    // Verifica se o usuário está logado
    private val mLoggedUser = MutableLiveData<Boolean>()
    var loggedUser: LiveData<Boolean> = mLoggedUser


    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object : APIListener {
            override fun onSuccess(model: HeaderModel) {

                // Armazena os dados de login do usuário
                mSharedPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                // Exito ao logar na API
                mLogin.value = ValidationListener()
            }

            override fun onFailure(message: String) {

                // Falha ao logar na API
                mLogin.value = ValidationListener(message)
            }
        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyLoggedUser() {

        val loggedUser : Boolean;
        val token = mSharedPreferences.get(TaskConstants.SHARED.TOKEN_KEY)                   // Leitura do Token na sharedPreferences
        val personKey = mSharedPreferences.get(TaskConstants.SHARED.PERSON_KEY)              // Leitura do Person Key na sharedPreferences

        loggedUser = (token != "" && personKey != "")                                               // Se tiver logado, retorna true, caso contrário, false

        if(!loggedUser) {                                                                           // Chamda silenciosa vista pelo usuário
            mPriorityRepository.all();
        }
        mLoggedUser.value = loggedUser;
    }

}