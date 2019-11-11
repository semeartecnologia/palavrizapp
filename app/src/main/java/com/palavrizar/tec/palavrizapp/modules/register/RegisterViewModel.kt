package com.palavrizar.tec.palavrizapp.modules.register

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.palavrizar.tec.palavrizapp.models.EmailWhitelist
import com.palavrizar.tec.palavrizapp.models.LocationBlacklist
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.models.UserType
import com.palavrizar.tec.palavrizapp.modules.welcome.WelcomeActivity
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.COMMON_ERROR
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.USER_ALREADY_REGISTERED_ERROR
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository

class RegisterViewModel(application: Application): AndroidViewModel(application){


    private var mAuth: FirebaseAuth? = null
    private lateinit var userRepository : UserRepository
    private lateinit var sessionManager : SessionManager

    var isLoading = MutableLiveData<Boolean>()
    var showMessageMissingFields = MutableLiveData<Boolean>()
    var showMessageErrorRegister = MutableLiveData<Int>()
    var showMessagePwdNotMatch = MutableLiveData<Boolean>()


    fun initViewModel(){
        mAuth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(getApplication())
        userRepository = UserRepository(getApplication())
    }
    /**
     * Método para fazer registro pelo EMAIL
     */
    fun registerWithEmail(activity: Activity, email: String, password: String, confirmPassword: String, fullname: String) {
        if (checkFields(fullname, email, password, confirmPassword)) {
            isLoading.postValue(true)
            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            isLoading.postValue(false)
                            getUserDataAndLogin(fullname)
                        } else {
                            isLoading.postValue(false)
                            if (task.exception?.localizedMessage == "The email address is already in use by another account."){
                                showMessageErrorRegister.postValue(USER_ALREADY_REGISTERED_ERROR)
                            }else{
                                showMessageErrorRegister.postValue(COMMON_ERROR)
                            }

                        }

                    }
        }
    }

    fun getBlacklist(onCompletion: ((ArrayList<LocationBlacklist>) -> Unit)){
        userRepository.getLocationBlacklist(onCompletion)
    }
    fun getWhitelist(onCompletion: ((ArrayList<EmailWhitelist>) -> Unit)){
        userRepository.getLoginWhitelist(onCompletion)
    }

    fun checkFields(fullname: String, email: String, password: String, confirmPassword: String) : Boolean{
        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {
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
        user.essayCredits = 0
        if (user.photoUri == null)
            user.photoUri = ""
        //tipo e plano padrao, depois tem que trocar isso
        user.userType = UserType.ESTUDANTE
        user.plan = Constants.PLAN_FREE_ID
        //Salva Login no cache Shared Preferences
        user.registerDate = System.currentTimeMillis()
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