package com.semear.tec.palavrizapp.modules.themes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.semear.tec.palavrizapp.utils.repositories.SessionManager;
import com.semear.tec.palavrizapp.utils.repositories.ThemesRepository;

public class ThemesViewModel extends AndroidViewModel {

    private ThemesRepository themesRepository;
    private SessionManager sessionManager = new SessionManager(getApplication());

    public MutableLiveData<String> namePlanLiveData = new MutableLiveData<>();

    public ThemesViewModel(@NonNull Application application) {
        super(application);
        themesRepository = new ThemesRepository(application);
    }


    public void getPlanName(){
        namePlanLiveData.postValue(sessionManager.getUserPlan().name());
    }

}
