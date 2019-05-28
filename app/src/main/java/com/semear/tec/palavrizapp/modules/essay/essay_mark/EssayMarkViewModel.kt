package com.semear.tec.palavrizapp.modules.essay.essay_mark

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.EssayListUser
import com.semear.tec.palavrizapp.utils.repositories.EssayRepository

class EssayMarkViewModel(application: Application) : AndroidViewModel(application) {

    private var essayRepository = EssayRepository(application)
    var essayList = MutableLiveData<ArrayList<Essay>>()


    fun getEssayList(){
        essayRepository.getEssayList {
            essayList.postValue(it)
        }
    }

}