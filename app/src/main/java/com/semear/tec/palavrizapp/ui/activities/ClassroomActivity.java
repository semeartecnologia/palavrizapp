package com.semear.tec.palavrizapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.semear.tec.palavrizapp.R;
import com.semear.tec.palavrizapp.repositories.SessionManager;
import com.semear.tec.palavrizapp.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.semear.tec.palavrizapp.utils.Constants.EXTRA_COD_VIDEO;
import static com.semear.tec.palavrizapp.utils.Constants.EXTRA_DESCRPTION_VIDEO;
import static com.semear.tec.palavrizapp.utils.Constants.EXTRA_SUBTITLE_VIDEO;
import static com.semear.tec.palavrizapp.utils.Constants.EXTRA_TITLE_VIDEO;

public class ClassroomActivity extends YouTubeBaseActivity {


    @BindView(R.id.youtube_fragment)
    YouTubePlayerView mYoutubePlayerView;

    @BindView(R.id.next_lesson)
    Button nextLesson;

    private SessionManager sessionManager;

    private String codVideo;
    private String title;
    private String subtitle;
    private String description;

    @BindView(R.id.course_title)
    TextView tvTitle;
    @BindView(R.id.course_subtitle)
    TextView tvSubtitle;
    @BindView(R.id.course_description)
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        ButterKnife.bind(this);

        if (getIntent() != null){
            codVideo = getIntent().getStringExtra(EXTRA_COD_VIDEO);
            title = getIntent().getStringExtra(EXTRA_TITLE_VIDEO);
            subtitle = getIntent().getStringExtra(EXTRA_SUBTITLE_VIDEO);
            description = getIntent().getStringExtra(EXTRA_DESCRPTION_VIDEO);

            if (title != null && subtitle != null && description != null) {
                tvTitle.setText(title);
                tvSubtitle.setText(subtitle);
                tvDescription.setText(description);
            }
        }

        sessionManager = new SessionManager(this);




        if (sessionManager.isUserFirstTime()){
                nextLesson.setText(getString(R.string.btn_concluir));
                sessionManager.setUserFirstTime(false);
                nextLesson.setOnClickListener(view->finish());
        }else{
            nextLesson.setOnClickListener(view->{});
        }

        mYoutubePlayerView.initialize(Constants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("youtubio", "sucesso");
                youTubePlayer.loadVideo(codVideo);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("youtubio", "erro dos brow");
            }
        });

    }
}
