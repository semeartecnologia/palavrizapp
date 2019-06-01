package com.semear.tec.palavrizapp.modules.essay

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.essay.image_check.EssayCheckActivity
import com.semear.tec.palavrizapp.utils.adapters.MyEssayAdapter
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_IMAGE_CHECK
import com.semear.tec.palavrizapp.utils.repositories.EssayRepository
import kotlinx.android.synthetic.main.activity_my_essay.*
import kotlinx.android.synthetic.main.layout_no_essay.*

class MyEssayActivity : AppCompatActivity() {

    private var viewmodel: MyEssayViewModel? = null
    private val adapter = MyEssayAdapter()
    private var essayRepository: EssayRepository? = null

    private val REQUEST_CAMERA = 333
    private val REQUEST_IMAGE_CAPTURE = 345
    private val REQUEST_IMAGE_CHECK = 405


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_essay)
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
    }

    private fun setupListeners(){
        btn_first_essay.setOnClickListener {
            checkCameraPermission()
        }
        btn_send_essay.setOnClickListener{
            checkCameraPermission()
        }
    }

    private fun startImageCheckActivity(bmp: Bitmap) {
        val it = Intent(this, EssayCheckActivity::class.java)
        it.putExtra(EXTRA_IMAGE_CHECK, bmp)
        startActivity(it)
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
                layout_my_essays?.visibility = View.GONE
                layout_no_essay?.visibility = View.VISIBLE
            }else{
                layout_my_essays?.visibility = View.VISIBLE
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
