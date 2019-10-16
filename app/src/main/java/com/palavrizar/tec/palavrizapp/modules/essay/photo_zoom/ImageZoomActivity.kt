package com.palavrizar.tec.palavrizapp.modules.essay.photo_zoom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ablanco.zoomy.Zoomy
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import kotlinx.android.synthetic.main.activity_image_zoom.*
import java.io.File


class ImageZoomActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)

        setExtras(intent)
    }

    private fun setExtras(intent: Intent?){
        if (intent != null) {
            //iv_full_screen.setImageURI(Uri.intent.getParcelableExtra(Constants.EXTRA_IMAGE_FULL_SCREEN))
            iv_full_screen.setImageURI(Uri.fromFile(File(intent.getStringExtra(Constants.EXTRA_IMAGE_FULL_SCREEN))))
            val builder = Zoomy.Builder(this).target(iv_full_screen)
            builder.register()
        }
    }

}
