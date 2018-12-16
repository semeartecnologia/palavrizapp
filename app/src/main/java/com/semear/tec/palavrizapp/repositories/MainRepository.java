package com.semear.tec.palavrizapp.repositories;

import com.semear.tec.palavrizapp.models.User;

public class MainRepository {

    FirebaseRepository firebaseRepositoriy;
    RoomRepository roomRepository;


    /**
     * Registra usuario no firebase
     * @param user
     */
    public void registerUser(User user){
        firebaseRepositoriy = new FirebaseRepository();
        firebaseRepositoriy.saveUser(user);
    }
}
