package com.semear.tec.palavrizapp.modules.essay.essay_view_professor

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.utils.Commons
import com.semear.tec.palavrizapp.utils.constants.Constants
import com.semear.tec.palavrizapp.utils.repositories.EssayRepository
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_essay_view.*
import java.lang.Exception

class EssayViewActivity : AppCompatActivity() {

    private lateinit var actualEssay: Essay
    private var essayRepository : EssayRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essay_view)

        initRepositories()
        getExtras(intent)
        setupView()

    }

    private fun initRepositories() {
        essayRepository = EssayRepository(applicationContext)
    }

    private fun setupView() {
        val activity = this
        tv_essay_theme?.text = String.format(getString(R.string.tv_theme_placeholder), actualEssay.theme)
        tv_essay_title?.text = actualEssay.title

        essayRepository?.getEssayImage(actualEssay.url) {
            Picasso.get().load(it).into(image_essay, object: com.squareup.picasso.Callback {
                override fun onError(e: Exception?) {
                    Commons.showAlert(activity, "Erro", "Erro ao abrir imagem, contate nossos administradores" )
                }

                override fun onSuccess() {
                    image_essay?.visibility = View.VISIBLE
                    layout_progress_essay?.visibility = View.GONE
                }
            })

        }

        tv_profile_name_author?.text = actualEssay.author?.fullname


        Picasso.get().load(actualEssay.author?.photoUri).into(tv_profile_image_author)



    }

    private fun getExtras(intent: Intent?){
        if (intent != null) {
            actualEssay = intent.getParcelableExtra(Constants.EXTRA_ESSAY)
        }
    }
}
