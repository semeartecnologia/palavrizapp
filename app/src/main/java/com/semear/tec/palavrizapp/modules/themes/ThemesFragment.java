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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.utils.adapters.ThemesAdapter;
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository;

import java.util.ArrayList;

/**
 * Fragmento de seleção de Temas
 */
public class ThemesFragment extends Fragment {


    ThemesViewModel themesViewModel;
    private RecyclerView recyclerTheme1;
    private ThemesAdapter mAdapter;
    private VideoRepository videoRepository;
    private Spinner categorySpinner;
    private ProgressBar progressBar;

    public ThemesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themesViewModel = ViewModelProviders.of(this).get(ThemesViewModel.class);
        if (getActivity() != null) {
            videoRepository = new VideoRepository(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_themes, container, false);

        setupView(v);
        getCategoryList();
        return v;
    }

    private void setupView(View v){
        mAdapter = new ThemesAdapter();
        recyclerTheme1 = v.findViewById(R.id.rv_themes);
        recyclerTheme1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerTheme1.setAdapter(mAdapter);

        categorySpinner = v.findViewById(R.id.category_spinner);
        progressBar = v.findViewById(R.id.progress_loading_videos);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) categorySpinner.getItemAtPosition(position);
                mAdapter.clearVideoList();
                progressBar.setVisibility(View.VISIBLE);
                recyclerTheme1.setVisibility(View.GONE);
                getVideoList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void getCategoryList(){
        if (getActivity() == null) return;
        ArrayList<String> categoryArray = new ArrayList<>();

        videoRepository.getCategoryList(categories -> {
                    String categorySearch = "";
                    for ( int i = 0; i < categories.size(); i++){
                        if ( i == 0 ){
                            categorySearch = categories.get(i).getCategory();
                        }
                        categoryArray.add(categories.get(i).getCategory());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_dropdown_item,
                            categoryArray
                    );

            categorySpinner.setAdapter(adapter);
            getVideoList();
            return null;
        });
    }

    public void getVideoList(){
        if (getActivity() == null) return;
        videoRepository.getVideoList(videoList ->{
            mAdapter.addAllVideo(videoList);
            progressBar.setVisibility(View.GONE);
            recyclerTheme1.setVisibility(View.VISIBLE);
            return null;
        });
    }

}
