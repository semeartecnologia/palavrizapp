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




    fun saveTheme(themes: Themes) {
        return realtimeRepository.saveTheme(themes)
    }
}
