package com.palavrizar.tec.palavrizapp.modules.classroom

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Video
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.modules.classroom.video_view.VideoFragment
import com.palavrizar.tec.palavrizapp.utils.adapters.CommentsAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.FileHelper
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_FIRST
import kotlinx.android.synthetic.main.activity_classroom.*
import java.io.File


class ClassroomActivity : BaseActivity() {


    private var video: Video? = null
    private var isFirstTime: Boolean? = null
    private var classroomViewModel: ClassroomViewModel? = null

    private lateinit var adapter: CommentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        if (isFirstTime == true) {
            setContentView(R.layout.activity_classroom_first_time)
            supportActionBar?.hide()
        }else{
            setContentView(R.layout.activity_classroom)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

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
        supportFragmentManager.beginTransaction().replace(R.id.frame_video, VideoFragment.newInstance(videoPath, video?.videoKey)).commit()
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
            isFirstTime = intent?.getBooleanExtra(EXTRA_VIDEO_FIRST, false)
        }
    }

    private fun setupNextClassButton(video: Video?){
        if (video != null) {
            btn_next_class?.visibility = View.VISIBLE
            if (isFirstTime == true || video.orderVideo == "-1"){
                btn_next_class?.text = getString(R.string.btn_concluir)
            }
            btn_next_class?.setOnClickListener {

                if (isFirstTime == true){
                    finish()
                }else{
                    finish()
                    val itent = Intent(this, ClassroomActivity::class.java)
                    itent.putExtra(Constants.EXTRA_VIDEO, video)
                    startActivity(itent)
                }

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
