package com.semear.tec.palavrizapp.modules;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.modules.base.BaseActivity;
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity;
import com.semear.tec.palavrizapp.modules.dashboard.DashboardFragment;
import com.semear.tec.palavrizapp.modules.essay.image_check.EssayCheckActivity;
import com.semear.tec.palavrizapp.modules.upload.UploadActivity;
import com.semear.tec.palavrizapp.utils.Commons;
import com.semear.tec.palavrizapp.utils.constants.Constants;
import com.semear.tec.palavrizapp.utils.repositories.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_IMAGE_CHECK;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_SUBTITLE_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO;
import static com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_PATH;

public class MainActivity extends BaseActivity {

    @BindView(R.id.frameContent)
    FrameLayout frameContent;

    SessionManager sessionManager;
    private MainViewModel mainViewModel;

    private static int SELECT_VIDEO = 300;
    private static int REQUEST_READ_STORAGE = 400;
    private static int REQUEST_WRITE_STORAGE = 401;
    private static int REQUEST_IMAGE_CAPTURE = 345;
    private static int REQUEST_IMAGE_CHECK = 405;

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
        if (sessionManager != null && sessionManager.isUserFirstTime()){
            startClassroomActivity();
        }
    }

    private void registerObservers(){
        mainViewModel.isUserOnline().observe(this, isOnline -> {
            if (!isOnline){
                redirectToLogin();
            }
        });
    }

    public void setActionBarTitle(String text){
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(text);
    }

    private void initViewModel(){
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.initViewModel();
    }

    public void startClassroomActivity(){
        Intent it = new Intent(this, ClassroomActivity.class);
        it.putExtra(EXTRA_COD_VIDEO, "kHpPYqPwC9k");
        it.putExtra(EXTRA_TITLE_VIDEO, getString(R.string.first_class_title));
        it.putExtra(EXTRA_SUBTITLE_VIDEO, getString(R.string.first_class_subtitle));
        it.putExtra(EXTRA_DESCRPTION_VIDEO, getString(R.string.first_class_description));
        startActivity(it);
    }

    public void changeFragment(Fragment fragment, String fragName){
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
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                mainViewModel.logout();
                return true;
            case R.id.upload_video:
                openPickVideoGllery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openPickVideoGllery(){
        requestStoragePermission();
    }

    public void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);

        }else{
            requestWriteStoragePermission();
        }
    }

    public void requestWriteStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);

        }else{
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_VIDEO);
        }
    }



    private void startUploadActivity(String videoPath) {
        Intent it = new Intent(this, UploadActivity.class);
        it.putExtra(EXTRA_VIDEO_PATH, videoPath);
        startActivity(it);
    }

    private void startImageCheckActivity(Bitmap bmp){
        Intent it = new Intent(this, EssayCheckActivity.class);
        it.putExtra(EXTRA_IMAGE_CHECK, bmp);
        startActivityForResult(it, REQUEST_IMAGE_CHECK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if ( requestCode == REQUEST_READ_STORAGE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                requestWriteStoragePermission();

            }
        }else if ( requestCode == REQUEST_WRITE_STORAGE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_VIDEO);

            }
        }
    }

    @ Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO) {
                String selectedVideoPath = Commons.getRealPathFromURI(this, data.getData());
                try {
                    if(selectedVideoPath == null) {
                        finish();
                    } else {
                        startUploadActivity(selectedVideoPath);
                        //mainViewModel.uploadVideo(this, selectedVideoPath, "nome.mp4");
                    }
                } catch (Exception e) {

                    Log.d("videao", "error");
                    e.printStackTrace();
                }
            }else  if (requestCode == REQUEST_IMAGE_CAPTURE){
                if ( data.getExtras() != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    startImageCheckActivity(photo);
                }
            }
        }else if (resultCode == Constants.RESULT_NEGATIVE){
            if (requestCode == REQUEST_IMAGE_CHECK){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

}
