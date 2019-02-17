package com.semear.tec.palavrizapp.modules.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.Toast
import com.semear.tec.palavrizapp.modules.login.LoginActivity

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

    fun redirectToLogin(){
        val it = Intent(this, LoginActivity::class.java)
        startActivity(it)
    }

    fun showToast(text: String, show: Boolean) {
        if (show)
            Toast.makeText(application, text, Toast.LENGTH_SHORT).show()
    }

}