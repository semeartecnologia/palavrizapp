package com.semear.tec.palavrizapp.modules.upload

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import com.semear.tec.palavrizapp.models.*
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.repositories.PlansRepository
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository

class UploadViewModel(application: Application): AndroidViewModel(application) {

    var deleteVideoLiveData = MutableLiveData<Boolean>()
    var videoRepository = VideoRepository(application)
    var plansRepository = PlansRepository(application)
    var editVideoSuccessLiveData = MutableLiveData<Boolean>()

    var structuresListLiveData = MutableLiveData<ArrayList<Structure>>()
    var conceptsListLiveData = MutableLiveData<ArrayList<Concept>>()
    var themeListLiveData = MutableLiveData<ArrayList<Themes>>()
    var planListLiveData = MutableLiveData<ArrayList<PlansBilling>>()

    fun uploadVideo(context: Context, video: Video){
        val intent = Intent(context, UploadService::class.java)
        intent.putExtra(Constants.EXTRA_VIDEO, video)
        context.startService(intent)
    }

    fun editVideo(video: Video?){
        if (video != null) {
            videoRepository.editVideo(video) {
                editVideoSuccessLiveData.postValue(true)
            }
        }
    }

    fun deleteVideo(video: Video){
        videoRepository.deleteVideo(video){
            deleteVideoLiveData.postValue(true)
        }
    }

    fun getVideoStructureList(){
        videoRepository.getVideosStructureList {
            structuresListLiveData.postValue(it)
        }
    }

    fun getVideoThemeList(){
        videoRepository.getThemes {
            themeListLiveData.postValue(it)
        }
    }

    fun saveStructure(structure: Structure){
        videoRepository.saveStructure(structure){}
    }

    fun saveConcept(concept: Concept){
        videoRepository.saveConcept(concept){}
    }

    fun getVideoConceptList(){
        videoRepository.getVideosConceptList{
            conceptsListLiveData.postValue(it)
        }
    }

    fun getSinglePlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        plansRepository.getSinglePlans(onCompletion)
    }
}