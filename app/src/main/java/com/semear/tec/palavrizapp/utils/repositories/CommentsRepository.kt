package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.semear.tec.palavrizapp.models.Comment

class CommentsRepository(val context: Context) {

    private val realtimeRepository = RealtimeRepository(context)

    fun saveComment(comment: Comment, videoKey: String, onCompletion: () -> Unit){
        return realtimeRepository.saveComment(comment,videoKey, onCompletion)
    }

}