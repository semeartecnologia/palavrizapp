package com.semear.tec.palavrizapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.Plans;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.models.UserType;
import com.semear.tec.palavrizapp.utils.Commons;
import com.semear.tec.palavrizapp.utils.Constants;
import com.semear.tec.palavrizapp.viewmodel.LoginRegisterViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.et_email) EditText email;
    @BindView(R.id.et_fullname) EditText fullname;
    @BindView(R.id.et_password) EditText password;
    @BindView(R.id.register_now)
    TextView btnRegister;

    private LoginRegisterViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();

        loginViewModel = ViewModelProviders.of(this).get(LoginRegisterViewModel.class);
        loginViewModel.initViewModel();

        if (extras != null){
            email.setText(extras.getString(Constants.EXTRA_LOGIN));
        }

        btnRegister.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (loginViewModel.checkFields(fullname.getText().toString(),
                    emailText,
                    passwordText)){

                loginViewModel.registerWithEmail(RegisterActivity.this, emailText,passwordText);
            }else{
                //Commons.showAlert();
            }
        });

    }


}
