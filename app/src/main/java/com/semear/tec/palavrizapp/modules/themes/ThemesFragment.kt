package com.semear.tec.palavrizapp.modules.themes


import android.app.Activity
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
import com.semear.tec.palavrizapp.utils.adapters.ThemesAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.interfaces.OnVideoClicked
import com.semear.tec.palavrizapp.utils.repositories.VideoRepository

import java.util.ArrayList

/**
 * Fragmento de seleção de Temas
 */
class ThemesFragment : Fragment(), OnVideoClicked {


    private var themesViewModel: ThemesViewModel? = null
    private var recyclerTheme1: RecyclerView? = null
    private var mAdapter: ThemesAdapter? = null
    private var videoRepository: VideoRepository? = null
    private var categorySpinner: Spinner? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themesViewModel = ViewModelProviders.of(this).get(ThemesViewModel::class.java)
        if (activity != null) {
            videoRepository = VideoRepository(activity!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_themes, container, false)

        setupView(v)
        getCategoryList()
        return v
    }

    private fun setupView(v: View) {
        mAdapter = ThemesAdapter(this)
        recyclerTheme1 = v.findViewById(R.id.rv_themes)
        recyclerTheme1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerTheme1?.adapter = mAdapter

        categorySpinner = v.findViewById(R.id.category_spinner)
        progressBar = v.findViewById(R.id.progress_loading_videos)

        categorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val category = categorySpinner!!.getItemAtPosition(position) as String
                mAdapter?.clearVideoList()
                progressBar?.visibility = View.VISIBLE
                recyclerTheme1?.visibility = View.GONE
                getVideoList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }


    fun getCategoryList() {
        if (activity == null) return
        val categoryArray = ArrayList<String>()

        videoRepository!!.getCategoryList { categories ->
            var categorySearch = ""
            for (i in categories.indices) {
                if (i == 0) {
                    categorySearch = categories[i].category
                }
                categoryArray.add(categories[i].category)
            }

            val adapter = ArrayAdapter(
                    activity!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryArray
            )

            categorySpinner!!.adapter = adapter
            getVideoList()
            null
        }
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
