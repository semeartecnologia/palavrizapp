package com.semear.tec.palavrizapp.modules.essay.image_check

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.StatusEssay
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.repositories.EssayRepository
import com.semear.tec.palavrizapp.utils.repositories.SessionManager
import kotlinx.android.synthetic.main.activity_check_image.*


class EssayCheckActivity : AppCompatActivity() {

    var bmpImageEssay: Bitmap? = null
    var mAttacher: PhotoViewAttacher? = null
    var essayRepository: EssayRepository? = null
    var sessionManager: SessionManager? = null

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
    }

    fun setupExtras(){
        if (intent != null) {
            bmpImageEssay = intent?.getParcelableExtra(Constants.EXTRA_IMAGE_CHECK)
            iv_essay_preview.setImageBitmap(bmpImageEssay)
            mAttacher = PhotoViewAttacher(iv_essay_preview)
        }
    }

    private fun setupView(){
        btn_negative.setOnClickListener {
            setResult(RESULT_NEGATIVE)
            finish()
        }
        btn_positive.setOnClickListener {
            dialog_is_readable.visibility = View.GONE
            dialog_title_essay.visibility = View.VISIBLE
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
            layout_sendind_progress.visibility = View.VISIBLE
            val title = et_title_essay?.text.toString()
            val essay = Essay(title, StatusEssay.UPLOADED, "")
            val user = sessionManager?.userLogged
            essayRepository?.saveEssay(essay, user?.userId ?: "", bmpImageEssay)
        }
    }
}
