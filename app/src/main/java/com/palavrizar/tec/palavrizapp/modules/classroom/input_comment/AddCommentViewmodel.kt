package com.palavrizar.tec.palavrizapp.modules.classroom.input_comment

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.palavrizar.tec.palavrizapp.models.Comment
import com.palavrizar.tec.palavrizapp.models.Reply
import com.palavrizar.tec.palavrizapp.utils.repositories.CommentsRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager

class AddCommentViewmodel(application: Application): AndroidViewModel(application) {

    private var commentsRepository = CommentsRepository(application)
    private var sessionManager = SessionManager(application)

    fun addComment(commentText: String, videoKey: String, onCompletion: () -> Unit){
        val comment = Comment("", commentText, sessionManager.userLogged, 0 )
        commentsRepository.saveComment(comment, videoKey, onCompletion)
    }

    fun addReply(commentText: String, commentId: String, videoKey: String, onCompletion: () -> Unit){
        val reply = Reply(commentText, sessionManager.userLogged)
        commentsRepository.saveReply(reply, commentId, videoKey, onCompletion)
    }
}