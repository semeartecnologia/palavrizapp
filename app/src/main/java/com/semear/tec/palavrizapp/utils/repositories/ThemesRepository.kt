package com.semear.tec.palavrizapp.utils.repositories

import android.arch.lifecycle.LiveData
import android.content.Context

import com.semear.tec.palavrizapp.models.GroupThemes
import com.semear.tec.palavrizapp.models.Themes

/**
 * Repositorio para o Banco de Dados de Temas
 */
class ThemesRepository(context: Context) {

    private val realtimeRepository = RealtimeRepository(context)
    private val storageRepository = StorageRepository(context)

    fun saveTheme(themes: Themes) {
        return realtimeRepository.saveTheme(themes)
    }

    fun getTheme(onCompletion: (ArrayList<Themes>) -> Unit){
        return realtimeRepository.getThemes(onCompletion)
    }

    fun uploadPdf(theme: Themes, onCompletion: (Boolean) -> Unit){
        return storageRepository.uploadPdf(theme, onCompletion)
    }

    fun downloadPdf(filename: String, onCompletion: ((String) -> Unit)){
        return storageRepository.getPdf(filename,onCompletion)
    }

    fun deleteTheme(themeId: String){
        return realtimeRepository.deleteTheme(themeId)
    }
}
