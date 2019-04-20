package com.semear.tec.palavrizapp.modules.dashboard;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.modules.MainActivity;
import com.semear.tec.palavrizapp.modules.plans.PlansFragment;
import com.semear.tec.palavrizapp.modules.themes.ThemesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DashboardFragment extends Fragment {

    @BindView(R.id.user_greetings)
    TextView tvHelloUser;

    @BindView(R.id.btn_see_more_plans)
    TextView btnSeeMorePlans;

    @BindView(R.id.user_plan)
    TextView tvUserPlan;

    @BindView(R.id.btn_see_more_aulas)
    TextView btnSeeMoreaulas;

    @BindView(R.id.layout_card_aulas)
    RelativeLayout cardAulas;

    @BindView(R.id.layout_card_plans)
    RelativeLayout cardPlans;

    @BindView(R.id.layout_card_essay)
    RelativeLayout cardEssay;




    private static int REQUEST_CAMERA = 333;
    private static int REQUEST_IMAGE_CAPTURE = 345;
    private DashboardViewModel dashboardViewModel;

    public DashboardFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        dashboardViewModel.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this,v);

        initView();

        return v;
    }

    public void initView(){

        User user = dashboardViewModel.getCurrentUser();
        tvHelloUser.setText(String.format(getString(R.string.salute_you),user.getFullname()));
        tvUserPlan.setText(user.getPlan().getPlanTitle(getContext()));

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setActionBarTitle("Dashboard");
            btnSeeMorePlans.setOnClickListener(v -> {
                mainActivity.changeFragment(new PlansFragment(), "Planos");
                mainActivity.setActionBarTitle("Planos");
            });
            cardPlans.setOnClickListener(v -> {
                mainActivity.changeFragment(new PlansFragment(), "Planos");
                mainActivity.setActionBarTitle("Planos");
            });
            btnSeeMoreaulas.setOnClickListener(v -> {
                mainActivity.changeFragment(new ThemesFragment(), "Aulas" );
                mainActivity.setActionBarTitle("Aulas");
            });
            cardAulas.setOnClickListener(v -> {
                mainActivity.changeFragment(new ThemesFragment(), "Aulas");
                mainActivity.setActionBarTitle("Aulas");
            });
            cardEssay.setOnClickListener(v -> {
                checkCameraPermission();
            });
        }
    }

    public void checkCameraPermission(){
        if (getActivity() == null) return;
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permissao", "pede");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);

        }else{
            Log.d("permissao", "concedida ja");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getActivity().startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == REQUEST_CAMERA){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }






}
