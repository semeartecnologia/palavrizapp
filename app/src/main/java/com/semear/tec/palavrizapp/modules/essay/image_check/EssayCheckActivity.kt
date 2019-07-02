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

    var bmpImageEssay: Bitmap? = null
    var essayRepository: EssayRepository? = null
    var sessionManager: SessionManager? = null
    var themesRepository: ThemesRepository? = null

    private var themeHash = hashMapOf<String,String>()

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

    fun setupExtras(){
        if (intent != null) {
            bmpImageEssay = intent?.getParcelableExtra(Constants.EXTRA_IMAGE_CHECK)
            iv_essay_preview.setImageBitmap(bmpImageEssay)
        }
    }

    private fun loadThemes(){
        themeHash.clear()
        themesRepository?.getTheme {
            it.forEach { theme ->
                themeHash[theme.themeName] = theme.themeId
            }
            showLoadingProgress(false)
            showFieldsDialogInserTitleAndTheme(true)

            val listThemes = ArrayList<String>(themeHash.keys)
            val adapter = ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, listThemes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            theme_spinner.adapter = adapter
            adapter.notifyDataSetChanged()
        }


    }

    private fun showLoadingProgress(show: Boolean){
        if (show){
            progress_loading_themes?.visibility = View.VISIBLE
        }else{
            progress_loading_themes?.visibility = View.GONE
        }
    }

    private fun showFieldsDialogInserTitleAndTheme(show: Boolean){
        if (show){
            dialog_title_theme_label?.visibility = View.VISIBLE
            theme_spinner?.visibility = View.VISIBLE
            et_title_essay?.visibility = View.VISIBLE
            btn_send_essay?.visibility = View.VISIBLE
        }else{
            dialog_title_theme_label?.visibility = View.GONE
            theme_spinner?.visibility = View.GONE
            et_title_essay?.visibility = View.GONE
            btn_send_essay?.visibility = View.GONE
        }
    }

    private fun setupView(){
        btn_negative.setOnClickListener {
            /*setResult(RESULT_NEGATIVE)
            finish()*/
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
            dialog_title_essay.visibility = View.VISIBLE
            loadThemes()
            showLoadingProgress(true)
            showFieldsDialogInserTitleAndTheme(false)
        }
        et_title_essay.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                btn_send_essay.isEnabled = s != null && s.isNotBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        btn_send_essay.setOnClickListener {
            btn_send_essay.isEnabled = false
            layout_sendind_progress.visibility = View.VISIBLE
            et_title_essay?.visibility = View.GONE
            val title = et_title_essay?.text.toString()
            val user = sessionManager?.userLogged

            val themeId = themeHash[theme_spinner?.selectedItem.toString().trim()]
            val essay = Essay(title, theme_spinner?.selectedItem.toString(), themeId!!, user, DateFormatHelper.currentTimeDate,StatusEssay.UPLOADED, "")

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
