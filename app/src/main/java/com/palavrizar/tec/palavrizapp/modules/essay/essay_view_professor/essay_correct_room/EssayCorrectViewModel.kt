package com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.essay_correct_room

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.models.Feedback
import com.palavrizar.tec.palavrizapp.models.StatusEssay
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.repositories.EssayRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager

class EssayCorrectViewModel(application: Application): AndroidViewModel(application) {


    sealed class ViewEvent {
        class FeedBackSent(val success: Boolean) : ViewEvent()
        object EssayUnreadable : ViewEvent()
    }

    private var essayRepository = EssayRepository(getApplication())
    private var sessionManager =  SessionManager(getApplication())

    var actualEssay = MutableLiveData<Essay>()
    var userPermitionAccessRoom = MutableLiveData<Boolean>()
    var essayImageUrlLiveData = MutableLiveData<String>()
    var showProgress = MutableLiveData<Boolean>()
    var viewEvent = MutableLiveData<ViewEvent>()


    fun setExtras(bundle: Bundle){
        val essay: Essay? = bundle.getParcelable(Constants.EXTRA_ESSAY)
        val isReadMode: Boolean = bundle.getBoolean(Constants.EXTRA_ESSAY_READ_MODE)


        if (essay != null) {
            if (isReadMode && essay.status == StatusEssay.NOT_READABLE){
                viewEvent.postValue(ViewEvent.EssayUnreadable)
            }

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

    fun downloadEssayImage(filename: String?, onCompletion: ((String) -> Unit)){
        essayRepository.downloadEssayImage(filename ?: "") {
            if (it) {
                onCompletion.invoke(filename ?: "")
            }
        }
    }

    fun downloadVideoFeedback(urlVideo: String, onCompletion: (String?) -> Unit){
        essayRepository.downloadVideoFeedback(urlVideo, onCompletion)
    }

    fun onSendEssayFeedback(actualEssay: Essay, feedbackText: String, urlVideo: String = ""){
        showProgress.postValue(true)
        if (urlVideo.isNotBlank()){
            essayRepository.uploadVideoFeedback(urlVideo){
                actualEssay.feedback = Feedback(sessionManager.userLogged, it, feedbackText)
                essayRepository.setFeedbackOwnerOnEssay(actualEssay, sessionManager.userLogged, StatusEssay.FEEDBACK_READY) {
                    viewEvent.postValue(ViewEvent.FeedBackSent(true))
                }
            }
        }else{
            actualEssay.feedback = Feedback(sessionManager.userLogged, urlVideo, feedbackText)
            essayRepository.setFeedbackOwnerOnEssay(actualEssay, sessionManager.userLogged, StatusEssay.FEEDBACK_READY) {
                viewEvent.postValue(ViewEvent.FeedBackSent(true))
            }
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