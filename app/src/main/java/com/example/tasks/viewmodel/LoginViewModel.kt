package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.helper.FingerPrintHelper
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.local.SecurityPreferences
import com.example.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // Atributos
    private val mPersonRepository = PersonRepository(application)
    private val mPriorityRepository = PriorityRepository(application)
    private val mSharedPreferences = SecurityPreferences(application)                               // Declaração do SharedPrefences

    // Observa o valor de mLogin (Verificação do acesso a API)
    private val mLogin = MutableLiveData<ValidationListener>()
    var login: LiveData<ValidationListener> = mLogin

    // Verifica se o usuário está logado
    private val mFingerPrint = MutableLiveData<Boolean>()
    var fingerPrint: LiveData<Boolean> = mFingerPrint

    fun isAuthenticationAvailable() {

        val tokenKey = mSharedPreferences.get(TaskConstants.SHARED.TOKEN_KEY)                // Leitura do Token na sharedPreferences
        val personKey = mSharedPreferences.get(TaskConstants.SHARED.PERSON_KEY)              // Leitura do Person Key na sharedPreferences

        // Se tiver logado, retorna true, caso contrário, false
        val everLoggedUser : Boolean = (tokenKey != "" && personKey != "")

        // Insere as informações do Header do HTTP
        RetrofitClient.addHeader(tokenKey, personKey)

        // Chamda silenciosa vista pelo usuário
        if(!everLoggedUser) {
            mPriorityRepository.all();
        }

        // Verifica a disponibilidade do leitor biométrico do dispositivo móvel
        if(FingerPrintHelper.isAuthenticationAvailable(getApplication())) {
            mFingerPrint.value = everLoggedUser
        }
    }

    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object : APIListener <HeaderModel> {
            override fun onSuccess(model: HeaderModel) {

                // Armazena os dados de login do usuário
                mSharedPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                // Insere as informações do Header do HTTP
                RetrofitClient.addHeader(model.token, model.personKey)

                // Exito ao logar na API
                mLogin.value = ValidationListener()
            }

            override fun onFailure(message: String) {

                // Falha ao logar na API
                mLogin.value = ValidationListener(message)
            }
        })
    }

}
