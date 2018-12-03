package com.semear.tec.palavrizapp.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.activities.LoginActivity;
import com.semear.tec.palavrizapp.activities.MainActivity;
import com.semear.tec.palavrizapp.activities.RegisterActivity;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.models.UserType;
import com.semear.tec.palavrizapp.repositories.MainRepository;
import com.semear.tec.palavrizapp.utils.Constants;

public class LoginViewModel extends AndroidViewModel {

    private MainRepository mainRepository;
    private FirebaseAuth mAuth;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel(){
        mainRepository = new MainRepository();
        mAuth = FirebaseAuth.getInstance();
    }



    public void handleFacebookAccessToken(Activity activity, AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fbUser = mAuth.getCurrentUser();
                        User user = new User();
                        user.setUserId(fbUser.getUid());
                        user.setEmailLogin(fbUser.getEmail());
                        user.setFullname(fbUser.getDisplayName());
                        //padrao, depois tem que trocar isso
                        user.setUserType(UserType.ESTUDANTE_PLANO1);
                        mainRepository.registerUser(user);
                        startMainActivity();
                    } else {
                        Toast.makeText(getApplication(), getApplication().getString(R.string.facebook_fail_login), Toast.LENGTH_SHORT).show();

                    }

                });
    }

    private void startMainActivity(){
        Intent it = new Intent(getApplication(), MainActivity.class);
        getApplication().startActivity(it);
    }

    /**
     * Se tiver digitado algum e-mail no login, passa ele pro Registro
     * @param email
     */
    public void startRegisterActivity(String email){
        Intent it = new Intent(getApplication(), RegisterActivity.class);
        it.putExtra(Constants.EXTRA_LOGIN, email);
        getApplication().startActivity(it);
    }

}
