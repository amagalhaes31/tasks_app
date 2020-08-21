package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.repository.local.SecurityPreferences

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mSharedPreferences = SecurityPreferences(application)                               // Declaração do SharedPrefences

    private val mPersonName = MutableLiveData<String>()
    var personName: LiveData<String> = mPersonName

    private val mLogout = MutableLiveData<Boolean>()
    var logout: LiveData<Boolean> = mLogout

    fun loadUserName(){
        mPersonName.value = mSharedPreferences.get(TaskConstants.SHARED.PERSON_NAME)
    }

    fun logout() {
        mSharedPreferences.remove(TaskConstants.SHARED.TOKEN_KEY)
        mSharedPreferences.remove(TaskConstants.SHARED.PERSON_KEY)
        mSharedPreferences.remove(TaskConstants.SHARED.PERSON_NAME)
        mLogout.value = true
    }
}