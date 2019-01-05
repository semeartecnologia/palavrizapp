package com.semear.tec.palavrizapp.interfaces;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.semear.tec.palavrizapp.models.Groups;

import java.util.List;

import static com.semear.tec.palavrizapp.utils.Constants.TABLE_GROUPS;

@Dao
public interface GroupsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(Groups group);

    @Query("SELECT * FROM " + TABLE_GROUPS)
    LiveData<List<Groups>> getAllGroups();
}
