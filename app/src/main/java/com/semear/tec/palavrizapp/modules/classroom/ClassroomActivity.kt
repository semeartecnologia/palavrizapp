package com.semear.tec.palavrizapp.modules.classroom

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.modules.classroom.video_view.VideoFragment
import com.semear.tec.palavrizapp.utils.adapters.CommentsAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO
import kotlinx.android.synthetic.main.activity_classroom.*


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

    private fun setupViewObservers(){
        classroomViewModel?.videoDownloadUrl?.observe(this, Observer {
            if (it != null) {
                setupVideoFragment(it)
            }
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
