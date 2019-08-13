package com.palavrizar.tec.palavrizapp.modules.admin.themes

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.Themes
import com.palavrizar.tec.palavrizapp.utils.adapters.ThemesListAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.FileHelper
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnThemeClicked
import kotlinx.android.synthetic.main.list_themes_fragment.*
import java.io.File
import android.provider.OpenableColumns
import android.database.Cursor
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.support.v4.runOnUiThread
import java.io.FileOutputStream


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

    private fun saveFileInStorage(uri: Uri,  onCompletion: (String) -> Unit) {

        Thread(Runnable {
            var file: File? = null
            try {
                val mimeType = activity!!.contentResolver.getType(uri)
                if (mimeType != null) {
                    val inputStream = activity!!.contentResolver.openInputStream(uri)

                    val fileName = getFileName(uri)
                    if (fileName != "") {
                        file = File(
                                context!!.getExternalFilesDir(
                                        Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + "/" + fileName)
                        val output = FileOutputStream(file)
                        try {
                            val buffer = ByteArray(inputStream!!.available()) // or other buffer size
                            var read = inputStream.read(buffer)

                            while (read != -1) {
                                output.write(buffer, 0, read)
                                read = inputStream.read(buffer)
                            }

                            output.flush()
                            val path = file.absolutePath//use this path
                            onCompletion(path)

                        } finally {
                            output.close()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()

    }

    private fun getFileName(uri: Uri): String {
        var displayName = ""
        var cursor: Cursor? = null
        if (activity != null)
            cursor = activity!!.contentResolver
                    .query(uri, null, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {

                displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return displayName
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 231) {
                if (data != null) {
                    saveFileInStorage(data.data!!){
                        editedTheme?.urlPdf = it
                        runOnUiThread {
                            showCreateThemeDialog(editedTheme)
                        }

                    }
                }
            }else if(requestCode == 232){
                if (data != null) {
                    saveFileInStorage(data.data!!){
                        editedTheme?.urlPdf = it
                        runOnUiThread {
                            showEditThemeDialog(editedTheme, true)
                        }
                    }
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
                val fhirPath = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val file = File(fhirPath, "$it.pdf")
                FileHelper.openPdf(context ?: return@Observer, file)
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
        requestWriteStoragePermission(uri)
    }

    fun requestWriteStoragePermission(uri: String) {
        if (ContextCompat.checkSelfPermission(activity as Activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    224)

        } else {
            viewModel.downloadPdf(uri)
        }
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
            DialogHelper.showYesNoMessage(activity as Activity, getString(R.string.dialog_delete_theme_title), getString(R.string.dialog_delete_theme_text),{
                viewModel.deleteTheme(theme?.themeId ?: "")
            },{})
        })
    }


}
