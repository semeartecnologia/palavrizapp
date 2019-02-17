package com.semear.tec.palavrizapp.modules

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.semear.tec.palavrizapp.utils.repositories.SessionManager

class MainViewModel(application: Application): AndroidViewModel(application){

    private lateinit var sessionManager : SessionManager
    var isUserOnline = MutableLiveData<Boolean>()
    private lateinit var mAuth : FirebaseAuth
    private lateinit var loginManager : LoginManager



    fun initViewModel(){
        sessionManager = SessionManager(getApplication())
        mAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()
    }

    fun logout(){
        sessionManager.setUserOffline()
        mAuth.signOut()
        loginManager.logOut()
        isUserOnline.postValue(false)
    }
}