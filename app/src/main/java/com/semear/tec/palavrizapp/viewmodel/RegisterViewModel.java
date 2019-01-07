package com.semear.tec.palavrizapp.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.repositories.UserRepository;

public class RegisterViewModel extends ViewModel {

    private UserRepository userRepository;

    public void initViewModel(){
        userRepository = new UserRepository();
    }

    public boolean register(User user){

        if (user.getFullname().isEmpty() ||
                user.getEmail().isEmpty() ||
                user.getPassword().isEmpty() ||
                user.getLocation().isEmpty()){
            return false;
        }


        userRepository.registerUser(user);
        return true;
    }
}
