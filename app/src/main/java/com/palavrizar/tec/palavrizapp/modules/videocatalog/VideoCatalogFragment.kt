package com.palavrizar.tec.palavrizapp.modules.videocatalog


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
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Video
import com.palavrizar.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.palavrizar.tec.palavrizapp.utils.adapters.VideosAdapter
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnVideoEvent
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.VideoRepository
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
    private var toggleConcept: ToggleButton? = null
    private var toggleTheme: ToggleButton? = null
    private var toggleStructure: ToggleButton? = null

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

        setupToggleButtons()
        setupFilterButton()
    }

    private fun setupToggleButtons() {
        toggleConcept?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                toggleButtonsFilter(R.id.toggleConcept)
                getVideoList("concept")
                sessionManager?.videoConceptFilter = true
            }else{
                sessionManager?.videoConceptFilter = false
                checkAllToogleOff()
            }
        }
        toggleTheme?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                toggleButtonsFilter(R.id.toggleTheme)
                getVideoList("themeName")
                sessionManager?.videoThemeFilter = true
            }else{
                sessionManager?.videoThemeFilter = false
                checkAllToogleOff()
            }
        }
        toggleStructure?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                toggleButtonsFilter(R.id.toggleStructure)
                getVideoList("structure")
                sessionManager?.videoStructureFilter = true
            }else{
                sessionManager?.videoStructureFilter = false
                checkAllToogleOff()
            }
        }

        if (sessionManager?.videoStructureFilter == true){
            toggleStructure?.isChecked = true
        }
        if (sessionManager?.videoThemeFilter == true){
            toggleTheme?.isChecked = true
        }
        if (sessionManager?.videoConceptFilter == true){
            toggleConcept?.isChecked = true
        }
    }

    private fun checkAllToogleOff() {
        if (toggleConcept?.isChecked == false && toggleTheme?.isChecked == false && toggleStructure?.isChecked == false){
            getVideoList()
        }
    }

    private fun toggleButtonsFilter(res: Int){
        if (res == R.id.toggleStructure){
            toggleTheme?.isChecked = false
            toggleConcept?.isChecked = false
            sessionManager?.videoConceptFilter = false
            sessionManager?.videoThemeFilter = false
        }else if(res == R.id.toggleTheme){
            toggleConcept?.isChecked = false
            toggleStructure?.isChecked = false
            sessionManager?.videoStructureFilter = false
            toggleConcept?.isChecked = false
        }else if(res == R.id.toggleConcept){
            toggleTheme?.isChecked = false
            toggleStructure?.isChecked = false
            sessionManager?.videoStructureFilter = false
            sessionManager?.videoThemeFilter = false
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
                    tv_plan_name?.text = it
                }else{
                    tv_plan_name?.visibility = View.GONE
                }
            })
        }


    override fun onResume() {
        super.onResume()
        if (sessionManager?.videoStructureFilter == true){
            getVideoList("structure")
        }else if (sessionManager?.videoThemeFilter == true){
            getVideoList("themeName")
        }else if (sessionManager?.videoConceptFilter == true){
            getVideoList("concept")
        }else{
            getVideoList()
        }
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
