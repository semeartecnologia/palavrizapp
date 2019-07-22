package com.palavrizar.tec.palavrizapp.modules.videocatalog

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.palavrizar.tec.palavrizapp.models.Concept
import com.palavrizar.tec.palavrizapp.models.Structure
import com.palavrizar.tec.palavrizapp.models.Themes

import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.VideoRepository

class VideoCatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val themesRepository = ThemesRepository(application)
    private val videoRepository = VideoRepository(application)
    private val sessionManager = SessionManager(getApplication())

    var structuresListLiveData = MutableLiveData<ArrayList<Structure>>()
    var conceptsListLiveData = MutableLiveData<ArrayList<Concept>>()
    var themeListLiveData = MutableLiveData<ArrayList<Themes>>()

    var namePlanLiveData = MutableLiveData<String>()

    fun getPlanName() {
       namePlanLiveData.postValue(sessionManager.userPlan)
    }

    fun getVideoThemeList(){
        videoRepository.getThemes {
            themeListLiveData.postValue(it)
        }
    }

    fun getVideoConceptList(){
        videoRepository.getVideosConceptList{
            conceptsListLiveData.postValue(it)
        }
    }

    fun getVideoStructureList(){
        videoRepository.getVideosStructureList {
            structuresListLiveData.postValue(it)
        }
    }


}
