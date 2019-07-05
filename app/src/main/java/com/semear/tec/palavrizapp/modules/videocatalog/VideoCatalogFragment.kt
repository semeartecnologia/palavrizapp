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
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.semear.tec.palavrizapp.utils.adapters.VideosAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.interfaces.OnVideoEvent
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository
import kotlinx.android.synthetic.main.fragment_themes.*

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

        val v = inflater.inflate(R.layout.fragment_themes, container, false)

        setupView(v)
        registerObservers()
        return v
    }

    private fun loadThemes(){
        videoCatalogViewModel?.getVideoThemeList()
    }

    private fun loadConcepts(){
        videoCatalogViewModel?.getVideoConceptList()
    }

    private fun loadStructures(){
        videoCatalogViewModel?.getVideoStructureList()
    }

    private fun setupView(v: View) {
        videoCatalogViewModel?.getPlanName()
        mAdapter = VideosAdapter(this)
        recyclerTheme1 = v.findViewById(R.id.rv_themes)
        recyclerTheme1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerTheme1?.adapter = mAdapter

        progressBar = v.findViewById(R.id.progress_loading_videos)

        categorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                mAdapter?.clearVideoList()
                progressBar?.visibility = View.VISIBLE
                recyclerTheme1?.visibility = View.GONE
                getVideoList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        loadFilters()
    }

    private fun loadFilters() {
        loadThemes()
        loadConcepts()
        loadStructures()
    }

    fun registerObservers(){
        videoCatalogViewModel?.structuresListLiveData?.observe(this, Observer {
            if (it != null) {
                val defaultValue = getString(R.string.choose_structure)
                val structureListString = arrayListOf<String>()
                structureListString.add(defaultValue)
                it.forEach { structure ->
                    structureListString.add(structure.structure)
                }

                val adapterStructure = ArrayAdapter(activity as Activity,
                        android.R.layout.simple_spinner_item, structureListString)

                adapterStructure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                structure_spinner?.adapter = adapterStructure

            }
        })
        videoCatalogViewModel?.conceptsListLiveData?.observe(this, Observer {
            if (it != null) {
                val defaultValue = getString(R.string.choose_conceito)
                val conceptListString = arrayListOf<String>()
                conceptListString.add(defaultValue)
                it.forEach { concept ->
                    conceptListString.add(concept.concept)
                }

                val adapterConcept = ArrayAdapter(activity as Activity,
                        android.R.layout.simple_spinner_item, conceptListString)
                adapterConcept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                concept_spinner?.adapter = adapterConcept
            }
        })
        videoCatalogViewModel?.themeListLiveData?.observe(this, Observer {
            if (it != null) {
                val defaultValue = getString(R.string.choose_theme)
                val themeListString = arrayListOf<String>()
                themeListString.add(defaultValue)
                it.forEach { theme ->
                    themeListString.add(theme.themeName)
                }

                val adapter = ArrayAdapter(activity as Activity,
                        android.R.layout.simple_spinner_item, themeListString)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                theme_spinner?.adapter = adapter
            }
        })
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
            null
        }
    }

    override fun onVideoMoved() {
    }

    override fun onVideoClicked(v: Video) {
        val it = Intent(activity as Activity, ClassroomActivity::class.java)
        it.putExtra(Constants.EXTRA_COD_VIDEO, v.path)
        it.putExtra(Constants.EXTRA_TITLE_VIDEO, v.title)
        //it.putExtra(EXTRA_SUBTITLE_VIDEO, v.);
        it.putExtra(Constants.EXTRA_DESCRPTION_VIDEO, v.description)
        it.putExtra(Constants.EXTRA_VIDEO_KEY, v.videoKey)
        startActivity(it)
    }
}
