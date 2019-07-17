package com.semear.tec.palavrizapp.modules.videocatalog


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.models.VideoFilter
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.semear.tec.palavrizapp.utils.adapters.VideosAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.interfaces.OnVideoEvent
import com.semear.tec.palavrizapp.utils.repositories.SessionManager
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository
import kotlinx.android.synthetic.main.fragment_video_catalog.*

/**
 * Fragmento de catalogo de aulas
 */
class VideoCatalogFragment : Fragment(), OnVideoEvent {

    private var videoCatalogViewModel: VideoCatalogViewModel? = null
    private var recyclerTheme1: RecyclerView? = null
    private var iv_filter_btn: ImageView? = null
    private var mAdapter: VideosAdapter? = null
    private var videoRepository: VideoRepository? = null
    private var sessionManager: SessionManager? = null
    private var progressBar: ProgressBar? = null
    private var videoFilter: VideoFilter? = null
    private var theme_spinner: Spinner? = null
    private var structure_spinner: Spinner? = null
    private var concept_spinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoCatalogViewModel = ViewModelProviders.of(this).get(VideoCatalogViewModel::class.java)
        if (activity != null) {
            videoRepository = VideoRepository(activity!!)
            sessionManager = SessionManager(activity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_video_catalog, container, false)
        setupView(v)
        registerObservers()
        return v
    }

    private fun loadThemes(){
        videoCatalogViewModel?.getVideoThemeList()

        theme_spinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var themeSelected = parent?.getItemAtPosition(position).toString()
                if (position == 0){
                    themeSelected = ""
                }

                if (videoFilter == null) {
                    videoFilter = VideoFilter()
                    videoFilter?.themeName = themeSelected
                }else{
                    videoFilter?.themeName = themeSelected
                }
                //getVideoList(videoFilter)
            }

        }
    }

    private fun loadConcepts(){
        videoCatalogViewModel?.getVideoConceptList()

        concept_spinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var conceptSelected = parent?.getItemAtPosition(position).toString()
                if (position == 0){
                    conceptSelected = ""
                }

                if (videoFilter == null) {
                    videoFilter = VideoFilter()
                    videoFilter?.concept = conceptSelected
                }else{
                    videoFilter?.concept = conceptSelected
                }
                //getVideoList(videoFilter)
            }

        }
    }

    private fun loadStructures(){
        videoCatalogViewModel?.getVideoStructureList()

        structure_spinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var structureSelected = parent?.getItemAtPosition(position).toString()
                if (position == 0){
                    structureSelected = ""
                }

                if (videoFilter == null) {
                    videoFilter = VideoFilter()
                    videoFilter?.structure = structureSelected
                }else{
                    videoFilter?.structure = structureSelected
                }
                //getVideoList(videoFilter)
            }

        }
    }

    private fun setupView(v: View) {
        videoCatalogViewModel?.getPlanName()
        mAdapter = VideosAdapter(this)
        recyclerTheme1 = v.findViewById(R.id.rv_themes)
        recyclerTheme1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerTheme1?.adapter = mAdapter

        progressBar = v.findViewById(R.id.progress_loading_videos)
        iv_filter_btn = v.findViewById(R.id.iv_filter_btn)
        concept_spinner = v.findViewById(R.id.concept_spinner)
        theme_spinner = v.findViewById(R.id.theme_spinner)
        structure_spinner = v.findViewById(R.id.structure_spinner)

        loadFilters()
        setupFilterButton()
    }

    private fun setupFilterButton() {
        iv_filter_btn?.setOnClickListener {

            if (theme_spinner?.visibility == View.GONE) {
                iv_filter_btn?.setColorFilter(ContextCompat.getColor(activity as Activity, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
                theme_spinner?.visibility = View.VISIBLE
                concept_spinner?.visibility = View.VISIBLE
                structure_spinner?.visibility = View.VISIBLE
            } else {
                iv_filter_btn?.setColorFilter(ContextCompat.getColor(activity as Activity, android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                theme_spinner?.visibility = View.GONE
                concept_spinner?.visibility = View.GONE
                structure_spinner?.visibility = View.GONE
            }
        }
        iv_filter_btn?.performClick()
    }

        private fun loadFilters() {
            loadThemes()
            loadConcepts()
            loadStructures()
        }

        fun registerObservers(){
            videoCatalogViewModel?.structuresListLiveData?.observe(this, Observer {
                if (it != null) {
                    val defaultValue = getString(R.string.all_structure)
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
                    val defaultValue = getString(R.string.all_conceito)
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
                    val defaultValue = getString(R.string.all_themes)
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


    override fun onResume() {
        super.onResume()
        getVideoList()
    }

    private fun getVideoList(videoFilter: VideoFilter? = null) {
            if (activity == null) return

            val jsonProgress = sessionManager?.videosProgress

            progressBar?.visibility = View.VISIBLE
            recyclerTheme1?.visibility = View.GONE
            videoRepository?.getVideoList(videoFilter) { videoList ->
                mAdapter?.addAllVideo(videoList, jsonProgress)
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
