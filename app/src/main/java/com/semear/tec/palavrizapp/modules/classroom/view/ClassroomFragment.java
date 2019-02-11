package com.semear.tec.palavrizapp.modules.classroom.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.utils.constants.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassroomFragment extends Fragment {


    YouTubePlayerView mYoutubePlayerView;
    Button nextLesson;

    YouTubePlayer.OnInitializedListener mOnInitializedListener;

    public ClassroomFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_classroom, container, false);

        mYoutubePlayerView = v.findViewById(R.id.youtube_fragment);
        nextLesson = v.findViewById(R.id.next_lesson);


        nextLesson.setOnClickListener(view ->
            mYoutubePlayerView.initialize(Constants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    Log.d("youtubio", "sucesso");
                    youTubePlayer.loadVideo("vcQ4oGqm7yo");
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Log.d("youtubio", "erro dos brow");
                }
            }));

        return v;
    }


}
