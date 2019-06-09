package com.semear.tec.palavrizapp.modules.dashboard

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.semear.tec.palavrizapp.models.Themes

import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.utils.repositories.SessionManager
import com.semear.tec.palavrizapp.utils.repositories.ThemesRepository

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    var sessionManager: SessionManager = SessionManager(getApplication())
    var themesRepository: ThemesRepository = ThemesRepository(getApplication())

    val currentUser: User?
        get() = sessionManager.userLogged

}
