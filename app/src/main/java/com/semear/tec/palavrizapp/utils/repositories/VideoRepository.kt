package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.semear.tec.palavrizapp.models.*

class VideoRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var storageRepository = StorageRepository(context)
    private var sessionManager: SessionManager = SessionManager(context)

    fun getVideoList(onCompletion: ((ArrayList<Video>) -> Unit)){
        realtimeRepository.getVideosList(sessionManager.userPlan, onCompletion)
    }

    fun getAllVideos(onCompletion: ((ArrayList<Video>) -> Unit)){
        realtimeRepository.getVideosList(Plans.NO_PLAN, onCompletion)
    }

    fun getVideoDownloadUrl(path: String, onCompletion: ((String) -> Unit)){
        storageRepository.getVideoDownloadUrl(path,onCompletion)
    }

    fun getThumnailDownloadUrl(path: String, onCompletion: ((String) -> Unit)){
        storageRepository.getThumbUrl(path, onCompletion)
    }

    fun editVideo(video: Video, onCompletion: () -> Unit){
        realtimeRepository.editVideo(video, onCompletion)
    }

    fun editVideoOrder(videoList: ArrayList<Video>, onCompletion: (Boolean) -> Unit){
        realtimeRepository.editVideoOrder(videoList, onCompletion)
    }

    fun deleteVideo(video: Video, onCompletion: (() -> Unit)){
        realtimeRepository.deleteVideo(video.videoKey) {
            if (it){
                storageRepository.deleteVideoFromStorage(video.path) {
                    onCompletion()
                }
            }
        }
    }

    fun getThemes(onCompletion: (ArrayList<Themes>) -> Unit){
        realtimeRepository.getThemes(onCompletion)
    }

    fun getVideosStructureList(onCompletion: ((ArrayList<Structure>) -> Unit)){
        realtimeRepository.getVideosStructureList(onCompletion)
    }

    fun saveStructure(structure: Structure, onCompletion: () -> Unit){
        realtimeRepository.saveStructure(structure, onCompletion)
    }

    fun saveConcept(concept: Concept, onCompletion: () -> Unit){
        realtimeRepository.saveConcept(concept, onCompletion)
    }

    fun getVideosConceptList(onCompletion: ((ArrayList<Concept>) -> Unit)){
        realtimeRepository.getVideosConceptList(onCompletion)
    }

}