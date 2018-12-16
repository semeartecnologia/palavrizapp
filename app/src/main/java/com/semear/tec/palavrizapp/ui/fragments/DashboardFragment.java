package com.semear.tec.palavrizapp.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.models.User;
import com.semear.tec.palavrizapp.ui.activities.MainActivity;
import com.semear.tec.palavrizapp.viewmodel.DashboardViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DashboardFragment extends Fragment {

    @BindView(R.id.user_greetings)
    TextView tvHelloUser;

    @BindView(R.id.btn_see_more_plans)
    TextView btnSeeMorePlans;

    @BindView(R.id.user_plan)
    TextView tvUserPlan;


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
        if (mainActivity != null)
            btnSeeMorePlans.setOnClickListener(v -> mainActivity.changeFragment(new PlansFragment()));
    }






}
