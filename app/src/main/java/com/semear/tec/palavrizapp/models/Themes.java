package com.semear.tec.palavrizapp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.semear.tec.palavrizapp.utils.Constants.TABLE_THEMES;

@Entity(tableName = TABLE_THEMES,
        indices = {@Index("groupId")},
        foreignKeys = @ForeignKey(entity = Groups.class,
        parentColumns = "id",
        childColumns = "groupId",
        onDelete = CASCADE))
public class Themes {

    @PrimaryKey
    @NonNull
    private int themeId;

    @NonNull
    private int groupId; // Foreign Key
    private String themeName;

    public Themes(@NonNull int themeId, int groupId, String themeName) {
        this.themeId = themeId;
        this.groupId = groupId;
        this.themeName = themeName;
    }

    @NonNull
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
}
