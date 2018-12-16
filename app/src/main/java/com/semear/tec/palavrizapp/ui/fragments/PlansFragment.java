package com.semear.tec.palavrizapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.ui.adapters.PlansAdapter;


public class PlansFragment extends Fragment {

    RecyclerView recyclerPlans;

    public PlansFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plans, container, false);

        PlansAdapter mAdapter = new PlansAdapter();
        recyclerPlans = v.findViewById(R.id.rvPlans);
        recyclerPlans.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPlans.setAdapter(mAdapter);
        mAdapter.startList();

        return v;
    }




}
