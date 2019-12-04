package com.palavrizar.tec.palavrizapp.modules.upload

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import com.palavrizar.tec.palavrizapp.BuildConfig
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.*
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.modules.classroom.video_view.VideoFragment
import com.palavrizar.tec.palavrizapp.utils.adapters.PlanListAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.FileHelper
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.BROADCAST_UPLOAD_DONE
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.BROADCAST_UPLOAD_PROGRESS
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_IS_EDIT
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO_PATH
import kotlinx.android.synthetic.main.fragment_add_video.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit


class UploadActivity : BaseActivity() {

    private var videoUrl = ""
    private var filename = ""

    private var video: Video? = null
    private var isEdit = false
    private lateinit var uploadViewModel: UploadViewModel


    private var adapter: PlanListAdapter = PlanListAdapter()
    private var themeListString = arrayListOf<String>()

    private var listOfThemes = arrayListOf<Themes>()
    private var videoPdfPath: String? = ""

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
        loadThemes()
        loadStructures()
        loadConcepts()
        setupRadioGroup()
        setupAddPdfButton()
    }



    private fun setupRadioGroup() {
        radioGroupVideoInfo.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_theme -> changeRadioItem(0)
                R.id.radio_concept -> changeRadioItem(1)
                R.id.radio_structure ->  changeRadioItem(2)
                else -> {
                }
            }
        }
    }
    private fun setupAddPdfButton() {
        btn_add_pdf?.setOnClickListener {
            callPdfPicker(false)
        }

        layout_pdf_filename?.setOnClickListener {
            videoPdfPath = ""
            layout_pdf_filename?.visibility = View.GONE
        }

    }

    private fun changeRadioItem(i: Int){
        if ( i == 0){
            spinner_theme?.visibility = View.VISIBLE
            layout_add_pdf?.visibility = View.GONE
        }else if (i == 1){
            spinner_theme?.visibility = View.GONE
            layout_add_pdf?.visibility = View.VISIBLE
        }else if( i == 2){
            spinner_theme?.visibility = View.GONE
            layout_add_pdf?.visibility = View.VISIBLE
        }

    }
    private fun callPdfPicker(isEdit: Boolean){
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = "application/pdf"
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        if (isEdit){
            startActivityForResult(Intent.createChooser(intentPDF, "Select PDF"), 232)
        }else {
            startActivityForResult(Intent.createChooser(intentPDF, "Select PDF"), 231)
        }
    }

    private fun saveFileInStorage(uri: Uri,  onCompletion: (String) -> Unit) {

        Thread(Runnable {
            var file: File? = null
            try {
                val mimeType = contentResolver.getType(uri)
                if (mimeType != null) {
                    val inputStream = contentResolver.openInputStream(uri)

                    val fileName = getFileName(uri)
                    if (fileName != "") {
                        file = File(
                                getExternalFilesDir(
                                        Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + "/" + fileName)
                        val output = FileOutputStream(file)
                        try {
                            val buffer = ByteArray(inputStream!!.available()) // or other buffer size
                            var read = inputStream.read(buffer)

                            while (read != -1) {
                                output.write(buffer, 0, read)
                                read = inputStream.read(buffer)
                            }

                            output.flush()
                            val path = file.absolutePath//use this path
                            onCompletion(path)

                        } finally {
                            output.close()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()

    }

    private fun getFileName(uri: Uri): String {
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        var displayName = ""
        var cursor: Cursor? = null

        cursor = this.contentResolver
                .query(uri, null, null, null, null, null)
        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                //Log.i(TAG, "Display Name: $displayName")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return displayName
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 231) {
                if (data != null) {
                    saveFileInStorage(data.data!!){
                        val urlPdf = it
                        runOnUiThread {
                            layout_pdf_filename?.visibility = View.VISIBLE
                            layout_pdf_filename?.text = urlPdf.split("/")?.lastOrNull() ?: "Excluir PDF"
                            videoPdfPath = it
                        }
                    }
                }
            }else if(requestCode == 232){
                if (data != null) {
                    saveFileInStorage(data.data!!){

                    }
                }
            }
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
    private fun setupVideoFragment(videoPath: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_video, VideoFragment.newInstance(videoPath, video?.videoKey)).commit()
    }

    fun registerObservers(){
        uploadViewModel.videoDownloadUrl?.observe(this, Observer {
            if (it != null) {
                setupVideoFragment(it)
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
                video?.listOfPlans?.add(it)
            }
        }

        uploadViewModel.getSinglePlans {
            plansBillingList ->
            if (video?.listOfPlans?.contains(Constants.PLAN_FREE_ID) == true){
                arrayPlans.add(PlanSwitch(Constants.PLAN_FREE_ID, true))
            }else{
                arrayPlans.add(PlanSwitch(Constants.PLAN_FREE_ID, false))
            }
            plansBillingList.forEach {
                if (video?.listOfPlans?.contains(it.plan_id) == true){
                    arrayPlans.add(PlanSwitch(it.plan_id, true))
                }else{
                    arrayPlans.add(PlanSwitch(it.plan_id, false))
                }
            }

            adapter.planList = arrayPlans
        }


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
                            listOfPlans += it.plan + "/"
                        }
                    }
                }

                if (title.isBlank()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                var structure: String? = null
                if (radio_structure.isChecked){
                    structure = "structure"
                }

                var concept: String? = null
                if (radio_concept.isChecked){
                    concept = "concept"
                }

                var themeName: String? = null
                var selectedTheme: Themes? = null
                if (radio_theme.isChecked){
                  themeName = spinner_theme?.selectedItem.toString()
                    if (listOfThemes.isNotEmpty()) {
                        selectedTheme = listOfThemes.filter { it.themeName == themeName }.singleOrNull()
                    }
                    themeName = "themes"
                }

                btn_upload.isEnabled = false
                video_title?.isEnabled = false
                video_description?.isEnabled = false
                adapter.disableAllCheckboxes()

                if (isEdit){
                    val video = Video(this.video?.orderVideo ?: "0", listOfPlans, this.video?.videoKey ?: return@setOnClickListener, title, description, "", videoUrl, video?.videoThumb ?: "", selectedTheme?.urlPdf ?: videoPdfPath ?: "", themeName, concept, structure )
                    if (check_intro?.isChecked == true){
                        uploadViewModel.getVideoIntroUploadedAlready {
                            if (it){
                                DialogHelper.showYesNoMessage(this, "", getString(R.string.video_intro_already_there),
                                        {
                                            uploadViewModel.deleteVideo(video, keepStorage = true)
                                            uploadViewModel.editVideoToIntro(video)
                                        },{
                                    finish()
                                })
                            }else{
                                uploadViewModel.deleteVideo(video, keepStorage = true)
                                uploadViewModel.editVideoToIntro(video)
                            }
                        }
                    }else {
                        uploadViewModel.editVideo(video)
                    }
                }else {

                    val video = Video("0", listOfPlans, "", title, description, "", videoUrl, null, selectedTheme?.urlPdf ?: videoPdfPath ?: "", selectedTheme?.themeName, concept, structure)
                    toggleButtonUpload()

                    if (check_intro?.isChecked == true){
                        uploadViewModel.getVideoIntroUploadedAlready {
                            if (it){
                                DialogHelper.showYesNoMessage(this, "", getString(R.string.video_intro_already_there),
                                        {
                                            getThumbnailAndUpload(video, true)
                                        },{
                                    finish()
                                })
                            }else{
                                getThumbnailAndUpload(video, true)
                            }
                        }
                    }else{
                        toggleButtonUpload()
                        getThumbnailAndUpload(video)
                    }
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

            if (!video?.pdfPath.isNullOrBlank()){
                layout_add_pdf?.visibility = View.VISIBLE
                layout_pdf_filename?.visibility = View.VISIBLE
                layout_pdf_filename?.text = video?.pdfPath?.split("/")?.lastOrNull() ?: "Excluir PDF"
            }

            spinner_theme?.visibility = View.GONE
            if (!video?.themeName.isNullOrBlank()){
                spinner_theme?.visibility = View.VISIBLE
                radio_theme.isChecked = true
            }else if (!video?.structure.isNullOrBlank()){
                radio_structure.isChecked = true
                layout_add_pdf?.visibility = View.VISIBLE
            }else if (!video?.concept.isNullOrBlank()){
                radio_concept.isChecked = true
                layout_add_pdf?.visibility = View.VISIBLE
            }

        }else{
            radio_theme.isChecked = true
            spinner_theme?.visibility = View.VISIBLE
        }

        video_title.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                var size = s?.length ?: 0
                if ( size <= 100) {
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
        if (!videoUri.startsWith("/v0/b/palavrizar")){
            var mediaController = MediaController(this)
            mediaController.setAnchorView(frame_video_view)
            preview_video.setMediaController(mediaController)
            preview_video.setVideoURI(Uri.parse(videoUri))
            preview_video.start()
        }else{
            preview_video?.visibility = View.GONE
            frame_video?.visibility = View.VISIBLE
            uploadViewModel.getVideoUrlDownload(video?.path ?: videoUri)
        }

    }

    private fun getThumbnailAndUpload(video: Video, isIntro: Boolean = false){
        var file = File(videoUrl)
        val videoThumb = ThumbnailUtils.createVideoThumbnail(file.absolutePath,
                MediaStore.Images.Thumbnails.MINI_KIND)

        video.videoThumb = FileHelper.getRealPathFromURI(this, getImageUri(this, videoThumb) )

        uploadViewModel.uploadVideo(this, video, isIntro)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }




}