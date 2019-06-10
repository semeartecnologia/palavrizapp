package com.semear.tec.palavrizapp.models

data class Video(var orderVideo: Int = 0,  var videoPlan: Plans? = null, var videoKey: String = "", var title: String = "",var  description: String = "",var  category: String = "", var path: String = "", var videoThumb: String? = null)
