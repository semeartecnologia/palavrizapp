package com.semear.tec.palavrizapp.modules.essay.image_check

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.StatusEssay
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.interfaces.EssayUploadCallback
import com.semear.tec.palavrizapp.utils.repositories.EssayRepository
import com.semear.tec.palavrizapp.utils.repositories.SessionManager
import com.semear.tec.palavrizapp.utils.repositories.ThemesRepository
import kotlinx.android.synthetic.main.activity_check_image.*
import java.util.concurrent.TimeUnit
import android.widget.ArrayAdapter
import com.semear.tec.palavrizapp.utils.commons.DateFormatHelper
import com.semear.tec.palavrizapp.utils.commons.DialogHelper


class EssayCheckActivity : AppCompatActivity() {

    private var bmpImageEssay: Bitmap? = null
    private var essayRepository: EssayRepository? = null
    var sessionManager: SessionManager? = null
    private var themesRepository: ThemesRepository? = null

    private var themeId: String? = ""
    var themeName: String? = ""

    private val RESULT_NEGATIVE = 404

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_check_image)
        initRepository()
        setupExtras()
        setupView()
    }

    private fun initRepository() {
        essayRepository = EssayRepository(applicationContext)
        sessionManager = SessionManager(applicationContext)
        themesRepository = ThemesRepository(applicationContext)
    }

    private fun setupExtras(){
        if (intent != null) {
            bmpImageEssay = intent?.getParcelableExtra(Constants.EXTRA_IMAGE_CHECK)
            themeId = intent?.getStringExtra(Constants.EXTRA_ESSAY_THEME_ID)
            themeName = intent?.getStringExtra(Constants.EXTRA_ESSAY_THEME)
            iv_essay_preview.setImageBitmap(bmpImageEssay)
        }
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
            val user = sessionManager?.userLogged

            val essay = Essay("", themeName ?: return@setOnClickListener, themeId ?: return@setOnClickListener, user, DateFormatHelper.currentTimeDate,StatusEssay.UPLOADED, "")
            layout_sendind_progress.visibility = View.VISIBLE
            essayRepository?.saveEssay(essay, user?.userId ?: "", bmpImageEssay, object: EssayUploadCallback{

                @SuppressLint("CheckResult")
                override fun onSuccess() {
                    layout_sendind_progress.visibility = View.GONE
                    DialogHelper.showAlert(this@EssayCheckActivity, getString(R.string.upload_sucess_title), getString(R.string.upload_essay_success), "Ok")
                    io.reactivex.Observable.timer(3, TimeUnit.SECONDS)
                            .subscribe { _ ->
                                finish()
                            }
                }

                override fun onFail() {
                    layout_sendind_progress.visibility = View.GONE
                    DialogHelper.showAlert(this@EssayCheckActivity, getString(R.string.upload_essay_title), getString(R.string.upload_essay_error), "Ok")
                }

                override fun onProgress(progress: Int) {
                }

            })
        }

    }
}
