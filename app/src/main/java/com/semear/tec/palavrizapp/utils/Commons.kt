package com.semear.tec.palavrizapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.TextView
import com.semear.tec.palavrizapp.R
import kotlinx.android.synthetic.main.dialog_create_theme.view.*
import java.text.SimpleDateFormat
import java.util.*


object Commons {

    val currentTimeDate: String
        @SuppressLint("SimpleDateFormat")
        get() = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().time)

    fun showAlert(activity: Activity, titleAlert: String, textAlert: String, btnAlert: String) {

        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(activity)
        }
        builder.setTitle(titleAlert)
                .setMessage(textAlert)
                .setPositiveButton(btnAlert) { dialog, which -> dialog.dismiss() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    fun createDialogProgress(activity: Activity, message: String, cancellable: Boolean, cancelCallback: (() -> Unit)? = null): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_progress, null, false)
        val progressTextView = view.findViewById<TextView>(R.id.progressTextView)

        progressTextView.text = message
        val progressDialog  = AlertDialog.Builder(activity)
                .setCancelable(cancellable)
                .setOnCancelListener { cancelCallback?.invoke() }
                .setView(view)
                .create()
        progressDialog?.show()

        return progressDialog!!
    }

    fun createThemeDialog(activity: Activity, onClickVideoCallback:  (() -> Unit), createCallback: ((themeTitle: String, urlPdfAttach: String) -> Unit), cancelCallback: (() -> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_create_theme, null, true)

        val titleEditText = view.findViewById<TextInputEditText>(R.id.tv_theme_title)

        val createThemeDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .setOnDismissListener {cancelCallback.invoke() }
                .create()

        view.btn_attachment_pdf_theme.setOnClickListener {
            onClickVideoCallback.invoke()
        }

        view.btn_cancel_theme.setOnClickListener {
            createThemeDialog.dismiss()
            cancelCallback.invoke()
        }

        view.btn_create_theme  .setOnClickListener {
            createCallback.invoke(titleEditText.text.toString(), "")
            createThemeDialog.dismiss()
        }

        createThemeDialog.show()
        return createThemeDialog
    }

    fun showYesNoMessage(activity: Activity, title: String, message: String, positiveCallback: (() -> Unit)){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("SIM"){dialog, which ->
            positiveCallback.invoke()
            dialog.dismiss()
        }
        builder.setNeutralButton("NÃO"){dialog,_ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    fun showAlert(activity: Activity, titleAlert: String, textAlert: String) {

        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            builder = AlertDialog.Builder(activity)
        }
        builder.setTitle(titleAlert)
                .setMessage(textAlert)
                .setPositiveButton("ok") { dialog, which -> dialog.dismiss() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
            cursor.close()
        }
        return res
    }


}
