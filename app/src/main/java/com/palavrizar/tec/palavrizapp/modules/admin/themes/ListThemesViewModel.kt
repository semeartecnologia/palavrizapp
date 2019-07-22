package com.palavrizar.tec.palavrizapp.modules.admin.themes

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository

class ListThemesViewModel(application: Application) : AndroidViewModel(application) {

    var listThemes = MutableLiveData<ArrayList<Themes>>()
    var filePathLiveData = MutableLiveData<String>()
    private val themesRepository = ThemesRepository(application)

    fun fetchThemes(){
        themesRepository.getTheme {
            listThemes.postValue(it)
        }
    }

    fun downloadPdf(path: String){
        themesRepository.downloadPdf(path){
            filePathLiveData.postValue(it)
        }
    }

    fun saveTheme(themes: Themes,  onCompletion: () -> Unit){
        themesRepository.saveTheme(themes, onCompletion)
    }

    fun editTheme(newDocument: Boolean, themes: Themes,  onCompletion: () -> Unit){
        themesRepository.editTheme(newDocument, themes, onCompletion)
    }

    fun deleteTheme(themeId: String){
        if (themeId.isEmpty()) return
        themesRepository.deleteTheme(themeId)
    }
}
