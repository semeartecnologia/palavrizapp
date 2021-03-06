package com.palavrizar.tec.palavrizapp.modules.classroom.video_view

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.classroom.FullscreenVideoActivity
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import kotlinx.android.synthetic.main.video_view_fragment.*
import org.json.JSONObject
import java.math.BigDecimal


class VideoFragment: Fragment() {

    private lateinit var player: SimpleExoPlayer
    private var videoUrl = ""
    private var videoKey = ""
    private var videoProgress = 0
    private var isStorageVideo = false
    private var position: Long = 0
    private var window = 0
    private var playWhenReady: Boolean = true
    private lateinit var videoViewModel: VideoViewModel
    private var sessionManager: SessionManager? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.video_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initSessionManager()
        getExtras()
        setupView()
        initPlayer()
    }

    private fun initSessionManager() {
        sessionManager = SessionManager(activity)
    }

    private fun initViewModel(){
        videoViewModel = ViewModelProviders.of(this).get<VideoViewModel>(VideoViewModel::class.java)
    }

    private fun setupView(){
        configFullscreenListener()
    }

    private fun configListenerProgress() {
        player.addListener(object: Player.EventListener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            }
        })
    }

    private fun configFullscreenListener(){
        val controlView = player_view.findViewById<View>(R.id.exo_controller)
        val fullscreenButton = controlView.findViewById<View>(R.id.exo_fullscreen_button)
        fullscreenButton.setOnClickListener {
            val it = Intent(activity, FullscreenVideoActivity::class.java)
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            it.putExtra("urlVideo", videoUrl)
            it.putExtra("videoKey", videoKey)
            it.putExtra("position", player.currentPosition)
            it.putExtra("window", player.currentWindowIndex)
            it.putExtra("isStorageVideo", isStorageVideo)

            activity?.startActivity(it)
        }
    }

    private fun getExtras(){
        videoUrl = getVideoUrl() ?: ""
        position = getPosition() ?: 0
        window = getWindow() ?: 0
        videoKey = getVideoKey() ?: ""
        videoProgress = getVideoProgress() ?: 0
        isStorageVideo = getIsStorageVideo() ?: false
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

    private fun getVideoKey(): String? {
        return arguments?.getString("videoKey")
    }

    private fun getVideoProgress(): Int? {
        return arguments?.getInt("videoProgress")
    }

    private fun getIsStorageVideo(): Boolean? {
        return arguments?.getBoolean("isStorageVideo")
    }



    companion object {
        fun newInstance(videoUrl: String, videoKey: String? = "", progress: Int = 0): VideoFragment {
            val fragment = VideoFragment()
            val args = Bundle()
            args.putString("videoUrl", videoUrl)
            args.putString("videoKey", videoKey)
            args.putLong("position", progress.toLong())
            fragment.arguments = args
            return fragment
        }
    }

    private fun initPlayer(){
        if (!isStorageVideo) {
            player = ExoPlayerFactory.newSimpleInstance(
                    context,
                    DefaultRenderersFactory(context),
                    DefaultTrackSelector(), DefaultLoadControl()
            )

            player_view?.visibility = View.VISIBLE
            video_view?.visibility = View.GONE
            player_view?.player = player
            prepareMediaSource()
            player.playWhenReady = true
            player.seekTo(window, position)
            configListenerProgress()
        }else{
            player_view?.visibility = View.GONE
            video_view?.visibility = View.VISIBLE

            var mediaController = MediaController(activity as Activity)
            mediaController.setAnchorView(video_view)
            video_view.setMediaController(mediaController)
            video_view?.setVideoURI(Uri.parse(videoUrl))
            video_view?.start()
        }

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


    override fun onPause() {
        super.onPause()
        if (!isStorageVideo) {
            refreshJsonProgress()
            refreshJsonPosition()
            if (Util.SDK_INT <= 23) {
                releasePlayer()
            }
        }
    }

    private fun refreshJsonPosition(){
        val currentPosition = player.currentPosition

        val currentJson = sessionManager?.videosPosition

        if (currentJson != null && videoKey.isNotBlank()){
            try{
                val getValue = currentJson.get(videoKey)

                if (getValue != null){
                    val position = BigDecimal.valueOf(currentJson.getDouble(videoKey)).toFloat();
                    if (position < currentPosition){
                        currentJson.put(videoKey, currentPosition)
                        sessionManager?.saveVideosPosition(currentJson)
                    }
                }else {
                    currentJson.put(videoKey, currentPosition)
                    sessionManager?.saveVideosPosition(currentJson)
                }
            }catch (e: Exception){
                currentJson.put(videoKey, currentPosition)
                sessionManager?.saveVideosPosition(currentJson)
            }
        }else{
            val json = JSONObject()
            json.put(videoKey,currentPosition)
            sessionManager?.saveVideosPosition(json)
        }
    }

    private fun refreshJsonProgress(){
        val currentPosition = player.currentPosition
        val duration = player.duration
        val percentWatched = (currentPosition.toFloat()/duration.toFloat())*100

        val currentJson = sessionManager?.videosProgress

        if (currentJson != null && videoKey.isNotBlank()){
            try{
                val getValue = currentJson.get(videoKey)

                if (getValue != null){
                    val currentProgress = BigDecimal.valueOf(currentJson.getDouble(videoKey)).toFloat();
                    if (currentProgress < percentWatched){
                        currentJson.put(videoKey, percentWatched)
                        sessionManager?.saveVideosProgess(currentJson)
                    }
                }else {
                    currentJson.put(videoKey, percentWatched)
                    sessionManager?.saveVideosProgess(currentJson)
                }
            }catch (e: Exception){
                currentJson.put(videoKey, percentWatched)
                sessionManager?.saveVideosProgess(currentJson)
            }
        }else{
            val json = JSONObject()
            json.put(videoKey,percentWatched)
            sessionManager?.saveVideosProgess(json)
        }
    }

    override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23 && !isStorageVideo) {
            releasePlayer()
        }
    }

}