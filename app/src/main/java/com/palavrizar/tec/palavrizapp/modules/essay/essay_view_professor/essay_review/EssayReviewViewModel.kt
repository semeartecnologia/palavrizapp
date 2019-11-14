package com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.essay_review

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.models.Feedback
import com.palavrizar.tec.palavrizapp.models.StatusEssay
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.repositories.EssayRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager

class EssayReviewViewModel(application: Application): AndroidViewModel(application) {

    sealed class Dialog {
        class FailDialog(val titleResource: Int, val messageResource: Int) : Dialog()
    }

    sealed class ViewEvent {
        class SuccessSettingOwner(val essayId: String): ViewEvent()
        object FailSettingOwner: ViewEvent()
        object EssayUnreadable: ViewEvent()
    }

    var actualEssayLiveData = MutableLiveData<Essay>()
    var essayImageUrlLiveData = MutableLiveData<String>()
    var essayImageUrlFullScreenLiveData = MutableLiveData<String>()
    var enableCorrectButton = MutableLiveData<Boolean>()


    val dialogLiveData = MutableLiveData<Dialog?>()
    val viewEventLiveData = MutableLiveData<ViewEvent>()


    private var essayRepository = EssayRepository(getApplication())
    private var sessionManager =  SessionManager(getApplication())



    fun setExtras(bundle: Bundle){
        val essay: Essay? = bundle.getParcelable(Constants.EXTRA_ESSAY)
        if (essay != null) {
            actualEssayLiveData.postValue(essay)
            val feedback = essay.feedback
            if (feedback != null && feedback.user?.userId != sessionManager.userLogged.userId){
                enableCorrectButton.postValue(false)
            }else{
                enableCorrectButton.postValue(true)
            }
        }
    }

    fun getEssayImage(url: String?){
        essayRepository.getEssayImage(url ?: "") {
            essayImageUrlLiveData.postValue(it)
        }
    }
    fun showImageFullscreenClicked(url: String?){
        essayRepository.getEssayImage(url ?: "") {
            essayImageUrlFullScreenLiveData.postValue(it)
        }
    }


    fun setEssayUnreadable(){
        val userLogged = sessionManager.userLogged
        essayRepository.setEssayUnreadablaStatus(actualEssayLiveData.value ?: return, userLogged.userId) {
            viewEventLiveData.postValue(ViewEvent.EssayUnreadable)
        }
    }

    fun onButtonCorrectClicked(essayId: String){
        essayRepository.getEssayWaitingById(essayId,
                {
                    //OnSuccess
                    val essay = it
                    if (essay != null && essay.feedback == null){
                        //nao tem feedback ainda registrado, entao ta liberado pra salvar
                        val userLogged = sessionManager.userLogged
                        essay.feedback = Feedback(userLogged, null, null)

                        essayRepository?.setFeedbackOwnerOnEssay(essay, userLogged, StatusEssay.CORRECTING) {
                            //sucesso!! agora vc ta corrigindo
                            viewEventLiveData.postValue(ViewEvent.SuccessSettingOwner(essayId))
                        }
                    }else{
                        //ops, provavelmente alguem clicou no mesmo tempo, ai azedo o caldo, avisa o usuario
                        dialogLiveData.postValue(Dialog.FailDialog(R.string.dialog_essay_feedback_already_taken_title, R.string.dialog_essay_feedback_already_taken_content))
                        viewEventLiveData.postValue(ViewEvent.FailSettingOwner)
                    }
                }, {
            //OnFail
            dialogLiveData.postValue(Dialog.FailDialog(R.string.dialog_essay_feedback_already_taken_title, R.string.dialog_essay_feedback_already_taken_content))
            viewEventLiveData.postValue(ViewEvent.FailSettingOwner)
        })
    }
}