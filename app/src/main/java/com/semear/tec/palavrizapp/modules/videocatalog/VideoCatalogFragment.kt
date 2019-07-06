package com.semear.tec.palavrizapp.modules.videocatalog


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.semear.tec.palavrizapp.utils.adapters.VideosAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.interfaces.OnVideoEvent
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository
import kotlinx.android.synthetic.main.fragment_video_catalog.*

/**
 * Fragmento de catalogo de aulas
 */
class VideoCatalogFragment : Fragment(), OnVideoEvent {

    private var videoCatalogViewModel: VideoCatalogViewModel? = null
    private var recyclerTheme1: RecyclerView? = null
    private var mAdapter: VideosAdapter? = null
    private var videoRepository: VideoRepository? = null
    private var categorySpinner: Spinner? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoCatalogViewModel = ViewModelProviders.of(this).get(VideoCatalogViewModel::class.java)
        if (activity != null) {
            videoRepository = VideoRepository(activity!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_video_catalog, container, false)

        setupView(v)
        registerObservers()
        return v
    }

    private fun setupView(v: View) {
        videoCatalogViewModel?.getPlanName()
        mAdapter = VideosAdapter(this)
        recyclerTheme1 = v.findViewById(R.id.rv_themes)
        recyclerTheme1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerTheme1?.adapter = mAdapter

        progressBar = v.findViewById(R.id.progress_loading_videos)
        getVideoList()

    }

    fun registerObservers(){
        videoCatalogViewModel?.namePlanLiveData?.observe(this, Observer {
            if (!it.isNullOrBlank()){
                tv_plan_name?.text = it
            }else{
                tv_plan_name?.visibility = View.GONE
            }
        })
    }



    fun getVideoList() {
        if (activity == null) return
        videoRepository?.getVideoList { videoList ->
            mAdapter?.addAllVideo(videoList)
            progressBar?.visibility = View.GONE
            recyclerTheme1?.visibility = View.VISIBLE
        }
    }

    override fun onVideoMoved() {
    }

    override fun onVideoClicked(v: Video) {
        val it = Intent(activity as Activity, ClassroomActivity::class.java)
        it.putExtra(Constants.EXTRA_VIDEO, v)
        startActivity(it)
    }
}
