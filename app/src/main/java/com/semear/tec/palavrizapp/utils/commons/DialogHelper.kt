package com.semear.tec.palavrizapp.utils.commons

import android.app.Activity
import android.os.Build
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.Plans
import com.semear.tec.palavrizapp.models.Themes
import com.semear.tec.palavrizapp.models.User
import com.semear.tec.palavrizapp.models.UserType
import com.semear.tec.palavrizapp.utils.adapters.ThemesListAdapter
import com.semear.tec.palavrizapp.utils.interfaces.OnThemeClicked
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_create_theme.view.*
import kotlinx.android.synthetic.main.dialog_edit_user.view.*
import kotlinx.android.synthetic.main.dialog_theme_picker.view.*

object DialogHelper {


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

    fun createThemeDialog(activity: Activity, isEdit: Boolean? = false, theme: Themes? = null, onClickAttachment:  ((Themes?) -> Unit), createCallback: ((Themes) -> Unit), cancelCallback: (() -> Unit), onDeleteCallback: (()-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_create_theme, null, true)

        val titleEditText = view.findViewById<TextInputEditText>(R.id.tv_theme_title)

        if (theme != null){
            titleEditText.setText(theme.themeName)
            if (isEdit == true) {
                view.create_theme_title_label.text = activity.getString(R.string.edit_essay_theme)
                view.btn_create_theme.text = activity.getString(R.string.create_theme_edit_option)
            }

            if (!theme.urlPdf.isNullOrBlank()){
                view.layout_pdf_filename.text = theme.urlPdf?.split("/")?.lastOrNull() ?: ""
                view.layout_pdf_filename.visibility = View.VISIBLE
                view.layout_pdf_filename.visibility = View.VISIBLE
                view.btn_attachment_pdf_theme.isEnabled = false
            }
        }
        titleEditText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.btn_create_theme.isEnabled = s?.isEmpty() != true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.btn_create_theme.isEnabled = s?.isEmpty() != true
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        val createThemeDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .setOnDismissListener {cancelCallback.invoke() }
                .create()

        view.btn_attachment_pdf_theme.setOnClickListener {
            createThemeDialog.dismiss()
            if (theme != null) {
                theme.themeName = titleEditText?.text.toString()
                onClickAttachment.invoke(theme)
            }else{
                val newTheme = Themes(titleEditText?.text.toString(), theme?.urlPdf ?: "")
                onClickAttachment.invoke(newTheme)
            }

        }

        if (isEdit == false || isEdit == null) {
            view.btn_delete_theme.visibility = View.GONE
            view.layout_pdf_filename.setOnClickListener {
                theme?.urlPdf = ""
                view.layout_pdf_filename.visibility = View.GONE
                view.btn_attachment_pdf_theme.isEnabled = true
            }
        }

        view.btn_delete_theme.setOnClickListener {
            createThemeDialog.dismiss()
            onDeleteCallback.invoke()
        }

        view.btn_cancel_theme.setOnClickListener {
            createThemeDialog.dismiss()
            cancelCallback.invoke()
        }

        view.btn_create_theme.setOnClickListener {
            if (isEdit == false){
                createCallback.invoke(Themes(titleEditText.text.toString(), theme?.urlPdf ?: ""))
                createThemeDialog.dismiss()
            }else{
                if (theme != null) {
                    theme.themeName = titleEditText?.text.toString()
                    createCallback.invoke(theme)
                    createThemeDialog.dismiss()
                }
            }

        }

        createThemeDialog.show()
        return createThemeDialog
    }

    fun createThemePickerDialog(activity: Activity, listThemes: ArrayList<Themes>, onThemePicked: ((Themes)-> Unit), onPdfClicked: ((String)-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_theme_picker, null, true)

        val rvThemePicker = view.findViewById<RecyclerView>(R.id.rv_theme_picker)
        rvThemePicker.layoutManager = LinearLayoutManager(activity)
        val themePickerDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        val adapter = ThemesListAdapter(object: OnThemeClicked{
            override fun onThemeClicked(theme: Themes) {
                themePickerDialog.dismiss()
                onThemePicked.invoke(theme)
            }

            override fun onDownloadPdfClicked(uri: String) {
                onPdfClicked.invoke(uri)
            }

        })

        view.iv_close_dialog.setOnClickListener {
            themePickerDialog.dismiss()
        }
        adapter.themesList = listThemes
        rvThemePicker.adapter = adapter
        if (!activity.isFinishing) {
            themePickerDialog.show()
        }
        return themePickerDialog
    }


    fun createEditUserAdminDialog(activity: Activity, user: User, onSaveClicked:  ((User) -> Unit), onDeleteClicked: (() -> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_edit_user, null, true)

        val tvUserName = view.findViewById<TextView>(R.id.tv_user_name)
        val tvUserEmail = view.findViewById<TextView>(R.id.tv_user_email)
        val spUserPlans = view.findViewById<Spinner>(R.id.spinner_plans)
        val spUserUserType = view.findViewById<Spinner>(R.id.spinner_user_type)
        val ivPhotoUser = view.findViewById<CircleImageView>(R.id.photo_user_dialog)


        tvUserName.text = user.fullname
        tvUserEmail.text = user.email


        if (!user.photoUri.isNullOrBlank()) {
            Picasso.get().load(user.photoUri).into(ivPhotoUser)
        }


        val listUserTypes = UserType.names()
        val adapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, listUserTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spUserUserType.adapter = adapter
        adapter.notifyDataSetChanged()
        spUserUserType.setSelection(user.userType.userType)

        val listPlans = Plans.names()
        val adapterPlans = ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, listPlans)

        adapterPlans.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spUserPlans.adapter = adapterPlans
        adapterPlans.notifyDataSetChanged()
        spUserPlans.setSelection(user.plan.userPlan)



        val createThemeDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        view.btn_cancel.setOnClickListener {
            createThemeDialog.dismiss()
        }

        view.btn_delete.setOnClickListener {
            createThemeDialog.dismiss()
            onDeleteClicked.invoke()
        }

        view.btn_save.setOnClickListener {
            createThemeDialog.dismiss()
            user.plan = Plans.values()[spUserPlans.selectedItemPosition]
            user.userType = UserType.values()[spUserUserType.selectedItemPosition]
            onSaveClicked.invoke(user)
        }

        createThemeDialog.show()
        return createThemeDialog
    }

    fun showYesNoMessage(activity: Activity, title: String, message: String, positiveCallback: (() -> Unit), negativeCallback: (() -> Unit)? = null){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("SIM"){dialog, which ->
            positiveCallback.invoke()
            dialog.dismiss()
        }
        builder.setNeutralButton("NÃO"){dialog,_ ->
            negativeCallback?.invoke()
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

}