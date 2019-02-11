package com.semear.tec.palavrizapp.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity;
import com.semear.tec.palavrizapp.utils.repositories.SessionManager;
import com.semear.tec.palavrizapp.modules.dashboard.DashboardFragment;
import com.semear.tec.palavrizapp.utils.constants.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.frameContent)
    FrameLayout frameContent;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sessionManager != null && sessionManager.isUserFirstTime()){
            Intent it = new Intent(this, ClassroomActivity.class);
            it.putExtra(Constants.EXTRA_COD_VIDEO, "kHpPYqPwC9k");
            startActivity(it);

        }else{
            changeFragment(new DashboardFragment());
        }
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
