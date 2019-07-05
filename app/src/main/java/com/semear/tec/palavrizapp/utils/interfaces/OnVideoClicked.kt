package com.semear.tec.palavrizapp.utils.interfaces

import com.semear.tec.palavrizapp.models.Video

interface OnVideoEvent {
    fun onVideoClicked(v: Video)
    fun onVideoMoved()
}