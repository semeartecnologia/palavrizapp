package com.semear.tec.palavrizapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.viewmodel.LoginViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

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

    @BindView(R.id.tv_version_name) TextView tvVersionName;

    //Google
    @BindView(R.id.login_google)
    SignInButton gLogin;

    private LoginViewModel loginViewModel;

    public static final int G_SIGN_IN = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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

        tvVersionName.setText(loginViewModel.getVersionName());

    }

    public void emailLogin(){

    }

    public void gmailLogin(){
        Intent signInIntent = loginViewModel.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, G_SIGN_IN);
    }

    public void alertGooglePlayError(){
        String titleAlert = getString(R.string.alert_gplay_title);
        String textAlert = getString(R.string.alert_gplay_text);
        String btnAlert = getString(R.string.alert_gplay_btn_accept);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(LoginActivity.this);
        }

        builder.setTitle(titleAlert)
                .setMessage(textAlert)
                .setPositiveButton(btnAlert, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
                alertGooglePlayError();
                e.printStackTrace();
            }
        }
    }
}

