package com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.essay_correct_room

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.palavrizar.tec.palavrizapp.models.StatusEssay
import com.palavrizar.tec.palavrizapp.modules.classroom.FullscreenVideoActivity
import com.palavrizar.tec.palavrizapp.modules.essay.essay_view_professor.essay_review.video_view.EssayFeedbackView
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
    private var isVideoUpdated: Boolean = false

    private var videoUrl: String? = null
    private var videoUri: Uri? = null

    private val SELECT_VIDEO = 300
    private val UPDATE_VIDEO = 301
    private val REQUEST_READ_STORAGE = 400
    private val REQUEST_OPEN_VIDEO_STORAGE = 403
    private val REQUEST_WRITE_STORAGE = 401
    private val EXTRA_UPDATE = "isUpdate"


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
        viewmodel?.essayImageUrlFullScreenLiveData?.observe(this, Observer {
            if (it != null) {
                startFullscreenImageActivity(it)
            }
        })
        viewmodel?.viewEvent?.observe(this, Observer {
            when(it){
                is EssayCorrectViewModel.ViewEvent.FeedBackSent -> {
                    if (it.success){
                        DialogHelper.showOkMessage(activity as Activity, "", getString(R.string.correction_send_success), {
                            activity?.finish()
                        })
                    }
                }
                is EssayCorrectViewModel.ViewEvent.EssayUnreadable -> {
                    showUnreadableMessage()
                }
                is EssayCorrectViewModel.ViewEvent.TextEssayUpdated -> {
                    DialogHelper.showOkMessage(activity as Activity, "", getString(R.string.essay_text_updated), {
                        activity?.finish()
                    })
                }
            }
        })
    }

    fun showFeedbackUpdatedMessage(){
        DialogHelper.showOkMessage(activity as Activity, "", getString(R.string.essay_text_updated), {})
    }

    fun showUnreadableMessage(){
        DialogHelper.showOkMessage(activity as Activity, "", getString(R.string.essay_not_readable_message), {})
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
                        requestStoragePermissioAndShowVideo(Uri.fromFile(file))
                    }else{
                        requestStoragePermissioAndShowVideo(Uri.fromFile(file))
                    }
                }else {
                    showProgress(false)
                    viewmodel?.downloadVideoFeedback(url!!) {
                        if (it == null){
                            DialogHelper.showMessage(activity as Activity,"Erro", "Arquivo não encontrado!")
                        }else {
                            hideProgress()
                            val path = Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                            val file = File(path, "Palavrizapp/$it.mp4")
                            if (Build.VERSION.SDK_INT >= 24) {
                                requestStoragePermissioAndShowVideo(Uri.fromFile(file))
                            } else {
                                requestStoragePermissioAndShowVideo(Uri.fromFile(file))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupView(essay: Essay){
        setupImageEssay(essay.filename, essay.url)
        setupTitleAndTheme(essay)
        setupVideoAttachmentButton()
        setupSendFeedbackButton(essay)
        setupCancelButton(essay)
        setupDownloadImageLink(essay.filename)
        if (essay.isReadMode == true){
            isReadMode = true
            setupReadMode(essay)
        }
        if (essay.isReviewMode == true){
            setupReviewMode(essay)
        }
    }

    private fun setupReviewMode(essay: Essay) {
        btn_send_again?.visibility = View.VISIBLE
        btn_attachment_video_again?.visibility = View.VISIBLE
        et_feedback_text?.visibility = View.VISIBLE
        et_feedback_text?.isEnabled = true
        tv_feedback_text?.visibility = View.GONE
        et_feedback_text?.setText(essay.feedback?.text)

        rootView.requestFocus()
        btn_send_again?.setOnClickListener {

            if (!isVideoUpdated){
                val textFeedback = et_feedback_text?.text.toString()
                viewmodel?.editTextEssay(essay, textFeedback)
            }else{
                if (videoUrl.isNullOrBlank()){
                    DialogHelper.showOkMessage(activity as Activity, "", getString(R.string.essay_correction_no_video), {})
                }else {
                    viewmodel?.onUpdateEssayFeedback(essay, et_feedback_text?.text.toString(), videoUrl
                            ?: "")
                }
            }

        }
        btn_attachment_video_again?.setOnClickListener {
            requestStoragePermission(true)
        }
    }




    private fun setupSendFeedbackButton(essay: Essay) {
        btn_send_feedback?.setOnClickListener {
            if (videoUrl.isNullOrBlank()){
                DialogHelper.showOkMessage(activity as Activity, "", getString(R.string.essay_correction_no_video), {})
            }else {
                viewmodel?.onSendEssayFeedback(essay, et_feedback_text?.text.toString(), videoUrl
                        ?: "")
            }
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
        val it = Intent(activity, EssayFeedbackView::class.java)
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        it.putExtra("urlVideo", videoUri.toString())
        it.putExtra("videoKey", "")
        it.putExtra("position", 0)
        it.putExtra("window", 0)
        it.putExtra("isStorageVideo", true)

        activity?.startActivity(it)
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


    private fun setupImageEssay(filename: String, url: String){
        viewmodel?.getEssayImage(filename)

        iv_essay?.setOnClickListener {
           viewmodel?.showImageFullscreenClicked(filename)
        }
    }

    private fun startFullscreenImageActivity(url: String){
        val itn = Intent(activity, ImageZoomActivity::class.java)
        itn.putExtra(Constants.EXTRA_IMAGE_FULL_SCREEN, url)
        startActivity(itn)
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

    fun requestStoragePermission(isUpdate: Boolean = false) {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_STORAGE)

        } else {
            requestWriteStoragePermission(isUpdate)
        }
    }

    private fun requestWriteStoragePermission(isUpdate: Boolean = false) {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE)

        } else {
            val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            i.putExtra(EXTRA_UPDATE, isUpdate)
            if (isUpdate){
                startActivityForResult(i, UPDATE_VIDEO)
            }else{
                startActivityForResult(i, SELECT_VIDEO)
            }

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
                progress?.visibility = View.GONE
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_essay_correct, container, false)
    }

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

    override fun onResume() {

        fakeEdit?.requestFocus()
        super.onResume()
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
            getVideoPathAndSetupFilename(data)
        }else if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_VIDEO){
            isVideoUpdated = true
            getVideoPathAndSetupFilename(data)
        }
    }

    fun getVideoPathAndSetupFilename(data: Intent?){
        videoUrl = FileHelper.getRealPathFromURI(activity as Activity, data?.data ?: return)
        val splittedPath = videoUrl?.split("/")
        if (splittedPath != null) {
            setupFilename(splittedPath[splittedPath.size-1], false)
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
        fun newInstance(essay: Essay, isReadMode: Boolean = false, isReviewMode: Boolean = false) =
                EssayCorrectFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(Constants.EXTRA_ESSAY, essay)
                        putBoolean(Constants.EXTRA_ESSAY_READ_MODE, isReadMode)
                        putBoolean(Constants.EXTRA_ESSAY_REVIEW_MODE, isReviewMode)
                    }
                }
    }

}
