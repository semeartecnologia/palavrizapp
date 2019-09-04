package com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.essay_correct_room

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.modules.essay.photo_zoom.ImageZoomActivity
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.FileHelper
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_essay_correct.*
import java.io.File


class EssayCorrectFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private var viewmodel: EssayCorrectViewModel? = null
    private var isReadMode: Boolean? = null

    private var videoUrl: String? = null
    private var videoUri: Uri? = null

    private val SELECT_VIDEO = 300
    private val REQUEST_READ_STORAGE = 400
    private val REQUEST_OPEN_VIDEO_STORAGE = 403
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
        viewmodel?.showProgress?.observe(this, Observer {
            if (it == true) {
                showProgress(true)
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

    private fun hideProgress(){
        layout_filename?.visibility = View.GONE
        progress_download_video_feedback?.visibility = View.GONE
    }

    private fun showProgress(blockButtons: Boolean){
        layout_filename?.visibility = View.VISIBLE
        progress_download_video_feedback?.visibility = View.VISIBLE
        tv_video_filename?.visibility = View.GONE
        open_it?.visibility = View.GONE
        delete_it?.visibility = View.GONE
        download_it?.visibility = View.GONE

        btn_send_feedback?.isEnabled = !blockButtons
        btn_cancel?.isEnabled = !blockButtons
        et_feedback_text?.isEnabled = !blockButtons
        btn_attachment_video_feedback?.isEnabled = !blockButtons
    }

    private fun setupReadMode(essay: Essay){
        btn_send_feedback?.visibility = View.GONE
        tv_download_image?.visibility = View.GONE
        btn_cancel?.text = "Voltar"
        btn_attachment_video_feedback?.visibility = View.GONE
        et_feedback_text?.visibility = View.INVISIBLE
        tv_feedback_text?.visibility = View.VISIBLE
        tv_feedback_text?.text = essay.feedback?.text

        if (essay.feedback?.urlVideo != null){
            val url = essay.feedback?.urlVideo
            btn_watch_video?.visibility = View.VISIBLE

            val urlSplit = url?.split("/")
            val filename = urlSplit?.get(urlSplit.size-1)

            btn_watch_video?.setOnClickListener {
                val path = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val file = File(path, "Palavrizapp/$filename.mp4")

                if (file.exists()){
                    if(Build.VERSION.SDK_INT>=24) {
                        val uri = FileProvider.getUriForFile(context ?: return@setOnClickListener, "com.palavrizar.tec.palavrizapp.provider", file)
                        requestStoragePermissioAndShowVideo(uri)
                    }else{
                        requestStoragePermissioAndShowVideo(Uri.fromFile(file))
                    }
                }else {
                    showProgress(false)
                    viewmodel?.downloadVideoFeedback(url!!) {
                        hideProgress()
                        val path = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        val file = File(path, "Palavrizapp/$it.mp4")
                        if (Build.VERSION.SDK_INT >= 24) {
                            val uri = FileProvider.getUriForFile(context
                                    ?: return@downloadVideoFeedback, "com.palavrizar.tec.palavrizapp.provider", file)
                            requestStoragePermissioAndShowVideo(uri)
                        } else {
                            requestStoragePermissioAndShowVideo(Uri.fromFile(file))
                        }
                    }
                }
            }
        }
    }

    private fun setupView(essay: Essay){
        setupImageEssay(essay.url)
        setupTitleAndTheme(essay)
        setupVideoAttachmentButton()
        setupSendFeedbackButton(essay)
        setupCancelButton(essay)
        setupDownloadImageLink(essay.url)
        if (essay.isReadMode == true){
            isReadMode = true
            setupReadMode(essay)
        }
    }

    private fun setupSendFeedbackButton(essay: Essay) {
        btn_send_feedback?.setOnClickListener {
            viewmodel?.onSendEssayFeedback(essay,  et_feedback_text?.text.toString(), videoUrl ?: "")
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
                val file = File(fhirPath, "Palavrizapp/$it.png")
                if(Build.VERSION.SDK_INT>=24) {
                    val uri = FileProvider.getUriForFile(context ?: return@downloadEssayImage, "com.palavrizar.tec.palavrizapp.provider", file)
                    showPhoto(uri)
                }else{
                    showPhoto(Uri.fromFile(file))
                }
                DialogHelper.showToast(activity as Activity, "Download feito em ${file.path}")

            }
        }
    }

    private fun showVideo(videoUri: Uri){
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(videoUri, "video/*")
        startActivity(intent)
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


    private fun requestStoragePermissioAndShowVideo(videoUri: Uri) {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            this.videoUri = videoUri

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_OPEN_VIDEO_STORAGE)

        } else {
            showVideo(videoUri)
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

    private fun setupFilename(filename: String, uploaded: Boolean){

        layout_filename?.visibility = View.VISIBLE
        tv_video_filename?.text = filename
        tv_video_filename?.visibility = View.VISIBLE
        btn_attachment_video_feedback?.isEnabled = false
        checkFileAlreadyDownloaded(filename, uploaded)

    }

    private fun deleteVideo(){
        layout_filename?.visibility = View.GONE
        tv_video_filename?.visibility = View.GONE
        open_it?.visibility = View.GONE
        delete_it?.visibility = View.GONE
        download_it?.visibility = View.GONE
        btn_attachment_video_feedback?.isEnabled = true
        videoUrl = ""
    }

    private fun checkFileAlreadyDownloaded(filename: String, uploaded: Boolean){
        val path = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(path, "Palavrizapp/$filename")

        if (isReadMode != true){
            delete_it?.visibility = View.VISIBLE

            delete_it?.setOnClickListener {
                deleteVideo()
            }
        }

        if (uploaded) {
            if (file.exists()) {
                open_it?.visibility = View.VISIBLE
                download_it?.visibility = View.GONE
            } else {
                open_it?.visibility = View.GONE
                download_it?.visibility = View.VISIBLE
            }
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_OPEN_VIDEO_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (this.videoUri != null) {
                        showVideo(this.videoUri!!)
                    }
                } else {
                    return
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_VIDEO){
            videoUrl = FileHelper.getRealPathFromURI(activity as Activity, data?.data ?: return)
            val splittedPath = videoUrl?.split("/")
            if (splittedPath != null) {
                setupFilename(splittedPath[splittedPath.size-1], false)
            }
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
