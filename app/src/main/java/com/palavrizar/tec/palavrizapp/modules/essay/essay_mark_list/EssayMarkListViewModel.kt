package com.palavrizar.tec.palavrizapp.modules.essay.essay_mark_list

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.utils.repositories.EssayRepository

class EssayMarkListViewModel(application: Application) : AndroidViewModel(application) {

    private var essayRepository = EssayRepository(application)
    var essayListLiveData = MutableLiveData<ArrayList<Essay>>()
    var essayDoneListLiveData = MutableLiveData<ArrayList<Essay>>()


    fun getEssayList(){
        essayRepository.getEssayList {
            essayListLiveData.postValue(it)
            getEssayDoneList()
        }
    }

    fun getEssayDoneList(){
        essayRepository.getEssayDoneList{
            essayDoneListLiveData.postValue(it)
        }
    }

}