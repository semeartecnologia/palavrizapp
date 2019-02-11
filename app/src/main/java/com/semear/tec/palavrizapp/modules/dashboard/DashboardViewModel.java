package com.semear.tec.palavrizapp.modules.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.utils.repositories.DbManager;
import com.semear.tec.palavrizapp.utils.repositories.SessionManager;

public class DashboardViewModel extends AndroidViewModel {

    SessionManager sessionManager;
    DbManager dbManager;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        sessionManager = new SessionManager(getApplication());
        dbManager = DbManager.getInstance(getApplication());
        dbManager.insertInitialData(getApplication());
    }

    public User getCurrentUser(){
        return sessionManager.getUserLogged();
    }
}