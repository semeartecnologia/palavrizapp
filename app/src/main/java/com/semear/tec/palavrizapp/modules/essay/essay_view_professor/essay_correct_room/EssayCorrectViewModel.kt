package com.semear.tec.palavrizapp.modules.essay.essay_view_professor.essay_correct_room

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.Feedback
import com.semear.tec.palavrizapp.models.StatusEssay
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.repositories.EssayRepository
import com.semear.tec.palavrizapp.utils.repositories.SessionManager

class EssayCorrectViewModel(application: Application): AndroidViewModel(application) {


    sealed class ViewEvent {
        class FeedBackSent(val success: Boolean) : ViewEvent()
    }

    private var essayRepository = EssayRepository(getApplication())
    private var sessionManager =  SessionManager(getApplication())

    var actualEssay = MutableLiveData<Essay>()
    var userPermitionAccessRoom = MutableLiveData<Boolean>()
    var essayImageUrlLiveData = MutableLiveData<String>()
    var viewEvent = MutableLiveData<ViewEvent>()


    fun setExtras(bundle: Bundle){
        val essay: Essay? = bundle.getParcelable(Constants.EXTRA_ESSAY)
        val isReadMode: Boolean = bundle.getBoolean(Constants.EXTRA_ESSAY_READ_MODE)
        if (essay != null) {
            checkOwnerEssay(essay.essayId)
            if (isReadMode){
                essay.isReadMode = true
            }
            actualEssay.postValue(essay)
        }
    }

    fun getEssayImage(url: String?){
        essayRepository.getEssayImage(url ?: "") {
            essayImageUrlLiveData.postValue(it)
        }
    }

    fun onSendEssayFeedback(actualEssay: Essay, feedbackText: String, urlVideo: String = ""){
        actualEssay.feedback = Feedback(sessionManager.userLogged, urlVideo, feedbackText)
        essayRepository.setFeedbackOwnerOnEssay(actualEssay, sessionManager.userLogged, StatusEssay.FEEDBACK_READY) {
            //sucesso!! agora vc ta corrigindo
            viewEvent.postValue(ViewEvent.FeedBackSent(true))
        }
    }

    private fun checkOwnerEssay(essayId: String){
        essayRepository.getEssayWaitingListenerChange(essayId,
                {
                    essayRepository.getEssayWaitingById(essayId,
                            {
                                if (it != null){
                                    val feedback = it.feedback
                                    if (feedback != null){
                                        if (sessionManager.userLogged.userId != feedback.user?.userId){
                                            //nao eh teu id que ta la, avisa e volta pra lista
                                            userPermitionAccessRoom.postValue(false)
                                        }else{
                                            //eh vc mermo, sucesso,
                                            userPermitionAccessRoom.postValue(true)
                                            //viewEvent.postValue(ViewEvent.UserConfirmed(true, feedback))
                                        }
                                    }else{
                                        userPermitionAccessRoom.postValue(false)
                                        //viewEvent.postValue(ViewEvent.FeedbackNotFound)
                                    }
                                }else{
                                    userPermitionAccessRoom.postValue(false)
                                   // viewEvent.postValue(ViewEvent.FeedbackNotFound)
                                }
                            },
                            {
                                userPermitionAccessRoom.postValue(false)
                            })
                },{

        })
    }
}