package com.semear.tec.palavrizapp.modules.classroom.input_comment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.utils.constants.Constants
import kotlinx.android.synthetic.main.activity_add_comment.*


class AddCommentActivity : AppCompatActivity() {

    private var addCommentViewModel: AddCommentViewmodel? = null
    private var videoKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_comment)

        initViewModel()
        setExtra(intent)
        setupView()
    }

    private fun setupView() {
        action_send_question?.setOnClickListener {
            progress_comment_layout?.visibility = View.VISIBLE
            addCommentViewModel?.addComment(et_question?.text.toString(), videoKey){
                progress_comment_layout?.visibility = View.GONE
                finish()
            }
        }

        et_question?.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()){
                    action_send_question.visibility = View.GONE
                }else{
                    action_send_question.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun setExtra(intent: Intent){
        videoKey = intent.getStringExtra(Constants.EXTRA_VIDEO_KEY)
    }

    private fun initViewModel(){
        addCommentViewModel = ViewModelProviders.of(this).get(AddCommentViewmodel::class.java)
    }


    override fun onBackPressed() {
        finish()
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }
}
