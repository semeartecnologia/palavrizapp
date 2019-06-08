package com.semear.tec.palavrizapp.modules.admin.videos

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R


class ListVideosFragment : Fragment() {

    companion object {
        fun newInstance() = ListVideosFragment()
    }

    private lateinit var viewModel: ListVideosViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_videos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListVideosViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
