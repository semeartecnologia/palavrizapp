package com.palavrizar.tec.palavrizapp.modules.essay

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository

class MyEssayViewModel(application: Application) : AndroidViewModel(application) {

    private val themesRepository = ThemesRepository(application)

    fun fetchThemes(onThemePicked: ((ArrayList<Themes>)-> Unit)){
        themesRepository.getTheme {
            onThemePicked.invoke(it)
        }
    }

    fun downloadPdf(path: String, onDownloaded: ((String)-> Unit)){
        themesRepository.downloadPdf(path){
            onDownloaded(it)
        }
    }

}