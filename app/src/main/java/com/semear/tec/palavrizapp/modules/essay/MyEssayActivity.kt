package com.semear.tec.palavrizapp.modules.essay

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.semear.tec.palavrizapp.R

class MyEssayActivity : AppCompatActivity() {

    private var viewmodel: MyEssayViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_essay)
        supportActionBar?.title = getString(R.string.title)

        initViewModel()
    }

    private fun initViewModel(){
        viewmodel = ViewModelProviders.of(this).get(MyEssayViewModel::class.java)
    }

    private fun setupView(){
        checkUserHasEssay()
    }

    private fun checkUserHasEssay() {

    }
}
