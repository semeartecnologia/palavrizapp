package com.semear.tec.palavrizapp.modules.admin.videos

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository

class ListVideosViewModel(application: Application) : AndroidViewModel(application) {

    private var videoRepository = VideoRepository(application)
    var videoListLiveData = MutableLiveData<ArrayList<Video>>()

    fun fetchVideos(){
        videoRepository.getAllVideos {
            videoListLiveData.postValue(it)
        }
    }
}
