package com.palavrizar.tec.palavrizapp.modules.admin.videos

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.palavrizar.tec.palavrizapp.models.Video
import com.palavrizar.tec.palavrizapp.utils.repositories.VideoRepository

class ListVideosViewModel(application: Application) : AndroidViewModel(application) {

    private var videoRepository = VideoRepository(application)
    var videoListLiveData = MutableLiveData<ArrayList<Video>>()
    var showProgressLiveData = MutableLiveData<Boolean>()
    var successEditOrderLiveData = MutableLiveData<Boolean>()

    fun fetchVideos(){
        videoRepository.getAllVideos {
            videoListLiveData.postValue(it)
        }
    }

    fun editVideoOrder(videoList: ArrayList<Video>){
        showProgressLiveData.postValue(true)
        videoRepository.editVideoOrder(videoList){
            showProgressLiveData.postValue(false)
            successEditOrderLiveData.postValue(it)
        }
    }
}
