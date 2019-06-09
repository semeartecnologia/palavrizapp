package com.semear.tec.palavrizapp.modules.admin.videos

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.utils.adapters.ThemesAdapter
import kotlinx.android.synthetic.main.list_videos_fragment.*


class ListVideosFragment : Fragment() {

    private lateinit var adapter: ThemesAdapter

    companion object {
        fun newInstance() = ListVideosFragment()
    }

    private lateinit var viewModel: ListVideosViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_videos_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListVideosViewModel::class.java)
        adapter = ThemesAdapter()
        setupRecyclerVideos()
        registerObservers()
        viewModel.fetchVideos()
    }

    private fun registerObservers() {
        viewModel.videoListLiveData.observe(this, Observer {
            if (it != null) {
                progress_loading_videos?.visibility = View.GONE
                adapter.addAllVideo(it)
            }
        })
    }

    private fun setupRecyclerVideos() {
        rv_videos?.layoutManager = LinearLayoutManager(context)
        rv_videos?.adapter = adapter
    }

}
