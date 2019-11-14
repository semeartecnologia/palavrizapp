package com.palavrizar.tec.palavrizapp.modules.essay.photo_zoom

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ablanco.zoomy.Zoomy
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_zoom.*


class ImageZoomActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)

        setExtras(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_zoom, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_girar -> {
                rotateImage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun rotateImage(){
        requestedOrientation = if (resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT){
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }else{
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun setExtras(intent: Intent?){
        if (intent != null) {
            Picasso.get().load(intent.getStringExtra(Constants.EXTRA_IMAGE_FULL_SCREEN)).into(iv_full_screen, object: com.squareup.picasso.Callback {
                override fun onError(e: Exception?) {
                    //DialogHelper.showAlert(activity, "Erro", "Erro ao abrir imagem, contate nossos administradores" )
                }

                override fun onSuccess() {
                    iv_full_screen?.visibility = View.VISIBLE
                    //progress?.visibility = View.GONE
                }
            })
            val builder = Zoomy.Builder(this).target(iv_full_screen)
            builder.register()
        }
    }

}
