package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import android.graphics.Bitmap
import com.semear.tec.palavrizapp.models.Essay

class EssayRepository(val context: Context) {

    private var realtimeRepository: RealtimeRepository = RealtimeRepository(context)
    private var storageRepository: StorageRepository = StorageRepository(context)

    fun saveEssay(essay: Essay, userId: String, bmpImage: Bitmap?){
        if (userId.isNotBlank() && bmpImage != null) {
            storageRepository.uploadEssay(essay, userId, bmpImage)
        }
    }

    fun getEssayList(userId: String){
        realtimeRepository.getEssayList(userId)
    }
}