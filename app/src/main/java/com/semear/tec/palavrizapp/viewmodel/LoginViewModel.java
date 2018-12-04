package com.semear.tec.palavrizapp.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.ui.activities.LoginActivity;
import com.semear.tec.palavrizapp.ui.activities.MainActivity;
import com.semear.tec.palavrizapp.ui.activities.RegisterActivity;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.models.UserType;
import com.semear.tec.palavrizapp.repositories.MainRepository;
import com.semear.tec.palavrizapp.utils.Constants;

public class LoginViewModel extends AndroidViewModel {

    private MainRepository mainRepository;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel(){
        mainRepository = new MainRepository();
        mAuth = FirebaseAuth.getInstance();
        initGmailLogin();
    }


    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void initGmailLogin(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getApplication().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getApplication(), gso);
    }

    public void authWithGoogle(Activity activity, GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        getUserDataAndLogin();
                    } else {
                        Toast.makeText(getApplication(), getApplication().getString(R.string.google_fail_login), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void authWithFacebook(Activity activity, AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        getUserDataAndLogin();
                    } else {
                        Toast.makeText(getApplication(), getApplication().getString(R.string.facebook_fail_login), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void getUserDataAndLogin(){
        FirebaseUser gUser = mAuth.getCurrentUser();
        User user = new User();
        user.setUserId(gUser.getUid());
        user.setEmailLogin(gUser.getEmail());
        user.setFullname(gUser.getDisplayName());
        //padrao, depois tem que trocar isso
        user.setUserType(UserType.ESTUDANTE_PLANO1);
        mainRepository.registerUser(user);
        startMainActivity();
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
