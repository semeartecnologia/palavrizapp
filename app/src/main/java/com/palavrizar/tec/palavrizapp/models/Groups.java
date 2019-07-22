package com.palavrizar.tec.palavrizapp.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static com.palavrizar.tec.palavrizapp.utils.constants.Constants.TABLE_GROUPS;

@Entity(tableName = TABLE_GROUPS)
public class Groups {

    @PrimaryKey
    @NonNull
    private int id;

    private String groupName;
    private String temaConceito;

    public Groups(@NonNull int id, String groupName, String temaConceito) {
        this.id = id;
        this.groupName = groupName;
        this.temaConceito = temaConceito;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTemaConceito() {
        return temaConceito;
    }

    public void setTemaConceito(String temaConceito) {
        this.temaConceito = temaConceito;
    }
}
