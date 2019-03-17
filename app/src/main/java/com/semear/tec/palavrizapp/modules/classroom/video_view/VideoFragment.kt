package com.semear.tec.palavrizapp.modules.classroom.video_view

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.classroom.FullscreenVideoActivity
import kotlinx.android.synthetic.main.video_view_fragment.*

class VideoFragment: Fragment() {

    private lateinit var player: SimpleExoPlayer
    private var videoUrl = ""
    private var position: Long = 0
    private var window = 0
    private var playWhenReady: Boolean = true
    private lateinit var videoViewModel: VideoViewModel



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.video_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        getExtras()
        setupView()
    }

    private fun initViewModel(){
        videoViewModel = ViewModelProviders.of(this).get<VideoViewModel>(VideoViewModel::class.java)
    }

    private fun setupView(){
        configFullscreenListener()
    }

    private fun configFullscreenListener(){
        val controlView = player_view.findViewById<View>(R.id.exo_controller)
        val fullscreenButton = controlView.findViewById<View>(R.id.exo_fullscreen_button)
        fullscreenButton.setOnClickListener {
            val it = Intent(activity, FullscreenVideoActivity::class.java)
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            it.putExtra("urlVideo", videoUrl)
            it.putExtra("position", player.currentPosition)
            it.putExtra("window", player.currentWindowIndex)

            activity?.startActivity(it)
        }
    }

    private fun getExtras(){
        videoUrl = getVideoUrl() ?: ""
        position = getPosition() ?: 0
        window = getWindow() ?: 0
    }

    private fun getVideoUrl(): String? {
        return arguments?.getString("videoUrl")
    }
    private fun getPosition(): Long? {
        return arguments?.getLong("position")
    }
    private fun getWindow(): Int? {
        return arguments?.getInt("window")
    }

    companion object {
        fun newInstance(videoUrl: String): VideoFragment {
            val fragment = VideoFragment()
            val args = Bundle()
            args.putString("videoUrl", videoUrl)
            fragment.arguments = args
            return fragment
        }
    }

    private fun initPlayer(){
        player = ExoPlayerFactory.newSimpleInstance(
                context,
                DefaultRenderersFactory(context),
                DefaultTrackSelector(), DefaultLoadControl()
        )

        player_view.player = player
        player.playWhenReady = true
        prepareMediaSource()
        player.seekTo(window, position)

    }

    private fun releasePlayer() {
        playWhenReady = false
        player.release()

    }

    private fun prepareMediaSource(){
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri)
        player.prepare(mediaSource, true, false)
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(
                DefaultHttpDataSourceFactory("player-agent")).createMediaSource(uri)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        player_view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }


    override fun onResume() {
        super.onResume()
        initPlayer()
    }


    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

}