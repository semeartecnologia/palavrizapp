package com.palavrizar.tec.palavrizapp.modules.essay

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.modules.essay.image_check.EssayCheckActivity
import com.palavrizar.tec.palavrizapp.utils.adapters.MyEssayAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.FileHelper
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_ESSAY_THEME
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_ESSAY_THEME_ID
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_IMAGE_CHECK
import com.palavrizar.tec.palavrizapp.utils.repositories.EssayRepository
import kotlinx.android.synthetic.main.activity_my_essay.*
import kotlinx.android.synthetic.main.layout_no_essay.*
import java.io.File

class MyEssayActivity : BaseActivity() {

    private var viewmodel: MyEssayViewModel? = null
    private val adapter = MyEssayAdapter()
    private var essayRepository: EssayRepository? = null

    private var themeSelected: Themes? = null

    private val REQUEST_CAMERA = 333
    private val REQUEST_IMAGE_CAPTURE = 345
    private val REQUEST_IMAGE_CHECK = 405


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_essay)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title)

        initViewModel()
        initRepositories()
        setupView()
    }

    private fun initViewModel(){
        viewmodel = ViewModelProviders.of(this).get(MyEssayViewModel::class.java)
    }

    private fun initRepositories(){
        essayRepository = EssayRepository(applicationContext)
    }

    private fun setupView(){
        setupRecyclerView()
        checkUserHasEssay()
        setupListeners()
        registerObservers()
    }

    private fun setupListeners(){
        btn_send_essay.setOnClickListener{
            viewmodel?.sendEssayClicked()
        }
    }

    private fun requestWriteStoragePermission(uri: String) {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    224)

        } else {
            viewmodel?.downloadPdf(uri) { filename ->
                val fhirPath = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val file = File(fhirPath, "$filename.pdf")
                FileHelper.openPdf(this, file)
            }
        }
    }

    private fun registerObservers(){
        viewmodel?.userHasCreditLiveData?.observe(this, Observer {
            if ( it == false ){
                showNoCreditsDialog()
            }else{
                checkCameraPermission()
            }
        })
        viewmodel?.userNoPlanLiveData?.observe(this, Observer {
            if (it == true){
                showDialogNoPlan()
            }
        })
    }

    private fun showDialogNoPlan(){
        DialogHelper.showMessage(this, "", getString(R.string.no_plan_no_essay))
    }



    private fun showNoCreditsDialog(){
        DialogHelper.showMessage(this, "", getString(R.string.no_credits_essay))
    }

    private fun startImageCheckActivity(bmp: Bitmap) {
        val it = Intent(this, EssayCheckActivity::class.java)
        it.putExtra(EXTRA_IMAGE_CHECK, bmp)
        startActivityForResult(it, REQUEST_IMAGE_CHECK)
    }


    private fun setupRecyclerView() {
        rv_my_essays.layoutManager = LinearLayoutManager(this)
        rv_my_essays.adapter = adapter
    }

    private fun checkUserHasEssay() {
        progress_bar_loading?.visibility = View.VISIBLE
        essayRepository?.getEssayListByUser {
            rv_my_essays.visibility = View.VISIBLE
            progress_bar_loading?.visibility = View.GONE
            if (it.isEmpty()){
                rv_my_essays?.visibility = View.GONE
                layout_no_essay?.visibility = View.VISIBLE
            }else{
                rv_my_essays?.visibility = View.VISIBLE
                layout_no_essay?.visibility = View.GONE
                adapter.essayList = it
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA)

        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            this.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (data != null && data.extras != null) {
                    val photo = data.extras!!.get("data") as Bitmap
                    startImageCheckActivity(photo)
                }
            }
        } else if (resultCode == Constants.RESULT_NEGATIVE) {
            if (requestCode == REQUEST_IMAGE_CHECK) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
}
