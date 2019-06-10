package com.semear.tec.palavrizapp.modules.admin.users

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.models.UserType
import com.semear.tec.palavrizapp.utils.repositories.UserRepository

class ListUserViewModel(application: Application) : AndroidViewModel(application) {

    var listUser = MutableLiveData<ArrayList<User>>()
    private val userRepository = UserRepository(application)

    fun fetchUserList(){
        userRepository.getUserList {
            listUser.postValue(it)
        }
    }

    fun setUserType(userId: String, userType: UserType, onCompletion: () -> Unit){
        userRepository.setUserType(userId, userType, onCompletion)
    }

}