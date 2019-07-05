package com.semear.tec.palavrizapp.modules.upload

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import com.semear.tec.palavrizapp.models.Concept
import com.semear.tec.palavrizapp.models.Structure
import com.semear.tec.palavrizapp.models.Themes
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository

class UploadViewModel(application: Application): AndroidViewModel(application) {

    var deleteVideoLiveData = MutableLiveData<Boolean>()
    var videoRepository = VideoRepository(application)
    var editVideoSuccessLiveData = MutableLiveData<Boolean>()

    var structuresListLiveData = MutableLiveData<ArrayList<Structure>>()
    var conceptsListLiveData = MutableLiveData<ArrayList<Concept>>()
    var themeListLiveData = MutableLiveData<ArrayList<Themes>>()

    fun uploadVideo(context: Context, video: Video){
        val intent = Intent(context, UploadService::class.java)
        intent.putExtra(Constants.EXTRA_VIDEO, video)/*
        intent.putExtra(Constants.EXTRA_FILE_URI, video.path)
        intent.putExtra(Constants.EXTRA_FILE_TITLE, video.title)
        intent.putExtra(Constants.EXTRA_FILE_DESCRIPTION, video.description)
        intent.putExtra(Constants.EXTRA_FILE_CATEGORY, video.category)
        intent.putExtra(Constants.EXTRA_FILE_THUMBNAIL, video.videoThumb)
        intent.putExtra(Constants.EXTRA_FILE_PLANS, video.videoPlan)*/
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
}