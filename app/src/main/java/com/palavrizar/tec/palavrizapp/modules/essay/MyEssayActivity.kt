package com.palavrizar.tec.palavrizapp.modules.essay

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.modules.essay.image_check.EssayCheckActivity
import com.palavrizar.tec.palavrizapp.utils.adapters.MyEssayAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.FilePath
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_ESSAY_RETRY
import com.palavrizar.tec.palavrizapp.utils.constants.Constants.EXTRA_IMAGE_CHECK
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnUnreadableClicked
import com.palavrizar.tec.palavrizapp.utils.repositories.EssayRepository
import kotlinx.android.synthetic.main.activity_my_essay.*
import kotlinx.android.synthetic.main.layout_no_essay.*


class MyEssayActivity : BaseActivity(), OnUnreadableClicked {


    private var viewmodel: MyEssayViewModel? = null
    private val adapter = MyEssayAdapter(this)
    private var essayRepository: EssayRepository? = null
    private var imageUri: Uri? = null

    private val REQUEST_CAMERA = 333
    private val REQUEST_IMAGE_CAPTURE = 345
    private val REQUEST_IMAGE_CHECK = 405
    private val REQUEST_WRITE_STORAGE = 224

    private var isRetryEssay: Boolean = false
    private var essayRetry: Essay? = null


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
        viewmodel?.getUserFirebase()
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
        btn_get_plan?.setOnClickListener {
            goToStoreScreen()
        }
        btn_enviar_redacao?.setOnClickListener {
            viewmodel?.sendEssayClicked()
        }
    }

    private fun requestWriteStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE)

        } else {
            startCamera()
        }
    }

    private fun setupViewUser(user: User){
        if (user.essaySoloCredits > 0 || user.plan != "plano_gratis"){
            btn_get_plan?.visibility = View.GONE
            text_get_plan?.visibility = View.GONE
            btn_enviar_redacao?.visibility = View.VISIBLE
        }else{
            btn_get_plan?.visibility = View.VISIBLE
            text_get_plan?.visibility = View.VISIBLE
        }
    }

    private fun registerObservers(){
        viewmodel?.currentUserLivedata?.observe(this, Observer {
            if (it != null){
                setupViewUser(it)
            }
        })
        viewmodel?.userHasCreditLiveData?.observe(this, Observer {
            if ( it == false ){
                showNoCreditsDialog()
            }else{
                isRetryEssay = false
                essayRetry = null
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
        DialogHelper.showGoToStoreMessage(this, "", getString(R.string.no_plan_no_essay), {}, {
            goToStoreScreen()
        })
    }

    private fun goToStoreScreen(){
        val it = Intent()
        setResult(Activity.RESULT_OK, it)
        finish()
    }



    private fun showNoCreditsDialog(){
        DialogHelper.showMessage(this, "", getString(R.string.no_credits_essay))
    }

    private fun startImageCheckActivity(path: String) {
        val it = Intent(this, EssayCheckActivity::class.java)
        it.putExtra(EXTRA_IMAGE_CHECK, path)
        if (essayRetry != null){
            it.putExtra(EXTRA_ESSAY_RETRY, essayRetry)
        }
        startActivityForResult(it, REQUEST_IMAGE_CHECK)
    }

    override fun onUnreadableClicked(essay: Essay) {
        DialogHelper.showYesNoMessage(this, "", getString(R.string.essay_not_readable_message), {
            isRetryEssay = true
            essayRetry = essay
            checkCameraPermission()
        }, {

        })
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
                btn_send_essay?.visibility = View.GONE
            }else{
                btn_send_essay?.visibility = View.VISIBLE
                rv_my_essays?.visibility = View.VISIBLE
                layout_no_essay?.visibility = View.GONE
                adapter.essayList = it
            }
        }
    }

    private fun checkCameraPermission(isRetry: Boolean = false, essay: Essay? = null) {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA)

        } else {
            requestWriteStoragePermission()
        }
    }

    private fun startCamera(){
        var values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Foto da Redação")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto para envio Palavrizar")
        imageUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestWriteStoragePermission()
            }
        }else if (requestCode == REQUEST_WRITE_STORAGE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    val thumbnail = MediaStore.Images.Media.getBitmap(
                            contentResolver, imageUri);
                    if (imageUri != null) {
                        val imageUrl = FilePath.getRealPathFromURI(this, imageUri!!)
                        startImageCheckActivity(imageUrl ?: return)
                    }
            }
        } else if (resultCode == Constants.RESULT_NEGATIVE) {
            if (requestCode == REQUEST_IMAGE_CHECK) {
                startCamera()
            }
        }
    }
}
