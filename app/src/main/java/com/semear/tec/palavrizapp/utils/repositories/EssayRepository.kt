package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import android.graphics.Bitmap
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.EssayListUser
import com.semear.tec.palavrizapp.models.StatusEssay
import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.utils.interfaces.EssayUploadCallback

class EssayRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var storageRepository: StorageRepository = StorageRepository(context)
    private var sessionManager: SessionManager = SessionManager(context)

    fun saveEssay(essay: Essay, userId: String, bmpImage: Bitmap?, callback: EssayUploadCallback){
        if (userId.isNotBlank() && bmpImage != null) {
            storageRepository.uploadEssay(essay, userId, bmpImage, callback)
        }
    }

    fun getEssayListByUser(onCompletion: ((ArrayList<Essay>) -> Unit)){
        val user = sessionManager.userLogged
        realtimeRepository.getEssayListByUser(user. userId, onCompletion)
    }

    fun getEssayImage(filename: String, onCompletion: ((String) -> Unit)){
        storageRepository.getEssay(filename, onCompletion)
    }

    fun downloadEssayImage(filename: String, onCompletion: ((Boolean) -> Unit)){
        storageRepository.downloadEssay(filename, onCompletion)
    }

    fun getEssayList(onCompletion: ((ArrayList<Essay>) -> Unit)){
        realtimeRepository.getEssayList(onCompletion)
    }

    fun getEssayWaitingById(essayId: String,  onCompletion: (Essay?) -> Unit, onFail: () -> Unit){
        realtimeRepository.getEssayWaitingById(essayId, onCompletion, onFail)
    }

    fun getEssayWaitingListenerChange(essayId: String, onChange: (Essay?) -> Unit, onFail: () -> Unit){
        realtimeRepository.getEssayWaitingListenerChange(essayId, onChange, onFail)
    }

    fun setFeedbackOwnerOnEssay(essay: Essay, user: User, status: StatusEssay, onCompletion: () -> Unit){
        realtimeRepository.setFeedbackOwnerOnEssay(essay, user, status, onCompletion)
    }

    fun setEssayUnreadablaStatus(essay: Essay, userId: String, onCompletion: () -> Unit){
        realtimeRepository.setEssayUnreadableStatus(essay, userId, onCompletion)
    }

    fun getEssayDoneList(themeId: String, author: User, onCompletion: (ArrayList<Essay>) -> Unit){
        realtimeRepository.getEssayDoneList(themeId, author, onCompletion)
    }

    fun uploadVideoFeedback(urlVideo: String, onCompletion: (String?) -> Unit){
        storageRepository.uploadVideoFeedback(urlVideo, onCompletion)
    }

    fun downloadVideoFeedback(urlVideo: String, onCompletion: (String?) -> Unit){
        storageRepository.downloadVideoFeedback(urlVideo, onCompletion)
    }
}