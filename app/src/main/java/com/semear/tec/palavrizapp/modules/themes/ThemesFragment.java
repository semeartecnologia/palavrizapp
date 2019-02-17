package com.semear.tec.palavrizapp.modules.themes;


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
import com.semear.tec.palavrizapp.models.VideoPreview;
import com.semear.tec.palavrizapp.utils.InitData;
import com.semear.tec.palavrizapp.utils.adapters.ThemesAdapter;

/**
 * Fragmento de seleção de Temas
 */
public class ThemesFragment extends Fragment {


    ThemesViewModel themesViewModel;
    private RecyclerView recyclerTheme1;
    private RecyclerView recyclerTheme2;
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
        recyclerTheme1 = v.findViewById(R.id.rv_themes);
        recyclerTheme1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerTheme1.setAdapter(mAdapter);

        recyclerTheme2 = v.findViewById(R.id.rv_redacao);
        recyclerTheme2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerTheme2.setAdapter(mAdapter);

        //getListOfThemes();
        getListOfMovies();

        return v;
    }

    public void getListOfThemes(){
        themesViewModel.getAllThemesWithGroups().observe(this, theme ->{
            if (theme != null){
                mAdapter.addThemes(theme);
            }
        });
    }

    public void getListOfMovies(){
        for(VideoPreview vp : InitData.getListVideoPreview()){
            mAdapter.addVideo(vp);
        }
    }

}
