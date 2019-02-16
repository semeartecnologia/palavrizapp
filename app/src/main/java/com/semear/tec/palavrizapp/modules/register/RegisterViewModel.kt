package com.semear.tec.palavrizapp.modules.register

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.semear.tec.palavrizapp.models.Plans
import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.models.UserType
import com.semear.tec.palavrizapp.modules.welcome.WelcomeActivity
import com.semear.tec.palavrizapp.utils.repositories.SessionManager
import com.semear.tec.palavrizapp.utils.repositories.UserRepository

class RegisterViewModel(application: Application): AndroidViewModel(application){


    private var mAuth: FirebaseAuth? = null
    private lateinit var userRepository : UserRepository
    private lateinit var sessionManager : SessionManager

    private var isLoading = MutableLiveData<Boolean>()
    var showMessageMissingFields = MutableLiveData<Boolean>()
    var showMessageErrorRegister = MutableLiveData<Boolean>()
    var showMessagePwdNotMatch = MutableLiveData<Boolean>()


    fun initViewModel(){
        mAuth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(getApplication())
        userRepository = UserRepository()
    }
    /**
     * Método para fazer registro pelo EMAIL
     */
    fun registerWithEmail(activity: Activity, email: String, password: String, confirmPassword: String, fullname: String,
                          radioCheckedId: Int) {
        isLoading.postValue(true)
        if (checkFields(fullname, email, password, confirmPassword, radioCheckedId)) {
            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            isLoading.postValue(false)
                            getUserDataAndLogin(fullname)
                        } else {
                            isLoading.postValue(false)
                            showMessageErrorRegister.postValue(true)
                        }

                    }
        }else {
            isLoading.postValue(false)
        }
    }

    fun checkFields(fullname: String, email: String, password: String, confirmPassword: String,radioCheckedId: Int) : Boolean{
        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty() || radioCheckedId == -1) {
            showMessageMissingFields.postValue(true)
            return false
        }else
            if (!password.equals(confirmPassword)){
                showMessagePwdNotMatch.postValue(true)
                return false
            }
            return true
    }

    fun getUserDataAndLogin(fullname: String)
    {
        //pega o usuario corrente, independente do tipo de login
        val gUser = mAuth?.currentUser

        //cria o objeto
        val user = User()
        user.userId = gUser?.uid
        user.email = gUser?.email
        user.fullname = fullname
        user.photoUri = gUser?.photoUrl?.toString()
        if (user.photoUri == null)
            user.photoUri = ""
        //tipo e plano padrao, depois tem que trocar isso
        user.userType = UserType.STUDENT
        user.plan = Plans.FREE_PLAN

        //Salva Login no cache Shared Preferences
        sessionManager.setUserOnline(user, true)

        //registra usuario pelo repositorio
        userRepository.registerUser(user)

        startWelcomeActivity(user.photoUri, fullname.split(" ")[0])
        }

    /**
     * Chama a activity Welcome First Time
     */
    private fun startWelcomeActivity(photoUri: String, username: String?) {
        val it = Intent(getApplication(), WelcomeActivity::class.java)
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        it.putExtra("photoUri", photoUri)
        it.putExtra("username", username)
        getApplication<Application>().startActivity(it)

    }
}