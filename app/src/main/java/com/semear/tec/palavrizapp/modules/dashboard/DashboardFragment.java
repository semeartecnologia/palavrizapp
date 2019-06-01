package com.semear.tec.palavrizapp.modules.dashboard;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.models.UserType;
import com.semear.tec.palavrizapp.modules.MainActivity;
import com.semear.tec.palavrizapp.modules.essay.MyEssayActivity;
import com.semear.tec.palavrizapp.modules.essay.essay_mark_list.EssayMarkListFragment;
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

    @BindView(R.id.card_layout_essay_list)
    CardView cardEssayListLayout;

    @BindView(R.id.layout_card_essay_list)
    RelativeLayout cardEssayList;



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
        checkUserTypeView();

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
                //checkCameraPermission();
                startMyEssayActivity();
            });
            cardEssayList.setOnClickListener(v -> {
                mainActivity.changeFragment(new EssayMarkListFragment(), "Sala de Correção");
                mainActivity.setActionBarTitle("Sala de Correção");
            });
        }
    }

    private void startMyEssayActivity(){
        Intent it = new Intent(getActivity(), MyEssayActivity.class);
        startActivity(it);
    }

    private void checkUserTypeView(){
        User user = dashboardViewModel.getCurrentUser();
        if (user.getUserType() == UserType.CORRETOR || user.getUserType() == UserType.ADMINISTRADOR){
            cardEssayListLayout.setVisibility(View.VISIBLE);
        }else{
            cardEssayListLayout.setVisibility(View.GONE);
        }
    }









}
