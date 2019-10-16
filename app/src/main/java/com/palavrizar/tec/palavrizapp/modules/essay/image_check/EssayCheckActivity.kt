package com.palavrizar.tec.palavrizapp.modules.essay.image_check

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Essay
import com.palavrizar.tec.palavrizapp.models.StatusEssay
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.utils.adapters.MyEssayAdapter
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.palavrizar.tec.palavrizapp.utils.interfaces.EssayUploadCallback
import com.palavrizar.tec.palavrizapp.utils.repositories.EssayRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository
import kotlinx.android.synthetic.main.activity_check_image.*
import java.util.concurrent.TimeUnit
import com.palavrizar.tec.palavrizapp.utils.commons.DateFormatHelper
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository
import java.io.File


class EssayCheckActivity : AppCompatActivity() {

    private var bmpImageEssayPath: String? = null
    private var essayRepository: EssayRepository? = null
    var sessionManager: SessionManager? = null
    private var themesRepository: ThemesRepository? = null
    private var userRepository: UserRepository? = null
    private val adapter = MyEssayAdapter()
    private var viewmodel: EssayCheckViewModel? = null


    private val RESULT_NEGATIVE = 404

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_check_image)
        initViewModel()
        registerObservers()
        initRepository()
        setupExtras()
        setupView()
    }

    private fun initRepository() {
        essayRepository = EssayRepository(applicationContext)
        sessionManager = SessionManager(applicationContext)
        themesRepository = ThemesRepository(applicationContext)
        userRepository = UserRepository(applicationContext)
    }

    private fun setupExtras(){
        if (intent != null) {
            bmpImageEssayPath = intent?.getStringExtra(Constants.EXTRA_IMAGE_CHECK)
            iv_essay_preview.setImageURI(Uri.fromFile(File(bmpImageEssayPath)))
        }
    }

    private fun showDialogThemes(listOfThemes: ArrayList<Themes>){
        DialogHelper.createThemePickerDialog(this, listOfThemes,
                {
                    //theme picked
                    val alreadySent = adapter.essayList.any { essay -> essay.themeId == it.themeId }

                    if (alreadySent) {
                        DialogHelper.showYesNoMessage(this, "", getString(R.string.dialog_essay_already_sent_text), {
                            sendEssay(it)
                        }, {

                        })
                    } else {
                        sendEssay(it)
                    }
                }, { url ->
        })
    }

    private fun initViewModel(){
        viewmodel = ViewModelProviders.of(this).get(EssayCheckViewModel::class.java)
    }

    private fun registerObservers(){
        viewmodel?.dialogThemesLiveData?.observe(this, Observer {
            if (it != null){
                showDialogThemes(it)
            }
        })
    }

    private fun sendEssay(theme: Themes){
        val user = sessionManager?.userLogged

        val essay = Essay("", theme.themeName, theme.themeId, user, DateFormatHelper.currentTimeDate,StatusEssay.UPLOADED, "")
        layout_sendind_progress.visibility = View.VISIBLE
        val bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(File(bmpImageEssayPath)))
        essay.url = bmpImageEssayPath ?: ""
        essayRepository?.saveEssay(essay, user?.userId ?: "", bmp, object: EssayUploadCallback{

            @SuppressLint("CheckResult")
            override fun onSuccess() {
                userRepository?.consumeOneCreditIfPossible(sessionManager?.userLogged?.userId ?: return, {
                    layout_sendind_progress.visibility = View.GONE
                    val subscription = io.reactivex.Observable.timer(3, TimeUnit.SECONDS)
                            .subscribe { _ ->
                                finish()
                            }
                    DialogHelper.showOkMessage(this@EssayCheckActivity, getString(R.string.upload_sucess_title), getString(R.string.upload_essay_success),{
                        subscription.dispose()
                        finish()
                    })

                }, {
                    layout_sendind_progress.visibility = View.GONE
                    DialogHelper.showMessage(this@EssayCheckActivity, getString(R.string.upload_essay_title), getString(R.string.upload_essay_error_2))
                })

            }

            override fun onFail() {
                layout_sendind_progress.visibility = View.GONE
                DialogHelper.showMessage(this@EssayCheckActivity, getString(R.string.upload_essay_title), getString(R.string.upload_essay_error))
            }

            override fun onProgress(progress: Int) {
            }

        })
    }


    private fun setupView(){
        btn_negative.setOnClickListener {
            DialogHelper.showYesNoMessage(this, "", getString(R.string.dialog_try_again_text),{
                setResult(RESULT_NEGATIVE)
                finish()
            },{
                finish()
            }
            )
        }

        btn_positive.setOnClickListener {
            dialog_is_readable.visibility = View.GONE

            viewmodel?.fetchThemes()



        }

    }
}
