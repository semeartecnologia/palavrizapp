package com.semear.tec.palavrizapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.ui.fragments.PlansFragment;
import com.semear.tec.palavrizapp.viewmodel.MainViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;

    @BindView(R.id.user_greetings)
    TextView tvHelloUser;

    @BindView(R.id.btn_see_more_plans)
    TextView btnSeeMorePlans;

    @BindView(R.id.frameContent)
    FrameLayout frameContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.init();

        initView();
    }

    public void initView(){
        User user = mainViewModel.getCurrentUser();
        tvHelloUser.setText(String.format(getString(R.string.salute_you),user.getFullname()));

        btnSeeMorePlans.setOnClickListener(v -> changeFragment(new PlansFragment()));
    }

    public void changeFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentView, fragment);
        ft.commit();
        frameContent.setVisibility(View.VISIBLE);
    }

}
