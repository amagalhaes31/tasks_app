package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tasks.service.HeaderModel
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.repository.PersonRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mPersonRepository = PersonRepository()

    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object: APIListener {
            override fun onSuccess(model: HeaderModel) {
                var s = ""
            }

            override fun onFailure(message: String) {

            }
        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyLoggedUser() {
    }

}