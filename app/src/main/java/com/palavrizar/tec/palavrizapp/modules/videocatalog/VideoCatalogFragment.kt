package com.palavrizar.tec.palavrizapp.modules.videocatalog


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Video
import com.palavrizar.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.palavrizar.tec.palavrizapp.utils.adapters.VideosAdapter
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnVideoEvent
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnVideoSearched
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.VideoRepository
import kotlinx.android.synthetic.main.fragment_video_catalog.*
import org.jetbrains.anko.backgroundResource

/**
 * Fragmento de catalogo de aulas
 */
class VideoCatalogFragment : Fragment(), OnVideoEvent, OnVideoSearched {

    private var videoCatalogViewModel: VideoCatalogViewModel? = null
    private var recyclerTheme1: RecyclerView? = null
    private var iv_filter_btn: ImageView? = null
    private var mAdapter: VideosAdapter? = null
    private var videoRepository: VideoRepository? = null
    private var sessionManager: SessionManager? = null
    private var progressBar: ProgressBar? = null
    private var toggleConcept: TextView? = null
    private var toggleTheme: TextView? = null
    private var toggleStructure: TextView? = null
    private var togglePlaylist: TextView? = null

    private var themeIsChecked = false
    private var structureIsChecked = false
    private var conceptIsChecked = false
    private var playlistIsChecked = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchView()
    }

    private fun setupView(v: View) {
        videoCatalogViewModel?.getPlanName()
        mAdapter = VideosAdapter(this)
        recyclerTheme1 = v.findViewById(R.id.rv_themes)
        recyclerTheme1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerTheme1?.adapter = mAdapter

        progressBar = v.findViewById(R.id.progress_loading_videos)
        iv_filter_btn = v.findViewById(R.id.iv_filter_btn)
        toggleConcept = v.findViewById(R.id.toggleConcept)
        toggleTheme = v.findViewById(R.id.toggleTheme)
        toggleStructure= v.findViewById(R.id.toggleStructure)
        togglePlaylist  = v.findViewById(R.id.tooglePlaylist)

        setupToggleButtons()
        setupFilterButton()

    }


    private fun setupSearchView(){
        val myContext = this
        sv_videos?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    mAdapter?.filter(query, myContext)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    mAdapter?.filter(newText, myContext)
                }
                return true
            }

        })
    }

    private fun setupToggleButtons() {
        toggleConcept?.setOnClickListener {
            if (!conceptIsChecked){
                conceptIsChecked = true
                themeIsChecked = false
                structureIsChecked = false
                playlistIsChecked = false

                toggleButtonsFilter()
                getVideoList("concept")
                sessionManager?.videoConceptFilter = true
            }else{
                sessionManager?.videoConceptFilter = false
                checkAllToogleOff()
            }
        }
        toggleTheme?.setOnClickListener {
            if (!themeIsChecked){
                conceptIsChecked = false
                themeIsChecked = true
                structureIsChecked = false
                playlistIsChecked = false

                toggleButtonsFilter()
                getVideoList("themeName")
                sessionManager?.videoThemeFilter = true
            }else{
                sessionManager?.videoThemeFilter = false
                checkAllToogleOff()
            }
        }
        toggleStructure?.setOnClickListener {
            if (!structureIsChecked){
                conceptIsChecked = false
                themeIsChecked = false
                structureIsChecked = true
                playlistIsChecked = false

                toggleButtonsFilter()
                getVideoList("structure")
                sessionManager?.videoStructureFilter = true
            }else{
                sessionManager?.videoStructureFilter = false
                checkAllToogleOff()
            }
        }
        togglePlaylist?.setOnClickListener {
            sessionManager?.videoStructureFilter = false
            sessionManager?.videoThemeFilter = false
            sessionManager?.videoConceptFilter = false

            if (!playlistIsChecked){
                conceptIsChecked = false
                themeIsChecked = false
                structureIsChecked = false
                playlistIsChecked = true

                toggleButtonsFilter()
                getVideoList()
            }else{
                checkAllToogleOff()
            }
        }

        if (sessionManager?.videoStructureFilter == true){
            toggleStructure?.performClick()
        }
        if (sessionManager?.videoThemeFilter == true){
            toggleTheme?.performClick()
        }
        if (sessionManager?.videoConceptFilter == true){
            toggleConcept?.performClick()
        }
    }

    private fun checkAllToogleOff() {
        conceptIsChecked = false
        structureIsChecked = false
        themeIsChecked = false
        playlistIsChecked = false

        togglePlaylist?.setBackgroundResource(R.drawable.background_tootle_not_selected)
        toggleStructure?.setBackgroundResource(R.drawable.background_tootle_not_selected)
        toggleConcept?.setBackgroundResource(R.drawable.background_tootle_not_selected)
        toggleTheme?.setBackgroundResource(R.drawable.background_tootle_not_selected)
        getVideoList()
    }

    private fun toggleButtonsFilter(){
        when {
            structureIsChecked -> {
                toggleStructure?.setBackgroundResource(R.drawable.background_tootle_selected)
                toggleConcept?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleTheme?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                togglePlaylist?.setBackgroundResource(R.drawable.background_tootle_not_selected)


                sessionManager?.videoConceptFilter = false
                sessionManager?.videoThemeFilter = false

            }
            themeIsChecked -> {
                toggleStructure?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleConcept?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleTheme?.setBackgroundResource(R.drawable.background_tootle_selected)
                togglePlaylist?.setBackgroundResource(R.drawable.background_tootle_not_selected)

                sessionManager?.videoStructureFilter = false
                sessionManager?.videoConceptFilter = false

            }
            conceptIsChecked -> {
                toggleConcept?.setBackgroundResource(R.drawable.background_tootle_selected)
                togglePlaylist?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleStructure?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleTheme?.setBackgroundResource(R.drawable.background_tootle_not_selected)

                sessionManager?.videoStructureFilter = false
                sessionManager?.videoThemeFilter = false
            }
            playlistIsChecked -> {
                togglePlaylist?.setBackgroundResource(R.drawable.background_tootle_selected)
                toggleConcept?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleStructure?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleTheme?.setBackgroundResource(R.drawable.background_tootle_not_selected)
            }
        }
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

        /*private fun loadFilters() {
            loadThemes()
            loadConcepts()
            loadStructures()
        }*/

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
                    if (it == Constants.PLAN_FREE_ID){
                     tv_plan_name?.visibility = View.GONE
                    }
                    tv_plan_name?.text = it
                }else{
                    tv_plan_name?.visibility = View.GONE
                }
            })
        }


    override fun onResume() {
        super.onResume()
        when {
            sessionManager?.videoStructureFilter == true -> {
                getVideoList("structure")
                toggleStructure?.setBackgroundResource(R.drawable.background_tootle_selected)
                toggleConcept?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleTheme?.setBackgroundResource(R.drawable.background_tootle_not_selected)
            }
            sessionManager?.videoThemeFilter == true -> {
                getVideoList("themeName")
                toggleStructure?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleConcept?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleTheme?.setBackgroundResource(R.drawable.background_tootle_selected)
            }
            sessionManager?.videoConceptFilter == true -> {
                getVideoList("concept")
                toggleStructure?.setBackgroundResource(R.drawable.background_tootle_not_selected)
                toggleConcept?.setBackgroundResource(R.drawable.background_tootle_selected)
                toggleTheme?.setBackgroundResource(R.drawable.background_tootle_not_selected)
            }
            else -> getVideoList()
        }
    }

    override fun onVideosSearch(videoList: ArrayList<Video>) {
        mAdapter?.setVideoSearched(videoList, sessionManager?.videosProgress)
    }

    private fun getVideoList(videoFilter: String? = null) {
            if (activity == null) return

            val jsonProgress = sessionManager?.videosProgress

            progressBar?.visibility = View.VISIBLE
            recyclerTheme1?.visibility = View.GONE
            videoRepository?.getVideoList(videoFilter) { videoList ->
                mAdapter?.addAllVideo(videoList, jsonProgress)
                progressBar?.visibility = View.GONE
                recyclerTheme1?.visibility = View.VISIBLE

                (recyclerTheme1?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(mAdapter?.getIndexToScroll() ?: 0, 100)//
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
