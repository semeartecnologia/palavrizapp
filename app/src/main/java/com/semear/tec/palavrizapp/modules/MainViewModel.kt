package com.semear.tec.palavrizapp.modules

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.modules.upload.UploadService
import com.semear.tec.palavrizapp.utils.constants.Constants
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

    fun getUserOnline(): User? {
        return sessionManager.userLogged
    }

    fun logout(){
        sessionManager.setUserOffline()
        mAuth.signOut()
        loginManager.logOut()
        isUserOnline.postValue(false)
    }



}