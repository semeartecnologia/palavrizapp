package com.semear.tec.palavrizapp.utils.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.semear.tec.palavrizapp.models.Groups;
import com.semear.tec.palavrizapp.utils.repositories.DbManager;

import java.util.List;

public class GroupsRepository {

    private DbManager dbManager;

    public GroupsRepository(Context context){
        dbManager = DbManager.getInstance(context);
    }

    public LiveData<List<Groups>> getAllGroups(){
        return dbManager.groupsDao().getAllGroups();
    }
}
