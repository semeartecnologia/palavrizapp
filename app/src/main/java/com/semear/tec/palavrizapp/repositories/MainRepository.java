package com.semear.tec.palavrizapp.repositories;

import com.semear.tec.palavrizapp.models.User;

public class MainRepository {

    FirebaseRepository firebaseRepositoriy;
    RoomRepository roomRepository;


    public void registerUser(User user){
        firebaseRepositoriy = new FirebaseRepository();
        firebaseRepositoriy.saveUser(user);
    }
}
