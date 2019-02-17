package com.semear.tec.palavrizapp.modules.classroom

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.semear.tec.palavrizapp.utils.repositories.SessionManager

class ClassroomViewModel(application: Application): AndroidViewModel(application){

    private lateinit var sessionManager : SessionManager
    var isUserFirstTime = MutableLiveData<Boolean>()

    fun initViewModel(){
        sessionManager = SessionManager(getApplication())
        if (sessionManager.isUserFirstTime){
            sessionManager.isUserFirstTime = false
            isUserFirstTime.postValue(true)
        }
    }
}