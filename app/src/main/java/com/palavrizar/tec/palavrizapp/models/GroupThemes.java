package com.palavrizar.tec.palavrizapp.models;

/**
 * classe pra pegar do banco de dados todos os temas com seus grupos
 */
public class GroupThemes {

    private int id;
    private String groupName;
    private String temaConceito;
    private String themeName;


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

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
