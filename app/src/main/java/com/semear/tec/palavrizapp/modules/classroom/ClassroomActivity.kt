package com.semear.tec.palavrizapp.modules.classroom

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.modules.classroom.video_view.VideoFragment
import com.semear.tec.palavrizapp.utils.adapters.CommentsAdapter
import com.semear.tec.palavrizapp.utils.commons.FileHelper
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO
import kotlinx.android.synthetic.main.activity_classroom.*
import java.io.File


class ClassroomActivity : BaseActivity() {


    private var video: Video? = null
    private var classroomViewModel: ClassroomViewModel? = null

    private lateinit var adapter: CommentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom)

        initViewModel()
        setupView()

    }

    fun setupView(){
        setupActionBar()
        setupExtras()
        setupViewObservers()
        //setupCommentsRecycler()
        //loadComments()
        getVideoUrlDownload()
        setupVideoDetails()
        setupCheckNextClass()
        setupOpenPdf()

    }

    private fun setupCheckNextClass() {
        classroomViewModel?.getNextVideo(video?.orderVideo ?: return)
    }

    private fun setupOpenPdf() {
        if (!video?.pdfPath.isNullOrBlank()){
            btn_open_pdf?.visibility = View.VISIBLE
            btn_open_pdf?.setOnClickListener {
                val filename = video?.pdfPath?.split("/")?.lastOrNull() ?: ""

                val fhirPath = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val file = File(fhirPath, "$filename.pdf")

                if (file.exists()) {
                    FileHelper.openPdf(this, file)
                } else {
                    classroomViewModel?.downloadPdf(video?.pdfPath ?: "") {
                        val mPath = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        val mFile = File(mPath, "$it.pdf")
                        FileHelper.openPdf(this, mFile)
                    }
                }
            }
        }
    }

    private fun setupVideoDetails() {
        video_title?.text = video?.title
        video_description?.text = video?.description
    }

    private fun getVideoUrlDownload() {
        classroomViewModel?.getVideoUrlDownload(video?.path ?: return)
    }

    /*private fun setupCommentsRecycler() {
        rv_comments?.layoutManager = LinearLayoutManager(this)
        rv_comments?.adapter = adapter
    }*/


   /* private fun loadComments() {
        classroomViewModel?.loadComments(videoKey)
    }*/

    private fun setupVideoFragment(videoPath: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_video, VideoFragment.newInstance(videoPath)).commit()
    }

    fun initViewModel(){
        classroomViewModel = ViewModelProviders.of(this).get(ClassroomViewModel::class.java)
        classroomViewModel?.initViewModel()
    }

    private fun setupActionBar(){
        supportActionBar?.title = getString(R.string.actionbar_classroom_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

 /*   override fun onReplyClicked(commentId: String?) {
        val intent = Intent(this, AddCommentActivity::class.java)
        intent.putExtra(EXTRA_VIDEO_KEY, videoKey)
        intent.putExtra(EXTRA_VIDEO_COMMENT, commentId)
        startActivity(intent)
    }*/

    private fun setupExtras(){
        if (intent != null) {
            video = intent?.getParcelableExtra(EXTRA_VIDEO)
        }
    }

    private fun setupNextClassButton(video: Video?){
        if (video != null) {
            btn_next_class?.visibility = View.VISIBLE
            btn_next_class?.setOnClickListener {
                finish()
                val itent = Intent(this, ClassroomActivity::class.java)
                itent.putExtra(Constants.EXTRA_VIDEO, video)
                startActivity(itent)
            }
        }else{
            btn_next_class_disabled?.visibility = View.VISIBLE
            btn_next_class_disabled?.setOnClickListener {
                showToast(getString(R.string.no_next_class), true)
            }
        }
    }

    private fun setupViewObservers(){
        classroomViewModel?.videoDownloadUrl?.observe(this, Observer {
            if (it != null) {
                setupVideoFragment(it)
            }
        })
        classroomViewModel?.showNextClassButton?.observe(this, Observer {
            setupNextClassButton(it)
        })

        classroomViewModel?.commentsLiveData?.observe(this, Observer {
            progress_loading_comments?.visibility = View.GONE
            if (it != null && it.isNotEmpty()){
                rv_comments.visibility = View.VISIBLE
                adapter.commentsList.addAll(it)
                adapter.notifyDataSetChanged()
                question_label?.text = getString(R.string.questions_label, it.size)
            }else{
                if (it?.size == 0){
                    question_label?.text = getString(R.string.questions_label, 0)
                }
            }
        })



      /*  layout_question?.setOnClickListener {
            val intent = Intent(this, AddCommentActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_KEY, videoKey)
            startActivity(intent)
        }*/

    }



}
