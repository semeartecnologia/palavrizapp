package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import android.graphics.Bitmap
import com.semear.tec.palavrizapp.models.Essay
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

    fun getEssayList(onCompletion: ((ArrayList<Essay>) -> Unit)){
        val user = sessionManager.userLogged
        realtimeRepository.getEssayList(user.userId, onCompletion)
    }
}