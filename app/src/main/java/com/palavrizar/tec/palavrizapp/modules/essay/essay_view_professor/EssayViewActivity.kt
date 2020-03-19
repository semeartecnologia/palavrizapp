package com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.models.StatusEssay
import com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.essay_correct_room.EssayCorrectFragment
import com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.essay_review.EssayReviewFragment
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import kotlinx.android.synthetic.main.activity_essay_view.*


class EssayViewActivity : AppCompatActivity(), EssayReviewFragment.OnFragmentInteractionListener, EssayCorrectFragment.OnFragmentInteractionListener {



    private lateinit var actualEssay: Essay
    private lateinit var actualFragment: Fragment
    private var isReadMode: Boolean = false
    private var isReviewMode: Boolean = false
    private var viewModel: EssayViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essay_view)

        initViewModel()
        setExtras(intent)
        setupView()
        registerObservers()

        if (isReadMode || actualEssay.status == StatusEssay.FEEDBACK_READY){
            startReadMode()
        }else {
            checkEssayHasOwner()
        }

    }

    private fun checkEssayHasOwner() {
        viewModel?.checkEssayFeedbackOwner(actualEssay.essayId)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(EssayViewModel::class.java)
    }

    fun changeFragment(fragment: Fragment, fragName: String) {
        actualFragment = fragment
        frameContent?.visibility = View.VISIBLE
        layout_load_essay?.visibility = View.GONE
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frameContent, fragment, fragName)
        ft.commit()
    }


    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    fun startReadMode(){
        changeFragment(EssayCorrectFragment.newInstance(actualEssay, true, isReviewMode), "EssayCorrect")
    }

    private fun setupView() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        actualFragment.onActivityResult(requestCode, resultCode, data)

    }

    private fun registerObservers(){
        viewModel?.viewEvent?.observe(this, Observer {
            when (it){
                is EssayViewModel.ViewEvent.UserConfirmed -> {
                    if (it.success){
                        changeFragment(EssayCorrectFragment.newInstance(actualEssay), "EssayCorrect")
                    }else{
                        changeFragment(EssayReviewFragment.newInstance(actualEssay), "EssayReview")
                    }
                }
                is EssayViewModel.ViewEvent.FeedbackNotFound -> {
                    changeFragment(EssayReviewFragment.newInstance(actualEssay), "EssayReview")
                }
            }
        })
    }

    override fun onCorrectClicked(id: String) {
       viewModel?.checkEssayFeedbackOwner(id)
    }

    override fun onFragmentInteraction(uri: Uri) {

    }



    private fun setExtras(intent: Intent?){
        if (intent != null) {
            actualEssay = intent.getParcelableExtra(Constants.EXTRA_ESSAY)
            isReadMode = intent.getBooleanExtra(Constants.EXTRA_ESSAY_READ_MODE, false)
            isReviewMode = intent.getBooleanExtra(Constants.EXTRA_ESSAY_REVIEW_MODE, false)

        }
    }
}
