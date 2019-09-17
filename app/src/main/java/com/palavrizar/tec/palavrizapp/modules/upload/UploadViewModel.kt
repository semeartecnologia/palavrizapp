package com.palavrizar.tec.palavrizapp.modules.upload

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import com.palavrizar.tec.palavrizapp.models.*
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.repositories.PlansRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.RealtimeRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.VideoRepository

class UploadViewModel(application: Application): AndroidViewModel(application) {

    var deleteVideoLiveData = MutableLiveData<Boolean>()
    var videoRepository = VideoRepository(application)
    var plansRepository = PlansRepository(application)
    var editVideoSuccessLiveData = MutableLiveData<Boolean>()
    var realtimeRepository = RealtimeRepository(application)

    var structuresListLiveData = MutableLiveData<ArrayList<Structure>>()
    var conceptsListLiveData = MutableLiveData<ArrayList<Concept>>()
    var themeListLiveData = MutableLiveData<ArrayList<Themes>>()
    var planListLiveData = MutableLiveData<ArrayList<PlansBilling>>()
    var videoDownloadUrl = MutableLiveData<String>()

    fun uploadVideo(context: Context, video: Video, isIntro: Boolean = false){
        val intent = Intent(context, UploadService::class.java)
        intent.putExtra(Constants.EXTRA_VIDEO, video)
        intent.putExtra(Constants.EXTRA_VIDEO_FIRST, isIntro)
        context.startService(intent)
    }

    fun getVideoIntroUploadedAlready(onCompletion: ((Boolean) -> Unit)){
        videoRepository.getVideosUploadedAlready(onCompletion)
    }

    fun editVideoToIntro(video: Video){
        realtimeRepository.saveVideoIntro(video)
    }

    fun editVideo(video: Video?){
        if (video != null) {
            videoRepository.editVideo(video) {
                editVideoSuccessLiveData.postValue(true)
            }
        }
    }

    fun getVideoUrlDownload(path: String){
        videoRepository.getVideoDownloadUrl(path){
            videoDownloadUrl.postValue(it)
        }
    }

    fun deleteVideo(video: Video,  keepStorage: Boolean = false){
        videoRepository.deleteVideo(video, keepStorage){
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