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
import com.semear.tec.palavrizapp.utils.repositories.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_SUBTITLE_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO;

public class MainActivity extends BaseActivity implements DashboardFragment.OnFragmentInteractionListener {

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
        changeFragment(new DashboardFragment(), "Dashboard");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sessionManager != null && sessionManager.isUserFirstTime()) {
            startClassroomActivity();
        }
    }

    private void registerObservers() {
        mainViewModel.isUserOnline().observe(this, isOnline -> {
            if (isOnline != null && !isOnline) {
                redirectToLogin();
            }
        });
    }

    public void setActionBarTitle(String text) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(text);
    }

    private void initViewModel() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.initViewModel();
    }

    public void startClassroomActivity() {
        Intent it = new Intent(this, ClassroomActivity.class);
        it.putExtra(EXTRA_COD_VIDEO, "kHpPYqPwC9k");
        it.putExtra(EXTRA_TITLE_VIDEO, getString(R.string.first_class_title));
        it.putExtra(EXTRA_SUBTITLE_VIDEO, getString(R.string.first_class_subtitle));
        it.putExtra(EXTRA_DESCRPTION_VIDEO, getString(R.string.first_class_description));
        startActivity(it);
    }

    public void changeFragment(Fragment fragment, String fragName) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameContent, fragment, fragName);
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
        switch (item.getItemId()) {
            case R.id.action_logout:
                mainViewModel.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void OnPlansClicked() {

    }

    @Override
    public void OnThemesClicked() {

    }
}
