package com.semear.tec.palavrizapp.modules.classroom

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.modules.classroom.input_comment.AddCommentActivity
import com.semear.tec.palavrizapp.modules.classroom.video_view.VideoFragment
import com.semear.tec.palavrizapp.utils.adapters.CommentsAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_COMMENT
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_KEY
import com.semear.tec.palavrizapp.utils.interfaces.OnReplyClicked
import kotlinx.android.synthetic.main.activity_classroom.*
import kotlinx.android.synthetic.main.item_add_question.*


class ClassroomActivity : BaseActivity(), OnReplyClicked {


    private var videoUrl = ""
    private var videoKey = ""
    private var classroomViewModel: ClassroomViewModel? = null

    private lateinit var adapter: CommentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom)

        initViewModel()
        adapter = CommentsAdapter(classroomViewModel?.userCanReply() ?: false, this)
        setupView()

    }

    fun setupView(){
        setupActionBar()
        setupExtras()
        setupViewObservers()
        setupVideoFragment()
        setupCommentsRecycler()
        loadComments()
    }

    private fun setupCommentsRecycler() {
        rv_comments?.layoutManager = LinearLayoutManager(this)
        rv_comments?.adapter = adapter
    }


    private fun loadComments() {
        classroomViewModel?.loadComments(videoKey)
    }

    private fun setupVideoFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame_video, VideoFragment.newInstance(videoUrl)).commit()
    }

    fun initViewModel(){
        classroomViewModel = ViewModelProviders.of(this).get(ClassroomViewModel::class.java)
        classroomViewModel?.initViewModel()
    }

    private fun setupActionBar(){
        supportActionBar?.title = getString(R.string.actionbar_classroom_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onReplyClicked(commentId: String?) {
        val intent = Intent(this, AddCommentActivity::class.java)
        intent.putExtra(EXTRA_VIDEO_KEY, videoKey)
        intent.putExtra(EXTRA_VIDEO_COMMENT, commentId)
        startActivity(intent)
    }

    private fun setupExtras(){
        if (intent != null) {
            videoUrl = intent?.getStringExtra(EXTRA_COD_VIDEO) ?: ""
            video_title?.text = intent?.getStringExtra(EXTRA_TITLE_VIDEO)
            video_description?.text = intent?.getStringExtra(EXTRA_DESCRPTION_VIDEO)
            videoKey = intent?.getStringExtra(EXTRA_VIDEO_KEY) ?: ""
        }
    }

    private fun setupViewObservers(){
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

        classroomViewModel?.isUserFirstTime?.observe(this, Observer { it ->
            if (it == true){
                //  next_lesson?.text = getString(R.string.btn_concluir)
                //  next_lesson?.setOnClickListener { finish() }
            }else{
                //  next_lesson?.setOnClickListener { }
            }
        })

        layout_question?.setOnClickListener {
            val intent = Intent(this, AddCommentActivity::class.java)
            intent.putExtra(EXTRA_VIDEO_KEY, videoKey)
            startActivity(intent)
        }

    }



}
