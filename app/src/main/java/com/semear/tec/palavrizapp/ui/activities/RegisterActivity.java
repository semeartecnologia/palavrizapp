package com.semear.tec.palavrizapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.Plans;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.models.UserType;
import com.semear.tec.palavrizapp.utils.Constants;
import com.semear.tec.palavrizapp.viewmodel.RegisterViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.et_email) EditText email;
    @BindView(R.id.et_fullname) EditText fullname;
    @BindView(R.id.et_password) EditText password;
    @BindView(R.id.et_location) EditText location;
    @BindView(R.id.register_now)
    TextView btnRegister;

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();

        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        registerViewModel.initViewModel();

        if (extras != null){
            email.setText(extras.getString(Constants.EXTRA_LOGIN));
        }

        btnRegister.setOnClickListener(v -> {
            register();
        });

    }

    public void register(){

        if (fullname.getText() != null &&
                email.getText() != null &&
                password.getText() != null &&
                location.getText() != null) {

            User user = new User(
                    fullname.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString(),
                    location.getText().toString(),
                    UserType.STUDENT,
                    Plans.FREE_PLAN
            );

            registerViewModel.register(user);
        }

    }
}
