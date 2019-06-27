package com.semear.tec.palavrizapp.modules.admin.themes

import android.app.Activity
import android.app.DownloadManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Themes
import com.semear.tec.palavrizapp.utils.adapters.ThemesListAdapter
import com.semear.tec.palavrizapp.utils.commons.DialogHelper
import com.semear.tec.palavrizapp.utils.commons.FileHelper
import com.semear.tec.palavrizapp.utils.constants.Helper
import com.semear.tec.palavrizapp.utils.interfaces.OnThemeClicked
import kotlinx.android.synthetic.main.list_themes_fragment.*


class ListThemesFragment : Fragment(), OnThemeClicked {


    private lateinit var adapter: ThemesListAdapter
    private var editedTheme: Themes? = null



    companion object {
        fun newInstance() = ListThemesFragment()
    }

    private lateinit var viewModel: ListThemesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_themes_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListThemesViewModel::class.java)
        adapter = ThemesListAdapter(this)
        setupRecyclerThemes()
        registerObservers()
        viewModel.fetchThemes()
        setupView()
    }

    private fun setupView() {
        fab_add_theme?.setOnClickListener {
            showCreateThemeDialog()
        }
    }

    private fun showCreateThemeDialog(theme: Themes? = null) {
        DialogHelper.createThemeDialog(activity as Activity
                ,false,
                theme,
                {
                    editedTheme = it
                    callPdfPicker(false)
                },
                { newTheme ->
                    viewModel.saveTheme(newTheme){

                    }

                },{

        },{

        })
    }

    private fun callPdfPicker(isEdit: Boolean){
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = "application/pdf"
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        if (isEdit){
            startActivityForResult(Intent.createChooser(intentPDF, "Select PDF"), 232)
        }else {
            startActivityForResult(Intent.createChooser(intentPDF, "Select PDF"), 231)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 231) {
                if (data != null) {
                    editedTheme?.urlPdf = FileHelper.getPathPdf(activity as Activity, data.data!!)
                    showCreateThemeDialog(editedTheme)
                }
            }else if(requestCode == 232){
                if (data != null) {
                    editedTheme?.urlPdf = FileHelper.getPathPdf(activity as Activity, data.data!!)
                    showEditThemeDialog(editedTheme, true)
                }
            }
        }
    }

    private fun setupRecyclerThemes() {
        rv_themes?.layoutManager = LinearLayoutManager(context)
        rv_themes?.adapter = adapter
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            when (intent?.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    val a ="acabou o dauload"
                }
            }
        }
    }

    private fun registerBroadcastReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)


            LocalBroadcastManager.getInstance(activity as Activity)
                    .registerReceiver(broadCastReceiver, intentFilter)


    }

    private fun unregisterBroadcastReceiver() {

            LocalBroadcastManager.getInstance(activity as Activity).unregisterReceiver(broadCastReceiver)


    }

    override fun onPause() {
        unregisterBroadcastReceiver()
        super.onPause()
    }

    override fun onResume() {
        registerBroadcastReceiver()
        super.onResume()
    }

    private fun registerObservers() {
        viewModel.filePathLiveData.observe(this, Observer {
            if (!it.isNullOrBlank()){
                val a = ""
                // Commons.openPdf(activity as Activity, File(context?.filesDir,it))
            }
        })
        viewModel.listThemes.observe(this, Observer {
            if (it != null) {
                progress_loading_themes?.visibility = View.GONE
                adapter.themesList = it
            }
        })
    }

    override fun onThemeClicked(theme: Themes) {
        showEditThemeDialog(theme)
    }

    override fun onDownloadPdfClicked(uri: String) {
        viewModel.downloadPdf(uri)
    }

    private fun showEditThemeDialog(theme: Themes? = null, newDocument: Boolean = false){
        DialogHelper.createThemeDialog(activity as Activity
                , true,
                theme,
                {
                    editedTheme = it
                    callPdfPicker(true)
                },
                { newTheme ->
                    viewModel.editTheme(newDocument, newTheme){

                    }

                },{

        }, {
            DialogHelper.showYesNoMessage(activity as Activity, getString(R.string.dialog_delete_theme_title), getString(R.string.dialog_delete_theme_text)){
                viewModel.deleteTheme(theme?.themeId ?: "")
            }
        })
    }


}
