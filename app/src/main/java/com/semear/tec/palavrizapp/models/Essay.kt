package com.semear.tec.palavrizapp.models

data class Essay(var title: String, var status: StatusEssay, var  url: String, var feedback: Feedback? = null)