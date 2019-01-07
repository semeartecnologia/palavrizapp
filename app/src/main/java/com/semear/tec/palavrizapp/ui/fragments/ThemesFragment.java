package com.semear.tec.palavrizapp.ui.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.ui.adapters.ThemesAdapter;
import com.semear.tec.palavrizapp.viewmodel.DashboardViewModel;
import com.semear.tec.palavrizapp.viewmodel.ThemesViewModel;

/**
 * Fragmento de seleção de Temas
 */
public class ThemesFragment extends Fragment {


    ThemesViewModel themesViewModel;
    private RecyclerView recyclerThemes;
    private ThemesAdapter mAdapter;

    public ThemesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themesViewModel = ViewModelProviders.of(this).get(ThemesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_themes, container, false);

        mAdapter = new ThemesAdapter();
        recyclerThemes = v.findViewById(R.id.rv_themes);
        recyclerThemes.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerThemes.setAdapter(mAdapter);

        getListOfThemes();

        return v;
    }

    public void getListOfThemes(){
        themesViewModel.getAllThemesWithGroups().observe(this, theme ->{
            if (theme != null){
                mAdapter.addThemes(theme);
            }
        });
    }

}
