package com.semear.tec.palavrizapp.models;

import java.util.Arrays;

/**
 * Tipos de usuário do sistema
 */
public enum UserType {

    /**
     * Todos estudantes
     */
    ESTUDANTE(0),
    /**
     * Corretores
     */
    CORRETOR(1),
    /**
     * Acesso Geral
     */
    ADMINISTRADOR(2);

    private final int userType;

    UserType(int userType) {
        this.userType = userType;
    }

    public int getUserType(){
        return userType;
    }

    public static String[] names() {
        return Arrays.toString(UserType.values()).replaceAll("^.|.$", "").split(", ");
    }

}
