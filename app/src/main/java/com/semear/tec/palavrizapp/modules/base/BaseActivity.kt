package com.semear.tec.palavrizapp.modules.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.dashboard.DashboardFragment

open class BaseActivity : AppCompatActivity() {

    //flag verificadora se é smartphone ou tablet
    var isPhone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        val dpHeight = outMetrics.heightPixels / density
        val dpWidth = outMetrics.widthPixels / density
        isPhone = dpHeight < 600 || dpWidth < 600
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}