package com.semear.tec.palavrizapp.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.semear.tec.palavrizapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemesFragment extends Fragment {


    public ThemesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_themes, container, false);
    }

}
