package com.palavrizar.tec.palavrizapp.modules.admin

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.palavrizar.tec.palavrizapp.models.LocationBlacklist
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    fun saveLocationBlacklisted(location: LocationBlacklist){
        userRepository.saveLocationBlacklist(location)
    }

    fun getLocationBlacklisted(onCompletion: ((ArrayList<LocationBlacklist>) -> Unit)){
        userRepository.getLocationBlacklist(onCompletion)
    }

}