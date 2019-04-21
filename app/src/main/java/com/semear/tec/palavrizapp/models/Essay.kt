package com.semear.tec.palavrizapp.models

data class Essay(var title: String = "", var theme: String = "", var status: StatusEssay = StatusEssay.UPLOADED, var  url: String = "", var feedback: Feedback? = null)