package com.semear.tec.palavrizapp.modules.admin.themes

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.semear.tec.palavrizapp.models.Themes
import com.semear.tec.palavrizapp.utils.repositories.ThemesRepository

class ListThemesViewModel(application: Application) : AndroidViewModel(application) {

    var listThemes = MutableLiveData<ArrayList<Themes>>()
    private val themesRepository = ThemesRepository(application)

    fun fetchThemes(){
        themesRepository.getTheme {
            listThemes.postValue(it)
        }
    }

    fun saveTheme(themes: Themes,  onCompletion: () -> Unit){
        themesRepository.saveTheme(themes, onCompletion)
    }

    fun editTheme(newDocument: Boolean, themes: Themes,  onCompletion: () -> Unit){
        themesRepository.editTheme(newDocument, themes, onCompletion)
    }
}
