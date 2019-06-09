package com.semear.tec.palavrizapp.modules.admin.themes

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Themes
import com.semear.tec.palavrizapp.utils.Commons
import com.semear.tec.palavrizapp.utils.adapters.ThemesListAdapter
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
        Commons.createThemeDialog(activity as Activity
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

        })
    }

    private fun callPdfPicker(isEdit: Boolean){
        val intentPDF = Intent(Intent.ACTION_GET_CONTENT)
        intentPDF.type = "application/pdf"
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE)
        if (isEdit){
            startActivityForResult(Intent.createChooser(intentPDF, "Select Picture"), 232)
        }else {
            startActivityForResult(Intent.createChooser(intentPDF, "Select Picture"), 231)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 231) {
                if (data != null) {
                    editedTheme?.urlPdf = Helper.getPathFromUri(activity as Activity, data.data!!)
                    showCreateThemeDialog(editedTheme)
                }
            }else if(requestCode == 232){
                if (data != null) {
                    editedTheme?.urlPdf = Helper.getPathFromUri(activity as Activity, data.data!!)
                    showEditThemeDialog(editedTheme, true)
                }
            }
        }
    }

    private fun setupRecyclerThemes() {
        rv_themes?.layoutManager = LinearLayoutManager(context)
        rv_themes?.adapter = adapter
    }

    private fun registerObservers() {
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

    private fun showEditThemeDialog(theme: Themes? = null, newDocument: Boolean = false){
        Commons.createThemeDialog(activity as Activity
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

        })
    }


}
