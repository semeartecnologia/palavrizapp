package com.semear.tec.palavrizapp.modules.upload

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.constants.Constants

class UploadViewModel: ViewModel() {

    fun uploadVideo(context: Context, video: Video){
        val intent = Intent(context, UploadService::class.java)
        intent.putExtra(Constants.EXTRA_FILE_URI, video.path)
        intent.putExtra(Constants.EXTRA_FILE_TITLE, video.title)
        intent.putExtra(Constants.EXTRA_FILE_DESCRIPTION, video.description)
        intent.putExtra(Constants.EXTRA_FILE_CATEGORY, video.category)
        intent.putExtra(Constants.EXTRA_FILE_THUMBNAIL, video.videoThumb)
        intent.putExtra(Constants.EXTRA_FILE_PLANS, video.videoPlan)
        context.startService(intent)
    }
}