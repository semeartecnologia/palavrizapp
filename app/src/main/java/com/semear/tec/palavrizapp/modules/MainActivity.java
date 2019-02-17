package com.semear.tec.palavrizapp.modules;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.modules.base.BaseActivity;
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity;
import com.semear.tec.palavrizapp.modules.dashboard.DashboardFragment;
import com.semear.tec.palavrizapp.utils.constants.Constants;
import com.semear.tec.palavrizapp.utils.repositories.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.frameContent)
    FrameLayout frameContent;

    SessionManager sessionManager;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
       // getSupportActionBar().show();

        initViewModel();
        registerObservers();
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

    private void registerObservers(){
        mainViewModel.isUserOnline().observe(this, isOnline -> {
            if (!isOnline){
                redirectToLogin();
            }
        });
    }

    private void initViewModel(){
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.initViewModel();
    }

    public void changeFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameContent, fragment);
        if (!(fragment instanceof DashboardFragment))
            ft.addToBackStack("tag");
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                mainViewModel.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
