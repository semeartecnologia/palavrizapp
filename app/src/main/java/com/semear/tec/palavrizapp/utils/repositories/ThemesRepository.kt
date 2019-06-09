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

    fun saveTheme(themes: Themes, onCompletion: () -> Unit) {
        if (!themes.urlPdf.isNullOrBlank()){
            storageRepository.uploadPdf(themes){
                if (it != null) {
                    realtimeRepository.saveTheme(it, onCompletion)
                }else{
                    realtimeRepository.saveTheme(themes, onCompletion)
                }
            }
        }else{
            return realtimeRepository.saveTheme(themes, onCompletion)
        }

    }

    fun editTheme(newDocument: Boolean, themes: Themes, onCompletion: () -> Unit){
        if (!themes.urlPdf.isNullOrBlank() && newDocument){
            storageRepository.uploadPdf(themes){
                if (it != null) {
                    realtimeRepository.editTheme(it, onCompletion)
                }else{
                    realtimeRepository.editTheme(themes, onCompletion)
                }
            }
        }else{
            return realtimeRepository.editTheme(themes, onCompletion)
        }
    }

    fun getTheme(onCompletion: (ArrayList<Themes>) -> Unit){
        return realtimeRepository.getThemes(onCompletion)
    }

   /* fun uploadPdf(theme: Themes, onCompletion: (Boolean) -> Unit){
        return storageRepository.uploadPdf(theme, onCompletion)
    }*/

    fun downloadPdf(path: String, onCompletion: ((String) -> Unit)){
        return storageRepository.getPdf(path,onCompletion)
    }

    fun deleteTheme(themeId: String){
        return realtimeRepository.deleteTheme(themeId)
    }
}
