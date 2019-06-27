package com.semear.tec.palavrizapp.modules.upload

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.semear.tec.palavrizapp.BuildConfig
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.PlanSwitch
import com.semear.tec.palavrizapp.models.Plans
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.utils.adapters.PlanListAdapter
import com.semear.tec.palavrizapp.utils.commons.DialogHelper
import com.semear.tec.palavrizapp.utils.commons.FileHelper
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.constants.Constants.BROADCAST_UPLOAD_DONE
import com.semear.tec.palavrizapp.utils.constants.Constants.BROADCAST_UPLOAD_PROGRESS
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_IS_EDIT
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_PATH
import kotlinx.android.synthetic.main.fragment_add_video.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit


class UploadActivity : BaseActivity() {

    private var videoUrl = ""
    private var filename = ""

    private var video: Video? = null
    private var isEdit = false
    private lateinit var uploadViewModel: UploadViewModel


    val arraySpinner = arrayOf("Categoria", "Língua Portuguesa", "Dicas Enem")

    private var adapter: PlanListAdapter = PlanListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_video)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        uploadViewModel = ViewModelProviders.of(this).get(UploadViewModel::class.java)

        setupExtras()
        setupView()
    }

    fun setupView(){
        setupFields()
        setupSpinner()
        setupVideo(videoUrl)
        setupUploadButton()
        setupDebugInfo()
        setupRecyclerPlans()
        setupPlansList()
        setupDeleteButton()
        registerObservers()
    }

    private fun setupDeleteButton() {
        if (isEdit){
            btn_delete?.visibility = View.VISIBLE
        }else{
            btn_delete?.visibility = View.GONE
        }

        btn_delete?.setOnClickListener {
            DialogHelper.showYesNoMessage(this, "", getString(R.string.delete_video_dialog_text)){
                if (video != null) {
                    uploadViewModel.deleteVideo(video!!)
                }
            }
        }
    }

    fun registerObservers(){
        uploadViewModel.deleteVideoLiveData.observe(this, Observer {
            if ( it == true){
                finish()
            }
        })
        uploadViewModel.editVideoSuccessLiveData.observe(this, Observer {
            if (it == true){
                finish()
            }
        })
    }

    private fun setupPlansList() {
        var arrayPlans = arrayListOf<PlanSwitch>()

        video?.videoPlan?.split("/")?.forEach {
            if (it.isNotBlank()){
                when (it) {
                    Plans.FREE_PLAN.name -> {
                        video?.listOfPlans?.add(Plans.FREE_PLAN)
                    }
                    Plans.BASIC_PLAN.name -> {
                        video?.listOfPlans?.add(Plans.BASIC_PLAN)
                    }
                    Plans.ADVANCED_PLAN.name -> {
                        video?.listOfPlans?.add(Plans.ADVANCED_PLAN)
                    }
                }
            }
        }


        Plans.values().forEach {
            if (it != Plans.NO_PLAN) {
                if (video?.listOfPlans?.contains(it) == true){
                    arrayPlans.add(PlanSwitch(it, true))
                }else{
                    arrayPlans.add(PlanSwitch(it, false))
                }

            }
        }
        adapter.planList = arrayPlans

    }

    private fun setupRecyclerPlans() {
        rv_plans?.layoutManager = LinearLayoutManager(applicationContext)
        rv_plans?.adapter = adapter
    }

    private fun registerBroadcastReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(BROADCAST_UPLOAD_DONE)
        intentFilter.addAction(BROADCAST_UPLOAD_PROGRESS)

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadCastReceiver, intentFilter)

    }

    private fun unregisterBroadcastReceiver(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiver)

    }

    private fun setupSpinner() {

        val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, arraySpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        category_spinner.adapter = adapter
    }

    fun uploadDone(){
        toggleButtonUpload()
        showToast( getString(R.string.upload_sucess_title), true)
        val subscribe = io.reactivex.Observable.timer(3, TimeUnit.SECONDS)
                .subscribe { _ ->
                    finish()
                }

    }

    fun setProgress(intent: Intent?){
        var progress = intent?.getIntExtra(Constants.BROADCAST_UPLOAD_PROGRESS, 0) ?: 0
        progress_upload.progress = progress
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            when (intent?.action) {
                BROADCAST_UPLOAD_DONE -> uploadDone()
                BROADCAST_UPLOAD_PROGRESS -> setProgress(intent)
            }
        }
    }

    override fun onResume() {
        registerBroadcastReceiver()
        super.onResume()
    }

    override fun onPause() {
        unregisterBroadcastReceiver()
        super.onPause()
    }

    private fun setupExtras(){
        if (intent != null) {
            videoUrl = intent?.getStringExtra(EXTRA_VIDEO_PATH) ?: ""
            filename = videoUrl.split("/").lastOrNull() ?: ""
            isEdit = intent?.getBooleanExtra(EXTRA_IS_EDIT,false) ?: false
            video = intent?.getParcelableExtra(EXTRA_VIDEO)
        }
    }

    private fun setupUploadButton(){
        if (isEdit){
            btn_upload?.text = getString(R.string.create_theme_edit_option)
        }

        btn_upload.setOnClickListener {


                if (checkFields()) {
                    val title = video_title.text.toString()
                    val description = video_description.text.toString()
                    val category = arraySpinner[category_spinner.selectedItemPosition]
                    var listOfPlans = ""
                    adapter.planList.forEach {
                        if (it.enabled) {
                            if (it.plan != null) {
                                listOfPlans += it.plan!!.name + "/"
                            }
                        }
                    }

                    if (isEdit){
                        val video = Video(0, listOfPlans, this.video?.videoKey ?: return@setOnClickListener, title, description, category, videoUrl, video?.videoThumb)
                        uploadViewModel.editVideo(video)
                    }else {

                        val video = Video(0, listOfPlans, "", title, description, category, videoUrl)
                        toggleButtonUpload()
                        getThumbnailAndUpload(video)
                    }

                }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupDebugInfo(){
        if (BuildConfig.DEBUG && !isEdit){
            video_title.setText("Teste video 1")
            video_description.setText("Esse é um video teste, estamos subindo somente com o intuito de testar, se você está vendo" +
                    "isso então algo deu muito errdo, obrigado")
            category_spinner.setSelection(1)

        }
    }

    private fun toggleButtonUpload(){
        progress_upload.isClickable = !progress_upload.isClickable
    }

    private fun checkFields(): Boolean{
        if (video_title.text.isNullOrBlank() || video_description.text.isNullOrBlank()){

            showToast(getString(R.string.fill_all_fields), true)
            return false
        }
        return true
    }

    private fun setupFields(){

        if (isEdit){
            if (!video?.title.isNullOrBlank()){
                video_title?.setText(video?.title)
            }
            if (!video?.description.isNullOrBlank()){
                video_description?.setText(video?.description)
            }
        }

        video_title.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                var size = s?.length ?: 0
                if ( size <= 50) {
                    tv_title_max_length.text = getString(R.string.video_size_title, size)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        video_description.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                var size = s?.length ?: 0
                if ( size <= 200) {
                    tv_description_max_length.text = getString(R.string.video_size_desc, size)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun setupVideo(videoUri: String){
        preview_video.setVideoURI(Uri.parse(videoUri))
        preview_video.start()
    }

    private fun getThumbnailAndUpload(video: Video){
        val duration = preview_video.duration
        preview_video.seekTo(duration/2) //seek to half of the video
        var file = File(videoUrl)
        val videoThumb = ThumbnailUtils.createVideoThumbnail(file.absolutePath,
                MediaStore.Images.Thumbnails.MINI_KIND)

        video.videoThumb = FileHelper.getRealPathFromURI(this, getImageUri(this, videoThumb) )

        Log.d("teste", "Thumb created!!! " + video.videoThumb)
        uploadViewModel.uploadVideo(this, video)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }




}