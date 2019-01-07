package com.semear.tec.palavrizapp.interfaces;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.semear.tec.palavrizapp.models.Themes;

import java.util.List;

import static com.semear.tec.palavrizapp.utils.Constants.TABLE_THEMES;

@Dao
public interface ThemesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTheme(Themes theme);

    @Query("SELECT * FROM " + TABLE_THEMES)
    LiveData<List<Themes>> getAllThemes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllThemes(Themes... themes);

}
