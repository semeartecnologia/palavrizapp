package com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.palavrizar.tec.palavrizapp.models.Feedback
import com.palavrizar.tec.palavrizapp.utils.repositories.EssayRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager

class EssayViewModel(application: Application): AndroidViewModel(application) {


    sealed class ViewEvent {
        class UserConfirmed(val success: Boolean, feedback: Feedback) : ViewEvent()
        object EssayNotFound: ViewEvent()
        object FeedbackNotFound: ViewEvent()
    }

    private var essayRepository = EssayRepository(getApplication())
    private var sessionManager =  SessionManager(getApplication())

    var viewEvent = MutableLiveData<ViewEvent>()

    fun checkEssayFeedbackOwner(essayId: String){
        essayRepository.getEssayWaitingListenerChange(essayId,
                {
                    essayRepository.getEssayWaitingById(essayId,
                            {
                                if (it != null){
                                    val feedback = it.feedback
                                    if (feedback != null){
                                        if (sessionManager.userLogged.userId != feedback.user?.userId){
                                            //nao eh teu id que ta la, avisa e volta pra lista
                                            viewEvent.postValue(ViewEvent.UserConfirmed(false, feedback))
                                        }else{
                                            //eh vc mermo, sucesso,
                                            viewEvent.postValue(ViewEvent.UserConfirmed(true, feedback))
                                        }
                                    }else{
                                        viewEvent.postValue(ViewEvent.FeedbackNotFound)
                                    }
                                }else{
                                    viewEvent.postValue(ViewEvent.FeedbackNotFound)
                                }
                            },
                            {
                                viewEvent.postValue(ViewEvent.EssayNotFound)
                            })
        },{

        })
    }
}