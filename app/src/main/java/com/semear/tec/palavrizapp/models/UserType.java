package com.semear.tec.palavrizapp.models;

/**
 * Tipos de usuário do sistema
 */
public enum UserType {

    /**
     * Todos estudantes
     */
    STUDENT(0),
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

}
