package com.semear.tec.palavrizapp.modules.essay.essay_view_professor.essay_review

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.modules.essay.photo_zoom.ImageZoomActivity
import com.semear.tec.palavrizapp.utils.Commons
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_essay_review.*


class EssayReviewFragment() : Fragment() {

    private var actualEssay: Essay? = null
    private var listener: OnFragmentInteractionListener? = null
    private var viewmodel: EssayReviewViewModel? = null

    private var dialogProgres: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        arguments?.let {
            viewmodel?.setExtras(it)
        }
    }

    private fun initViewModel() {
        viewmodel = ViewModelProviders.of(this).get(EssayReviewViewModel::class.java)
    }

    private fun registerObservers(){
        viewmodel?.actualEssayLiveData?.observe(this, Observer {
            actualEssay = it

            if (actualEssay != null){
                setupView()
            }
        })
        viewmodel?.essayImageUrlLiveData?.observe(this, Observer {
            showImageEssay(it)
        })
        viewmodel?.dialogLiveData?.observe(this, Observer {
            when (it){
                is EssayReviewViewModel.Dialog.FailDialog -> {
                    showFailDialog()
                }
            }
        })
        viewmodel?.viewEventLiveData?.observe(this, Observer {
            when (it){
                is EssayReviewViewModel.ViewEvent.FailSettingOwner -> {
                    dialogProgres?.dismiss()
                }
                is EssayReviewViewModel.ViewEvent.SuccessSettingOwner -> {
                    dialogProgres?.dismiss()
                    listener?.onCorrectClicked(it.essayId)
                }
            }
        })
        viewmodel?.enableCorrectButton?.observe(this, Observer {
            btn_correct?.isEnabled = it == true
            if (it == true) {
                btn_correct?.setText(R.string.btn_correct_this_essay)
            }else{
                btn_correct?.setText(R.string.btn_correct_in_progress)
            }
        })
    }

    private fun showFailDialog() {
        Commons.showAlert(activity as Activity, getString(R.string.dialog_essay_feedback_already_taken_title), getString(R.string.dialog_essay_feedback_already_taken_content), "Ok")
    }

    private fun showImageEssay(urlImage: String?){
        val activity = activity as Activity
        Picasso.get().load(urlImage).into(image_essay, object: com.squareup.picasso.Callback {
            override fun onError(e: Exception?) {
                Commons.showAlert(activity, "Erro", "Erro ao abrir imagem, contate nossos administradores" )
            }

            override fun onSuccess() {
                image_essay?.visibility = View.VISIBLE
                layout_progress_essay?.visibility = View.GONE
            }
        })
    }

    private fun setupView() {
        setupTitleAndTheme()
        setupImageEssay()
        setupAuthorDetails()
        setupCorrectButton()
        setupCancelButton()
    }

    private fun setupTitleAndTheme(){
        tv_essay_theme?.text = String.format(getString(R.string.tv_theme_placeholder), actualEssay?.theme)
        tv_essay_title?.text = actualEssay?.title
    }

    private fun setupCorrectButton(){
        val activity = activity as Activity

        btn_correct.setOnClickListener {
            Commons.showYesNoMessage(activity, getString(R.string.dialog_confirmation_correct_title), getString(R.string.dialog_confirmation_correct_content)) {
                dialogProgres = Commons.createDialogProgress(activity, "", false)
                setCorrectOwner()
            }
        }
    }

    private fun setupCancelButton(){
        val activity = activity as Activity

        btn_cancel.setOnClickListener {
            activity.finish()
        }
    }

    private fun setupAuthorDetails(){
        tv_profile_name_author?.text = actualEssay?.author?.fullname
        if (!actualEssay?.author?.photoUri.isNullOrBlank()) {
            Picasso.get().load(actualEssay?.author?.photoUri).into(tv_profile_image_author)
        }
    }

    private fun setupImageEssay(){
        viewmodel?.getEssayImage(actualEssay?.url)

        image_essay?.setOnClickListener {
            val itn = Intent(activity, ImageZoomActivity::class.java)
            val bitmap = (image_essay.drawable as BitmapDrawable).bitmap
            itn.putExtra(Constants.EXTRA_IMAGE_FULL_SCREEN, bitmap)
            startActivity(itn)
        }
    }

    private fun setCorrectOwner(){
        viewmodel?.onButtonCorrectClicked(actualEssay?.essayId ?: "fail")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_essay_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObservers()
    }

    interface OnFragmentInteractionListener {
        fun onCorrectClicked(essayId: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(essay: Essay) =
                EssayReviewFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(Constants.EXTRA_ESSAY, essay)
                    }
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
