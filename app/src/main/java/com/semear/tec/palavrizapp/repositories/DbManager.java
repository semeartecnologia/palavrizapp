package com.semear.tec.palavrizapp.repositories;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.semear.tec.palavrizapp.interfaces.GroupsDao;
import com.semear.tec.palavrizapp.interfaces.ThemesDao;
import com.semear.tec.palavrizapp.models.Groups;
import com.semear.tec.palavrizapp.models.Themes;
import com.semear.tec.palavrizapp.utils.InitData;

import static com.semear.tec.palavrizapp.utils.Constants.DB_NAME;
import static com.semear.tec.palavrizapp.utils.Constants.DB_VERSION;

@Database(entities = {Themes.class, Groups.class}, version = DB_VERSION)
public abstract class DbManager extends RoomDatabase {

    private static DbManager instance;
    abstract ThemesDao themesDao();
    abstract GroupsDao groupsDao();

    public static DbManager getInstance(Context ctx){
        if ( instance == null ){
            instance = Room.databaseBuilder(ctx, DbManager.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public void insertInitialData(Context ctx){
        Log.d("DebugDB", "inserting");
        getInstance(ctx).groupsDao().insertAllGroup(InitData.getInitialGroups());
        getInstance(ctx).themesDao().insertAllThemes(InitData.getInitialThemes());
    }

}
