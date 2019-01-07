package com.semear.tec.palavrizapp.repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.semear.tec.palavrizapp.models.User;

/**
 * Repositorio para o Banco de Dados de Usuarios
 */
public class UserRepository {

    private FirebaseDatabase fDatabase;
    private DatabaseReference myRef;

    public UserRepository(){
        fDatabase = FirebaseDatabase.getInstance();
    }

    /**
     * Registra usuario no firebase
     * @param user
     */
    public void registerUser(User user){
        //novo usuario cadastrando email na mao
        if (user.getUserId() == null || user.getUserId().isEmpty()){
            user.setUserId(fDatabase.getReference("users/").push().getKey());
        }
        myRef = fDatabase.getReference("users/" + user.getUserId());
        myRef.setValue(user);
    }
}
