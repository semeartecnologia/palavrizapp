package com.semear.tec.palavrizapp.modules.classroom

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_SUBTITLE_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO


class ClassroomActivity : BaseActivity() {

    private var codVideo : String? = String()
    private var classroomViewModel: ClassroomViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom)
        setupView()
    }

    fun setupView(){
        initViewModel()
        setupActionBar()
        setupExtras()
        setupViewObservers()
        initYoutubePlayer()
    }

    fun initViewModel(){
        classroomViewModel = ViewModelProviders.of(this).get(ClassroomViewModel::class.java)
        classroomViewModel?.initViewModel()
    }

    fun setupActionBar(){
        supportActionBar?.title = getString(R.string.actionbar_classroom_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setupExtras(){
        if (intent != null) {
            codVideo = intent?.getStringExtra(EXTRA_COD_VIDEO)
          //  course_title?.text = intent?.getStringExtra(EXTRA_TITLE_VIDEO)
          //  course_subtitle?.text = intent?.getStringExtra(EXTRA_SUBTITLE_VIDEO)
           // course_description?.text = intent?.getStringExtra(EXTRA_DESCRPTION_VIDEO)
        }
    }

    fun setupViewObservers(){
        classroomViewModel?.isUserFirstTime?.observe(this, Observer { it ->
            if (it == true){
              //  next_lesson?.text = getString(R.string.btn_concluir)
              //  next_lesson?.setOnClickListener { finish() }
            }else{
              //  next_lesson?.setOnClickListener { }
            }
        })

    }

    fun initYoutubePlayer(){
        val youTubePlayerFragment = supportFragmentManager
                .findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment
        youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
                youTubePlayer.loadVideo(codVideo)
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
            }



            override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {}
        })
    }
}
