package com.semear.tec.palavrizapp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.semear.tec.palavrizapp.utils.constants.Constants.TABLE_THEMES;


public class Themes {


    private int themeId;

    private int groupId; // Foreign Key
    private String themeName;
    private String subgroup;
    private String urlPdf;

    public Themes(){}

    public Themes(String themeName, String urlPdf){
        this.themeName = themeName;
        this.urlPdf = urlPdf;
    }


    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(@NonNull int themeId) {
        this.themeId = themeId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }
}
