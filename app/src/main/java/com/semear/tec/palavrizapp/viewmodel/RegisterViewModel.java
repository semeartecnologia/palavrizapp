package com.semear.tec.palavrizapp.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.repositories.MainRepository;

public class RegisterViewModel extends ViewModel {

    private MainRepository mainRepository;

    public void initViewModel(){
        mainRepository = new MainRepository();
    }

    public boolean register(User user){

        if (user.getFullname().isEmpty() ||
                user.getEmailLogin().isEmpty() ||
                user.getPassword().isEmpty() ||
                user.getLocation().isEmpty()){
            return false;
        }


        mainRepository.registerUser(user);
        return true;
    }
}
