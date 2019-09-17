package com.palavrizar.tec.palavrizapp.modules.admin

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.palavrizar.tec.palavrizapp.models.EmailWhitelist
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


    fun deleteLocationBlacklisted(location: LocationBlacklist){
        userRepository.deleteLocationBlacklist(location)
    }


    fun saveLoginWhitelist(location: EmailWhitelist){
        userRepository.saveLoginWhitelist(location)
    }

    fun getLoginWhitelist(onCompletion: ((ArrayList<EmailWhitelist>) -> Unit)){
        userRepository.getLoginWhitelist(onCompletion)
    }


    fun deleteLoginWhitelist(location: EmailWhitelist){
        userRepository.deleteLoginWhitelist(location)
    }



}