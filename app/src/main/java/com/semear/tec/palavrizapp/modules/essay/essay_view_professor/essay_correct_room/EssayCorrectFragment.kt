package com.semear.tec.palavrizapp.modules.essay.essay_view_professor.essay_correct_room

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.modules.essay.photo_zoom.ImageZoomActivity
import com.semear.tec.palavrizapp.utils.commons.DialogHelper
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_essay_correct.*
import java.io.File


class EssayCorrectFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private var viewmodel: EssayCorrectViewModel? = null

    private val SELECT_VIDEO = 300
    private val REQUEST_READ_STORAGE = 400
    private val REQUEST_WRITE_STORAGE = 401

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        arguments?.let {
            viewmodel?.setExtras(it)
        }
    }

    private fun initViewModel() {
        viewmodel = ViewModelProviders.of(this).get(EssayCorrectViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObservers()
    }

    private fun registerObservers() {
        viewmodel?.actualEssay?.observe(this, Observer {
            if (it != null){
                setupView(it)
            }
        })
        viewmodel?.essayImageUrlLiveData?.observe(this, Observer {
            showImageEssay(it)
        })
        viewmodel?.viewEvent?.observe(this, Observer {
            when(it){
                is EssayCorrectViewModel.ViewEvent.FeedBackSent -> {
                    if (it.success){
                        activity?.finish()
                    }
                }
            }
        })
    }

    private fun setupReadMode(essay: Essay){
        btn_send_feedback?.visibility = View.GONE
        btn_cancel?.text = "Voltar"
        btn_attachment_video_feedback?.visibility = View.GONE
        et_feedback_text?.visibility = View.INVISIBLE
        tv_feedback_text?.visibility = View.VISIBLE
        tv_feedback_text?.text = essay.feedback?.text
    }

    private fun setupView(essay: Essay){
        setupImageEssay(essay.url)
        setupTitleAndTheme(essay)
        setupVideoAttachmentButton()
        setupSendFeedbackButton(essay)
        setupCancelButton(essay)
        setupDownloadImageLink(essay.url)
        if (essay.isReadMode == true){
            setupReadMode(essay)
        }
    }

    private fun setupSendFeedbackButton(essay: Essay) {
        btn_send_feedback?.setOnClickListener {
            viewmodel?.onSendEssayFeedback(essay,  et_feedback_text?.text.toString(), "")
        }
    }

    private fun setupCancelButton(essay: Essay) {
        btn_cancel?.setOnClickListener {
            activity?.finish()
        }
    }

    private fun setupDownloadImageLink(filename: String){
        tv_download_image?.setOnClickListener {
            viewmodel?.downloadEssayImage(filename){
                val fhirPath = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val file = File(fhirPath, "$it.png")
                if(Build.VERSION.SDK_INT>=24) {
                    val uri = FileProvider.getUriForFile(context ?: return@downloadEssayImage, "com.semear.tec.palavrizapp.provider", file)
                    showPhoto(uri)
                }else{
                    showPhoto(Uri.fromFile(file))
                }
            }
        }
    }

    private fun showPhoto(photoUri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(photoUri, "image/*")
        startActivity(intent)
    }

    private fun setupTitleAndTheme(actualEssay: Essay){
        tv_theme_essay?.text = String.format(getString(R.string.tv_theme_placeholder), actualEssay?.theme)
        tv_title_essay?.text = actualEssay?.title
    }


    private fun setupImageEssay(url: String){
        viewmodel?.getEssayImage(url)

        iv_essay?.setOnClickListener {
            val itn = Intent(activity, ImageZoomActivity::class.java)
            val bitmap = (iv_essay.drawable as BitmapDrawable).bitmap
            itn.putExtra(Constants.EXTRA_IMAGE_FULL_SCREEN, bitmap)
            startActivity(itn)
        }
    }

    fun setupVideoAttachmentButton(){
        btn_attachment_video_feedback?.setOnClickListener {
            requestStoragePermission()
        }
    }

    fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_STORAGE)

        } else {
            requestWriteStoragePermission()
        }
    }

    private fun requestWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE)

        } else {
            val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, SELECT_VIDEO)
        }
    }


    private fun showImageEssay(urlImage: String?){
        val activity = activity as Activity
        Picasso.get().load(urlImage).into(iv_essay, object: com.squareup.picasso.Callback {
            override fun onError(e: Exception?) {
                DialogHelper.showAlert(activity, "Erro", "Erro ao abrir imagem, contate nossos administradores" )
            }

            override fun onSuccess() {
                iv_essay?.visibility = View.VISIBLE
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_essay_correct, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(essay: Essay, isReadMode: Boolean = false) =
                EssayCorrectFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(Constants.EXTRA_ESSAY, essay)
                        putBoolean(Constants.EXTRA_ESSAY_READ_MODE, isReadMode)
                    }
                }
    }

}
