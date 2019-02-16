package com.semear.tec.palavrizapp.modules.register;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.utils.constants.Constants;
import com.semear.tec.palavrizapp.modules.login.LoginViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.email) TextInputEditText email;
    @BindView(R.id.fullname) TextInputEditText fullname;
    @BindView(R.id.password) TextInputEditText password;
    @BindView(R.id.confirm_password) TextInputEditText confirmPassword;
    @BindView(R.id.radioGroupGender) RadioGroup radioGender;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.register_now)
    TextView btnRegister;

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        ButterKnife.bind(this);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email.setText(extras.getString(Constants.EXTRA_LOGIN));
        }

        initViewModel();
        setupButtonEvents();
        registerObservers();


    }

    private void initViewModel(){
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        registerViewModel.initViewModel();
    }

    private void registerObservers(){
        registerViewModel.getShowMessageErrorRegister().observe(this,
                aBoolean -> showToast(getString(R.string.register_fail), aBoolean));

        registerViewModel.getShowMessageMissingFields().observe(this,
                aBoolean -> showToast(getString(R.string.fill_all_fields), aBoolean));

        registerViewModel.getShowMessagePwdNotMatch().observe(this,
                aBoolean -> showToast(getString(R.string.pwd_not_match), aBoolean));
    }

    private void changeAvatarMale(){
        Picasso.get().load(R.drawable.avatar_man_512).into(profileImage);
    }

    private void changeAvatarFemale(){
        Picasso.get().load(R.drawable.avatar_woman_512).into(profileImage);
    }

    private void showToast(String text, Boolean show){
        if (show)
            Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).show();
    }

    private void setupButtonEvents(){

        radioGender.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId){
                    case R.id.radio_male:
                        changeAvatarMale();
                        break;
                    case R.id.radio_female:
                        changeAvatarFemale();
                        break;
                    default:
                        break;
                }
        });

        btnRegister.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            String name = fullname.getText().toString();
            String confPassword = confirmPassword.getText().toString();
            registerViewModel.registerWithEmail(RegisterActivity.this, emailText, passwordText, confPassword, name, radioGender.getCheckedRadioButtonId());
        });
    }



}
