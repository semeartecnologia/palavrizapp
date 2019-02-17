package com.semear.tec.palavrizapp.modules

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.semear.tec.palavrizapp.utils.repositories.SessionManager

class MainViewModel(application: Application): AndroidViewModel(application){

    private lateinit var sessionManager : SessionManager
    var isUserOnline = MutableLiveData<Boolean>()



    fun initViewModel(){
        sessionManager = SessionManager(getApplication())
    }

    fun logout(){
        sessionManager.setUserOffline()
        isUserOnline.postValue(false)
    }
}