package com.semear.tec.palavrizapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.utils.Constants;
import com.semear.tec.palavrizapp.viewmodel.LoginViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Classe responsável por Login e Registro de Usuário
 */
public class LoginActivity extends AppCompatActivity  {

    //private UserLoginTask mAuthTask = null;

    // UI references.
    @BindView(R.id.email) EditText mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.btn_facebook_login) ImageView btnFacebook;
    @BindView(R.id.btn_google_login) ImageView btnGoogle;
    @BindView(R.id.email_sign_in_button) Button btnEmail;

    @BindView(R.id.btn_register) TextView btnRegister;

    //Facebook
    @BindView(R.id.login_facebook) LoginButton fbLogin;
    CallbackManager callbackManager;

    //Google
    @BindView(R.id.login_google)
    SignInButton gLogin;

    private LoginViewModel loginViewModel;

    public static final int G_SIGN_IN = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.initViewModel();

        this.callbackManager = loginViewModel.initFacebookLogin(LoginActivity.this, fbLogin);

        btnRegister.setOnClickListener(v ->
                loginViewModel.startRegisterActivity(mEmailView.getText().toString())
        );

        btnFacebook.setOnClickListener( v ->
                fbLogin.performClick()
        );

        btnGoogle.setOnClickListener( v ->
                gmailLogin()
        );

        btnEmail.setOnClickListener(view -> emailLogin());

    }

    public void emailLogin(){

    }

    public void gmailLogin(){
        Intent signInIntent = loginViewModel.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, G_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == G_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginViewModel.authWithGoogle(LoginActivity.this, account);
            } catch (ApiException e) {
                Toast.makeText(getApplication(), getApplication().getString(R.string.google_fail_login), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

