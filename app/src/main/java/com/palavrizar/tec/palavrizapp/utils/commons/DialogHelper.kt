package com.palavrizar.tec.palavrizapp.utils.commons

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.*
import com.palavrizar.tec.palavrizapp.utils.adapters.ThemesListPickerAdapter
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnThemeClicked
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_add_plan.view.*
import kotlinx.android.synthetic.main.dialog_create_concept.view.*
import kotlinx.android.synthetic.main.dialog_create_structure.view.*
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

    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
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
        if (!titleEditText?.text.isNullOrBlank()){
            view.btn_create_theme.isEnabled = true
        }
        titleEditText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.btn_create_theme.isEnabled = s?.isEmpty() != true
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

    fun createStructureDialog(activity: Activity, isEdit: Boolean? = false, structure: Structure? = null,  createCallback: ((Structure) -> Unit), cancelCallback: (() -> Unit), onDeleteCallback: (()-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_create_structure, null, true)
        val titleEditText = view.findViewById<TextInputEditText>(R.id.tv_structure)

        if (structure != null){
            titleEditText.setText(structure.structure)
            if (isEdit == true) {
                view.create_structure_title_label.text = activity.getString(R.string.edit_structure_label)
                view.btn_create_structure.text = activity.getString(R.string.create_theme_edit_option)
            }
        }

        if (!titleEditText?.text.isNullOrBlank()){
            view.btn_create_structure.isEnabled = true
        }
        titleEditText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.btn_create_structure.isEnabled = s?.isEmpty() != true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.btn_create_structure.isEnabled = s?.isEmpty() != true
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val createStructureDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .setOnDismissListener {cancelCallback.invoke() }
                .create()

        if (isEdit == false || isEdit == null) {
            view.btn_delete_structure.visibility = View.GONE
        }

        view.btn_delete_structure.setOnClickListener {
            createStructureDialog.dismiss()
            onDeleteCallback.invoke()
        }

        view.btn_cancel_structure.setOnClickListener {
            createStructureDialog.dismiss()
            cancelCallback.invoke()
        }

        view.btn_create_structure.setOnClickListener {
            if (isEdit == false){
                createCallback.invoke(Structure(titleEditText.text.toString()))
                createStructureDialog.dismiss()
            }else{
                if (structure != null) {
                    structure.structure= titleEditText?.text.toString()
                    createCallback.invoke(structure)
                    createStructureDialog.dismiss()
                }
            }

        }
        createStructureDialog.show()
        return createStructureDialog
    }

    fun createConceptDialog(activity: Activity, isEdit: Boolean? = false, concept: Concept? = null,  createCallback: ((Concept) -> Unit), cancelCallback: (() -> Unit), onDeleteCallback: (()-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_create_concept, null, true)
        val titleEditText = view.findViewById<TextInputEditText>(R.id.tv_concept_title)

        if (concept != null){
            titleEditText.setText(concept.concept)
            if (isEdit == true) {
                view.create_concept_title_label.text = activity.getString(R.string.edit_concept_label)
                view.btn_create_concept.text = activity.getString(R.string.create_theme_edit_option)
            }
        }

        if (!titleEditText?.text.isNullOrBlank()){
            view.btn_create_concept.isEnabled = true
        }
        titleEditText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.btn_create_concept.isEnabled = s?.isEmpty() != true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.btn_create_concept.isEnabled = s?.isEmpty() != true
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.btn_create_concept.isEnabled = s?.isEmpty() != true
            }
        })

        val createConceptDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .setOnDismissListener {cancelCallback.invoke() }
                .create()

        if (isEdit == false || isEdit == null) {
            view.btn_delete_concept.visibility = View.GONE
        }

        view.btn_delete_concept.setOnClickListener {
            createConceptDialog.dismiss()
            onDeleteCallback.invoke()
        }

        view.btn_cancel_concept.setOnClickListener {
            createConceptDialog.dismiss()
            cancelCallback.invoke()
        }

        view.btn_create_concept.setOnClickListener {
            if (isEdit == false){
                createCallback.invoke(Concept(titleEditText.text.toString()))
                createConceptDialog.dismiss()
            }else{
                if (concept != null) {
                    concept.concept = titleEditText?.text.toString()
                    createCallback.invoke(concept)
                    createConceptDialog.dismiss()
                }
            }

        }
        createConceptDialog.show()
        return createConceptDialog
    }

    fun createAddPlanDialog(activity: Activity, isEdit: Boolean? = false, plansBilling: PlansBilling? = null,  createCallback: ((PlansBilling) -> Unit), cancelCallback: (() -> Unit), onDeleteCallback: (()-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_add_plan, null, true)

        val idPlanEditText = view.findViewById<TextInputEditText>(R.id.et_plan_id)
        val idPlanEditText2 = view.findViewById<TextInputEditText>(R.id.et_plan_id2)
        // val qntLimitEssayEditText = view.findViewById<TextInputEditText>(R.id.et_essay_limit_quant)
        val checkboxIsActive = view.findViewById<CheckBox>(R.id.check_active_limit)
        val radioDay = view.findViewById<RadioButton>(R.id.radio_day)
        val radioWeek = view.findViewById<RadioButton>(R.id.radio_week)
        val radioMonth = view.findViewById<RadioButton>(R.id.radio_month)

        if (plansBilling != null){
            idPlanEditText.setText(plansBilling.plan_id)
            val limitEssay = plansBilling.limitEssay
            val period = plansBilling.period
            if (limitEssay != null){
                idPlanEditText2.setText(plansBilling.limitEssay.toString())
                checkboxIsActive.isChecked = true
                //view.et_essay_limit_quant.setText(limitEssay)

                if (period != null){
                    when {
                        period == EnumPeriod.DIARIO -> radioDay.isChecked = true
                        period == EnumPeriod.SEMANAL -> radioWeek.isChecked = true
                        period == EnumPeriod.MENSAL -> radioMonth.isChecked = true
                    }
                }
            }else{
                checkboxIsActive.isChecked = false
            }

            if (isEdit == true) {
                idPlanEditText.isEnabled = false
                view.create_structure_add_plan.text = activity.getString(R.string.edit_plan_label)
                view.btn_create_plan.text = activity.getString(R.string.create_theme_edit_option)
            }
        }

        if (!idPlanEditText?.text.isNullOrBlank()){
            view.btn_create_plan.isEnabled = true
        }
        idPlanEditText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.btn_create_plan.isEnabled = s?.isEmpty() != true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.btn_create_plan.isEnabled = s?.isEmpty() != true
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.btn_create_plan.isEnabled = s?.isEmpty() != true
            }
        })

        val createConceptDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .setOnDismissListener {cancelCallback.invoke() }
                .create()

        if (isEdit == false || isEdit == null) {
            view.btn_delete_plan.visibility = View.GONE
        }

        /*  holder.view.check_plan.setOnCheckedChangeListener { buttonView, isChecked ->
              planList[index].isChecked = isChecked
          }
  */

        checkboxIsActive.setOnCheckedChangeListener { buttonView, isChecked ->
            //qntLimitEssayEditText.isEnabled = isChecked
            radioDay.isEnabled = isChecked
            radioMonth.isEnabled = isChecked
            radioWeek.isEnabled = isChecked

        }

        view.btn_delete_plan.setOnClickListener {
            createConceptDialog.dismiss()
            onDeleteCallback.invoke()
        }

        view.btn_cancel_plan.setOnClickListener {
            createConceptDialog.dismiss()
            cancelCallback.invoke()
        }

        view.btn_create_plan.setOnClickListener {
            if (isEdit == false) {

                var period = EnumPeriod.DIARIO

                if (checkboxIsActive.isChecked) {
                    if (radioDay.isChecked) {
                        period = EnumPeriod.DIARIO
                    } else if (radioWeek.isChecked) {
                        period = EnumPeriod.SEMANAL
                    } else if (radioMonth.isChecked) {
                        period = EnumPeriod.MENSAL
                    }

                    createCallback.invoke(PlansBilling(idPlanEditText.text.toString(), idPlanEditText2.text.toString().toInt(), period))
                } else {
                    createCallback.invoke(PlansBilling(idPlanEditText.text.toString(), null, null))
                }
                createConceptDialog.dismiss()
            } else {
                if (plansBilling != null) {
                    plansBilling.plan_id = idPlanEditText.text.toString()

                    if (checkboxIsActive.isChecked) {
                        var period = EnumPeriod.DIARIO
                        if (radioDay.isChecked) {
                            period = EnumPeriod.DIARIO
                        } else if (radioWeek.isChecked) {
                            period = EnumPeriod.SEMANAL
                        } else if (radioMonth.isChecked) {
                            period = EnumPeriod.MENSAL
                        }
                        plansBilling.period = period
                        plansBilling.limitEssay = idPlanEditText2.text.toString().toInt()
                    } else {
                        plansBilling.period = null
                        plansBilling.limitEssay = null
                    }

                    createCallback.invoke(plansBilling)
                    createConceptDialog.dismiss()


                }

            }
        }
        createConceptDialog.show()
        return createConceptDialog
    }


    fun createThemePickerDialog(activity: Activity, listThemes: ArrayList<Themes>, onThemePicked: ((Themes)-> Unit), onPdfClicked: ((String)-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_theme_picker, null, true)

        val rvThemePicker = view.findViewById<RecyclerView>(R.id.rv_theme_picker)
        rvThemePicker.layoutManager = LinearLayoutManager(activity)
        val themePickerDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        val adapter = ThemesListPickerAdapter(object: OnThemeClicked{
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
        val spUserPlans = view.findViewById<EditText>(R.id.et_plans)
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

        spUserPlans.setText(user.plan)



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
            user.plan = spUserPlans.text.toString()
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