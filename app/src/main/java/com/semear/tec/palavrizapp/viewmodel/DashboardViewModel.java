package com.semear.tec.palavrizapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.repositories.SessionManager;

public class DashboardViewModel extends AndroidViewModel {

    SessionManager sessionManager;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        sessionManager = new SessionManager(getApplication());
    }

    public User getCurrentUser(){
        return sessionManager.getUserLogged();
    }
}
