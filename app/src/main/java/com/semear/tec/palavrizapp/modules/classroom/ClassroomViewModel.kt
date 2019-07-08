package com.semear.tec.palavrizapp.modules.classroom

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.semear.tec.palavrizapp.models.Comment
import com.semear.tec.palavrizapp.models.UserType
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.repositories.CommentsRepository
import com.semear.tec.palavrizapp.utils.repositories.SessionManager
import com.semear.tec.palavrizapp.utils.repositories.ThemesRepository
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete

class ClassroomViewModel(application: Application): AndroidViewModel(application){

    private lateinit var sessionManager : SessionManager
    var isUserFirstTime = MutableLiveData<Boolean>()
    var commentsLiveData = MutableLiveData<List<Comment>>()
    private var commentsRepository = CommentsRepository(application)
    private var videoRepository = VideoRepository(application)
    private var themesRepository= ThemesRepository(application)
    var videoDownloadUrl = MutableLiveData<String>()

    var showNextClassButton = MutableLiveData<Video?>()

    fun userCanReply(): Boolean{
        return sessionManager.userLogged.userType == UserType.CORRETOR || sessionManager.userLogged.userType == UserType.ADMINISTRADOR
    }

    fun initViewModel(){
        sessionManager = SessionManager(getApplication())
    }

    fun getVideoUrlDownload(path: String){
        videoRepository.getVideoDownloadUrl(path){
            videoDownloadUrl.postValue(it)
        }
    }

    fun downloadPdf(path: String, onDownloaded: ((String)-> Unit)){
        themesRepository.downloadPdf(path){
            onDownloaded(it)
        }
    }

    fun getNextVideo(actualOrder: String){
        videoRepository.getNextVideo(actualOrder){
            showNextClassButton.postValue(it)
        }
    }

    fun loadComments(videoKey: String){
        commentsRepository.loadComment(videoKey) {
            doAsync {
                val listComments = it
                val listFiltered = listComments.filter { comment -> comment.replyCount > 0 }
                if (listFiltered.isEmpty()){
                    commentsLiveData.postValue(listComments)
                }else {
                    listComments.forEach { comment ->
                        if (comment.replyCount > 0) {
                            if (!comment.id.isNullOrBlank()) {
                                commentsRepository.loadReply(comment.id!!) { listReply ->
                                    comment.listReply = listReply
                                    commentsLiveData.postValue(listComments)
                                }
                            }
                        }
                    }
                }

            }



        }

    }
}