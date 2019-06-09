package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.semear.tec.palavrizapp.models.Plans
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.models.VideoCategory

class VideoRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var storageRepository = StorageRepository(context)
    private var sessionManager: SessionManager = SessionManager(context)

    fun getVideoList(onCompletion: ((ArrayList<Video>) -> Unit)){
        realtimeRepository.getVideosList(sessionManager.userPlan, onCompletion)
    }

    fun getCategoryList(onCompletion: ((ArrayList<VideoCategory>) -> Unit)){
        realtimeRepository.getVideosCategoryList(onCompletion)
    }

    fun getVideoDownloadUrl(path: String, onCompletion: ((String) -> Unit)){
        storageRepository.getVideoDownloadUrl(path,onCompletion)
    }

    fun getThumnailDownloadUrl(path: String, onCompletion: ((String) -> Unit)){
        storageRepository.getThumbUrl(path, onCompletion)
    }

}