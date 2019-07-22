package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.palavrizar.tec.palavrizapp.models.Video

interface OnVideoEvent {
    fun onVideoClicked(v: Video)
    fun onVideoMoved()
}