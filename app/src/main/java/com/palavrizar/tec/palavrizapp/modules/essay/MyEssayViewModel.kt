package com.palavrizar.tec.palavrizapp.modules.essay

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository

class MyEssayViewModel(application: Application) : AndroidViewModel(application) {

    private val themesRepository = ThemesRepository(application)
    private val userRepository = UserRepository(application)
    private val sessionManager = SessionManager(application)

    val userHasCreditLiveData = MutableLiveData<Boolean>()
    val dialogThemesLiveData = MutableLiveData<ArrayList<Themes>>()
    val userNoPlanLiveData = MutableLiveData<Boolean>()

    fun fetchThemes(onThemePicked: ((ArrayList<Themes>)-> Unit)){
        themesRepository.getTheme {
            onThemePicked.invoke(it)
        }
    }

    fun sendEssayClicked(){
        userRepository.userHasSoloCredits(sessionManager.userLogged.userId){ hasSoloCredits ->
            if (getUserPlan() == Constants.PLAN_FREE_ID && !hasSoloCredits ){
                userNoPlanLiveData.postValue(true)
            }else{
                if (hasSoloCredits){
                    startSendEssayFlow()
                }else {
                    checkAndGoIfHaveCredit()
                }
            }
        }

    }

    private fun startSendEssayFlow(){
        userHasCreditLiveData.postValue(true)
        fetchThemes {
            dialogThemesLiveData.postValue(it)
        }
    }

    private fun checkAndGoIfHaveCredit(){
        userRepository.userHasCredit(sessionManager.userLogged.userId) {
            if (it) {
                startSendEssayFlow()
            }else{
                userHasCreditLiveData.postValue(false)
            }

        }
    }

    fun getUserPlan(): String? {
        return sessionManager.userPlan
    }

    fun downloadPdf(path: String, onDownloaded: ((String)-> Unit)){
        themesRepository.downloadPdf(path){
            onDownloaded(it)
        }
    }

}