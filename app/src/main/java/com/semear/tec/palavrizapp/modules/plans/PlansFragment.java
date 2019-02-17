package com.semear.tec.palavrizapp.modules.plans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.modules.dashboard.DashboardFragment;
import com.semear.tec.palavrizapp.utils.repositories.SessionManager;
import com.semear.tec.palavrizapp.modules.MainActivity;
import com.semear.tec.palavrizapp.utils.adapters.PlansAdapter;


public class PlansFragment extends Fragment {

    RecyclerView recyclerPlans;
    SessionManager sessionManager;
    TextView btnUpdatePlan;

    public PlansFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plans, container, false);

        if (getContext() != null)
            sessionManager = new SessionManager(getContext());

        PlansAdapter mAdapter = new PlansAdapter(sessionManager.getUserPlan(), PlansFragment.this);
        btnUpdatePlan = v.findViewById(R.id.btn_update_plan);
        recyclerPlans = v.findViewById(R.id.rvPlans);
        recyclerPlans.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPlans.setAdapter(mAdapter);
        mAdapter.startList();

        btnUpdatePlan.setOnClickListener(view -> {
            sessionManager.setUserPlan(mAdapter.getLastCheckedPos());
            if(getActivity() != null)
                ((MainActivity) getActivity()).changeFragment(new DashboardFragment(), "Dashboard");
        });

        return v;
    }

    public void setEnableUpdateButton(Boolean isEnabled){
        btnUpdatePlan.setEnabled(isEnabled);
    }




}
