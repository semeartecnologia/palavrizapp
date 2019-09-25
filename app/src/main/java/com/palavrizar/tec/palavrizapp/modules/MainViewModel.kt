package com.palavrizar.tec.palavrizapp.modules

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.models.Video
import com.palavrizar.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.VideoRepository


class MainViewModel(application: Application): AndroidViewModel(application){

    private lateinit var sessionManager : SessionManager
    private val videoRepository = VideoRepository(application)

    fun initViewModel(){
        sessionManager = SessionManager(getApplication())
    }

    fun startClassroomActivity(context: Context) {
        val it = Intent(context, ClassroomActivity::class.java)
        videoRepository.getVideoIntro {video ->
            it.putExtra(Constants.EXTRA_VIDEO, video)
            it.putExtra(Constants.EXTRA_VIDEO_FIRST, true)
            context.startActivity(it)

        }

    }



}