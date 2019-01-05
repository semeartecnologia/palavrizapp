package com.semear.tec.palavrizapp.repositories;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


import com.semear.tec.palavrizapp.interfaces.ThemesDao;
import com.semear.tec.palavrizapp.models.Themes;

import static com.semear.tec.palavrizapp.utils.Constants.DB_NAME;
import static com.semear.tec.palavrizapp.utils.Constants.DB_VERSION;

@Database(entities = {Themes.class}, version = DB_VERSION)
public abstract class DbManager extends RoomDatabase {

    public static DbManager instance;
    public abstract ThemesDao themesDao();

    public static DbManager getInstance(Context ctx){
        if ( instance == null ){
            instance = Room.databaseBuilder(ctx, DbManager.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

}
