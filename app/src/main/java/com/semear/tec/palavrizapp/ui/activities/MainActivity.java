package com.semear.tec.palavrizapp.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.ui.fragments.DashboardFragment;
import com.semear.tec.palavrizapp.ui.fragments.PlansFragment;
import com.semear.tec.palavrizapp.viewmodel.DashboardViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.frameContent)
    FrameLayout frameContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        changeFragment(new DashboardFragment());

    }


    public void changeFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameContent, fragment);
        if (!(fragment instanceof DashboardFragment))
            ft.addToBackStack("tag");
        ft.commit();
    }

}
