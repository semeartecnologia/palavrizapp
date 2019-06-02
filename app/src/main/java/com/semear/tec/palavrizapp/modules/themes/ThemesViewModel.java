package com.semear.tec.palavrizapp.modules.themes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.semear.tec.palavrizapp.utils.repositories.ThemesRepository;

public class ThemesViewModel extends AndroidViewModel {

    private ThemesRepository themesRepository;

    public ThemesViewModel(@NonNull Application application) {
        super(application);
        themesRepository = new ThemesRepository(application);
    }


}
