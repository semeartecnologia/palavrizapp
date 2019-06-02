package com.semear.tec.palavrizapp.modules.register;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.modules.base.BaseActivity;
import com.semear.tec.palavrizapp.utils.constants.Constants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.textInputName) TextInputLayout textInputName;
    @BindView(R.id.textInputEmail) TextInputLayout textInputEmail;
    @BindView(R.id.textInputPwd) TextInputLayout textInputPwd;
    @BindView(R.id.textInputPwdConfirm) TextInputLayout textInputConfirmPwd;
    @BindView(R.id.email) TextInputEditText email;
    @BindView(R.id.fullname) TextInputEditText fullname;
    @BindView(R.id.password) TextInputEditText password;
    @BindView(R.id.confirm_password) TextInputEditText confirmPassword;
    @BindView(R.id.radioGroupGender) RadioGroup radioGender;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.progress_register) ProgressBar progressBar;
    @BindView(R.id.btn_register) Button btnRegister;
    @BindView(R.id.btn_cancel) Button btnCancel;

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email.setText(extras.getString(Constants.EXTRA_LOGIN));
        }

        setupActionBar();
        initViewModel();
        setupButtonEvents();
        registerObservers();


    }

    public void setupActionBar(){
        if ( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.actionbar_register_title));
        }
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

        registerViewModel.isLoading().observe(this,
                this::toggleLoading);
    }

    private void toggleLoading(boolean isLoading){
        if (isLoading){
//            email.setVisibility(View.GONE);
//            textInputEmail.setVisibility(View.GONE);
//            password.setVisibility(View.GONE);
//            textInputPwd.setVisibility(View.GONE);
//            fullname.setVisibility(View.GONE);
//            textInputName.setVisibility(View.GONE);
//            confirmPassword.setVisibility(View.GONE);
//            textInputConfirmPwd.setVisibility(View.GONE);
//            radioGender.setVisibility(View.GONE);
            btnRegister.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }else {
//            email.setVisibility(View.VISIBLE);
//            textInputEmail.setVisibility(View.VISIBLE);
//            password.setVisibility(View.VISIBLE);
//            textInputPwd.setVisibility(View.VISIBLE);
//            fullname.setVisibility(View.VISIBLE);
//            textInputName.setVisibility(View.VISIBLE);
//            confirmPassword.setVisibility(View.VISIBLE);
//            textInputConfirmPwd.setVisibility(View.VISIBLE);
//            radioGender.setVisibility(View.VISIBLE);
            btnRegister.setText(getString(R.string.register_now_btn));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void changeAvatarMale(){
        Picasso.get().load(R.drawable.avatar_man_512).into(profileImage);
    }

    private void changeAvatarFemale(){
        Picasso.get().load(R.drawable.avatar_woman_512).into(profileImage);
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

        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }



}
