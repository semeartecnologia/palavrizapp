package com.semear.tec.palavrizapp.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.semear.tec.palavrizapp.models.GroupThemes;
import com.semear.tec.palavrizapp.models.Themes;

import java.util.List;

/**
 * Repositorio para o Banco de Dados de Temas
 */
public class ThemesRepository {

    private DbManager dbManager;

    public ThemesRepository(Context context){
        dbManager = DbManager.getInstance(context);
    }

    public LiveData<List<Themes>> getAllThemes(){
        return dbManager.themesDao().getAllThemes();
    }

    public LiveData<List<GroupThemes>> getAllGroupsWithTemes(){ return dbManager.groupsDao().getGroupsWithThemes();}
}
