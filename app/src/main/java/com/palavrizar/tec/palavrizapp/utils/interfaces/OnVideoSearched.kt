package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.palavrizar.tec.palavrizapp.models.Video


interface OnVideoSearched {
    fun onVideosSearch(videoList: ArrayList<Video>)
}