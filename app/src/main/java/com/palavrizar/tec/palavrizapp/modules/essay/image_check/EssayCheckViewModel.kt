package com.palavrizar.tec.palavrizapp.modules.essay.image_check

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository

class EssayCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val themesRepository = ThemesRepository(application)
    val dialogThemesLiveData = MutableLiveData<ArrayList<Themes>>()


    fun fetchThemes(){
        themesRepository.getTheme {
            dialogThemesLiveData.postValue(it)
        }
    }
}