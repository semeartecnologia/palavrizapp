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
import android.widget.Toast
import com.semear.tec.palavrizapp.BuildConfig
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.*
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


    private var adapter: PlanListAdapter = PlanListAdapter()
    private var adapterStructure: ArrayAdapter<String>? = null
    private var adapterConcept: ArrayAdapter<String>? = null

    private var conceptListString = arrayListOf<String>()
    private var structureListString = arrayListOf<String>()
    private var themeListString = arrayListOf<String>()

    private var listOfThemes = arrayListOf<Themes>()

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
        setupAddButtonSpinners()
        loadThemes()
        loadStructures()
        loadConcepts()
    }

    private fun setupAddButtonSpinners() {
        btn_add_structure?.setOnClickListener {
            DialogHelper.createStructureDialog(this, false, null,
                    {
                        uploadViewModel.saveStructure(it)
                        adapterStructure?.add(it.structure)
                        spinner_structure?.setSelection(structureListString.size-1)
                    },
                    {}, {})
        }
        btn_add_concept?.setOnClickListener {
            DialogHelper.createConceptDialog(this, false, null,
                    {
                        uploadViewModel.saveConcept(it)
                        adapterConcept?.add(it.concept)
                        spinner_concept?.setSelection(conceptListString.size-1)
                    }
                    , {}, {})
        }
    }

    private fun setupDeleteButton() {
        if (isEdit){
            btn_delete?.visibility = View.VISIBLE
        }else{
            btn_delete?.visibility = View.GONE
        }

        btn_delete?.setOnClickListener {
            DialogHelper.showYesNoMessage(this, "", getString(R.string.delete_video_dialog_text),{
                if (video != null) {
                    uploadViewModel.deleteVideo(video!!)
                }
            },{})
        }
    }

    fun registerObservers(){
        uploadViewModel.conceptsListLiveData.observe(this, Observer {
            if (it != null) {
                val defaultValue = getString(R.string.choose_conceito)
                conceptListString = arrayListOf<String>()
                conceptListString.add(defaultValue)
                it.forEach { concept ->
                    conceptListString.add(concept.concept)
                }

                adapterConcept = ArrayAdapter(this,
                        android.R.layout.simple_spinner_item, conceptListString)
                adapterConcept?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_concept?.adapter = adapterConcept

                if (isEdit){
                    if (!video?.concept.isNullOrBlank()){
                        spinner_concept.setSelection ( conceptListString.indexOf(video?.concept))
                    }
                }
            }
        })
        uploadViewModel.structuresListLiveData.observe(this, Observer {
            if (it != null) {
                val defaultValue = getString(R.string.choose_structure)
                structureListString = arrayListOf<String>()
                structureListString.add(defaultValue)
                it.forEach { structure ->
                    structureListString.add(structure.structure)
                }

                 adapterStructure = ArrayAdapter(this,
                        android.R.layout.simple_spinner_item, structureListString)

                adapterStructure?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_structure?.adapter = adapterStructure

                if (isEdit){
                    if (!video?.structure.isNullOrBlank()){
                         spinner_structure.setSelection ( structureListString.indexOf(video?.structure))
                     }
                }
            }
        })
        uploadViewModel.themeListLiveData.observe(this, Observer {
            if (it != null) {
                listOfThemes = it
                val defaultValue = getString(R.string.choose_theme)
                themeListString = arrayListOf<String>()
                themeListString.add(defaultValue)
                it.forEach { theme ->
                    themeListString.add(theme.themeName)
                }

                val adapter = ArrayAdapter(this,
                        android.R.layout.simple_spinner_item, themeListString)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_theme?.adapter = adapter

                if (!video?.themeName.isNullOrBlank()){
                    spinner_theme.setSelection ( themeListString.indexOf(video?.themeName))
                }
            }
        })
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

    private fun loadThemes(){
        uploadViewModel.getVideoThemeList()
    }

    private fun loadConcepts(){
        uploadViewModel.getVideoConceptList()
    }

    private fun loadStructures(){
        uploadViewModel.getVideoStructureList()
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

        /*val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, arraySpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        category_spinner.adapter = adapter*/
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
            video = intent?.getParcelableExtra(EXTRA_VIDEO)
            isEdit = intent?.getBooleanExtra(EXTRA_IS_EDIT,false) ?: false
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
                //val category = arraySpinner[category_spinner.selectedItemPosition]


                var listOfPlans = ""
                adapter.planList.forEach {
                    if (it.isChecked) {
                        if (it.plan != null) {
                            listOfPlans += it.plan!!.name + "/"
                        }
                    }
                }

                if (title.isBlank() || spinner_concept?.selectedItemPosition == 0 || spinner_structure?.selectedItemPosition == 0 || spinner_theme?.selectedItemPosition == 0) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val structure = spinner_structure?.selectedItem.toString()
                val concept = spinner_concept?.selectedItem.toString()
                Log.d("aki", "$structure - $concept")
                val themeName = spinner_theme?.selectedItem.toString()

                val selectedTheme = listOfThemes.filter { it.themeName == themeName }.single()

                btn_upload.isEnabled = false
                video_title?.isEnabled = false
                video_description?.isEnabled = false
                adapter.disableAllCheckboxes()

                if (isEdit){
                    val video = Video("0", listOfPlans, this.video?.videoKey ?: return@setOnClickListener, title, description, "", videoUrl, video?.videoThumb, selectedTheme.urlPdf, selectedTheme.themeName, concept, structure )
                    uploadViewModel.editVideo(video)
                }else {

                    val video = Video("0", listOfPlans, "", title, description, "", videoUrl, null, selectedTheme.urlPdf, selectedTheme.themeName, concept, structure)
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