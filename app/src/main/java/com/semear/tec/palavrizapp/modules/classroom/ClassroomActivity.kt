package com.semear.tec.palavrizapp.modules.classroom

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.modules.classroom.video_view.VideoFragment
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO
import kotlinx.android.synthetic.main.activity_classroom.*


class ClassroomActivity : BaseActivity() {

    private var videoUrl = ""
    private var classroomViewModel: ClassroomViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom)
        setupView()
    }

    fun setupView(){
        initViewModel()
        setupActionBar()
        setupExtras()
        setupViewObservers()
        setupVideoFragment()
    }

    private fun setupVideoFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame_video, VideoFragment.newInstance(videoUrl)).commit()
    }

    fun initViewModel(){
        classroomViewModel = ViewModelProviders.of(this).get(ClassroomViewModel::class.java)
        classroomViewModel?.initViewModel()
    }

    fun setupActionBar(){
        supportActionBar?.title = getString(R.string.actionbar_classroom_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setupExtras(){
        if (intent != null) {
            videoUrl = intent?.getStringExtra(EXTRA_COD_VIDEO) ?: ""
            video_title?.text = intent?.getStringExtra(EXTRA_TITLE_VIDEO)
            video_description?.text = intent?.getStringExtra(EXTRA_DESCRPTION_VIDEO)
        }
    }

    fun setupViewObservers(){
        classroomViewModel?.isUserFirstTime?.observe(this, Observer { it ->
            if (it == true){
                //  next_lesson?.text = getString(R.string.btn_concluir)
                //  next_lesson?.setOnClickListener { finish() }
            }else{
                //  next_lesson?.setOnClickListener { }
            }
        })

    }



}
