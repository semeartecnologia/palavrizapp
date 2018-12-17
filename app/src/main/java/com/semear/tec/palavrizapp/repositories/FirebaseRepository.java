package com.semear.tec.palavrizapp.repositories;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.semear.tec.palavrizapp.models.User;

public class FirebaseRepository {


    private FirebaseDatabase fDatabase;
    private DatabaseReference myRef;

    public FirebaseRepository(){
        fDatabase = FirebaseDatabase.getInstance();
    }

    public void  saveUser(User user){
        //novo usuario cadastrando email na mao
        if (user.getUserId() == null || user.getUserId().isEmpty()){
            user.setUserId(fDatabase.getReference("users/").push().getKey());
        }
        myRef = fDatabase.getReference("users/" + user.getUserId());
        myRef.setValue(user);
    }

}
