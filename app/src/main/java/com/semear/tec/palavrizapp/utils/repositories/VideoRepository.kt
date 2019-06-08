package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.semear.tec.palavrizapp.models.Plans
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.models.VideoCategory

class VideoRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)

    fun getVideoList(category: String, onCompletion: ((ArrayList<Video>) -> Unit)){
        //TODO plans hardcoded
        realtimeRepository.getVideosList(Plans.FREE_PLAN, category, onCompletion)
    }

    fun getCategoryList(onCompletion: ((ArrayList<VideoCategory>) -> Unit)){
        realtimeRepository.getVideosCategoryList(onCompletion)
    }
}