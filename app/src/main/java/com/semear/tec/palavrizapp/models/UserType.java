package com.semear.tec.palavrizapp.models;

/**
 * Tipos de usuário do sistema
 */
public enum UserType {

    /**
     * Todos estudantes do plano 1 (mudar nome)
     */
    ESTUDANTE_PLANO1(0),
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

}
