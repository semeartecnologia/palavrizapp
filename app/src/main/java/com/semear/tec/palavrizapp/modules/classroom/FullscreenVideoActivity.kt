package com.semear.tec.palavrizapp.modules.classroom

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.classroom.video_view.VideoFragment

class FullscreenVideoActivity: AppCompatActivity() {

    private var videoUrl = "https://firebasestorage.googleapis.com/v0/b/palavrizapp.appspot.com/o/video_2.mp4?alt=media&token=8f19e683-70df-45dd-9e99-5c3d9a74a1d1"
    private var positionVideo: Long = 0
    private var currentWindow = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_full_screen)

        getExtras()
        setupView()
    }

    private fun setupView(){
        setupVideoFragment()
    }

    private fun getExtras(){
        videoUrl = intent?.getStringExtra("urlVideo") ?: ""
        positionVideo = intent?.getLongExtra("position",0) ?: 0
        currentWindow = intent?.getIntExtra("window", 0) ?: 0
    }

    private fun setupVideoFragment() {
        val argumentFragment = VideoFragment()//Get Fragment Instance
        val data = Bundle()//Use bundle to pass data
        data.putString("videoUrl", videoUrl)
        data.putLong("position", positionVideo)
        data.putInt("window", currentWindow)
        argumentFragment.arguments = data//Finally set argument bundle to fragment
        supportFragmentManager.beginTransaction().replace(R.id.frame_video, argumentFragment).commit()
    }

}