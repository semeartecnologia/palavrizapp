package com.semear.tec.palavrizapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.semear.tec.palavrizapp.models.GroupThemes;
import com.semear.tec.palavrizapp.repositories.ThemesRepository;

import java.util.List;

public class ThemesViewModel extends AndroidViewModel {

    private ThemesRepository themesRepository;

    public ThemesViewModel(@NonNull Application application) {
        super(application);
        themesRepository = new ThemesRepository(application);
    }

    public LiveData<List<GroupThemes>> getAllThemesWithGroups(){ return themesRepository.getAllGroupsWithTemes(); }

}
