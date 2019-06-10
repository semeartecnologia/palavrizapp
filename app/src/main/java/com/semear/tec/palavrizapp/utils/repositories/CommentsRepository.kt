package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.semear.tec.palavrizapp.models.Comment
import com.semear.tec.palavrizapp.models.Reply

class CommentsRepository(val context: Context) {

    private val realtimeRepository = RealtimeRepository(context)

    fun saveComment(comment: Comment, videoKey: String, onCompletion: () -> Unit){
        return realtimeRepository.saveComment(comment,videoKey, onCompletion)
    }

    fun saveReply(reply: Reply, commentId: String, videoKey: String, onCompletion: () -> Unit){
        return realtimeRepository.saveReply(reply, commentId, videoKey, onCompletion)
    }

    fun loadComment(videoKey: String, onCompletion: (ArrayList<Comment>) -> Unit){
        return realtimeRepository.loadComments(videoKey, onCompletion)
    }

    fun loadReply(commentId: String,  onCompletion: (ArrayList<Reply>) -> Unit){
        return realtimeRepository.loadReply(commentId, onCompletion)
    }

}