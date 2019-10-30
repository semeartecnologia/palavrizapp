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
import com.palavrizar.tec.palavrizapp.utils.adapters.LocationAdapter
import com.palavrizar.tec.palavrizapp.utils.adapters.ThemesListPickerAdapter
import com.palavrizar.tec.palavrizapp.utils.adapters.WhitelistAdapter
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnRemoveLocationClicked
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnRemoveWhitelist
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnThemeClicked
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_add_plan.view.*
import kotlinx.android.synthetic.main.dialog_add_plan.view.btn_cancel_plan
import kotlinx.android.synthetic.main.dialog_add_plan.view.create_structure_add_plan
import kotlinx.android.synthetic.main.dialog_add_product.view.*
import kotlinx.android.synthetic.main.dialog_choose_plan_or_product.view.*
import kotlinx.android.synthetic.main.dialog_create_concept.view.*
import kotlinx.android.synthetic.main.dialog_create_concept.view.btn_delete_concept
import kotlinx.android.synthetic.main.dialog_create_structure.view.*
import kotlinx.android.synthetic.main.dialog_create_theme.view.*
import kotlinx.android.synthetic.main.dialog_edit_user.view.*
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*
import kotlinx.android.synthetic.main.dialog_limit_location.view.*
import kotlinx.android.synthetic.main.dialog_theme_picker.view.*
import kotlinx.android.synthetic.main.dialog_whitelist.view.*

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
        }

        view.layout_pdf_filename.setOnClickListener {
            theme?.urlPdf = ""
            view.layout_pdf_filename.visibility = View.GONE
            view.btn_attachment_pdf_theme.isEnabled = true
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

    fun createPlanOrProductDialog(activity: Activity, onProductsSelected: (()-> Unit), onPlansSelected: (()-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_choose_plan_or_product, null, true)

        val createPlanOrProductDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        view.layout_pick_products.setOnClickListener {
            createPlanOrProductDialog.dismiss()
            onProductsSelected.invoke()
        }

        view.layout_pick_plans.setOnClickListener {
            createPlanOrProductDialog.dismiss()
            onPlansSelected.invoke()
        }

        createPlanOrProductDialog.show()
        return createPlanOrProductDialog

    }

    fun createForgotPasswordDialog(activity: Activity, email: String? = "", onSentClicked: ((String)-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_forgot_password, null, true)
        val etEmail = view.findViewById<TextInputEditText>(R.id.et_email_forgot)


        val forgotPasswordDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        if (!email.isNullOrBlank()){
            etEmail.setText(email)
        }

        view.btn_forgot.setOnClickListener {
            forgotPasswordDialog.dismiss()
            onSentClicked.invoke(etEmail.text.toString())
        }

        view.btn_cancel_forgot.setOnClickListener {
            forgotPasswordDialog.dismiss()
        }

        forgotPasswordDialog.show()
        return forgotPasswordDialog

    }

    fun createWhitelistDialog(activity: Activity, listWhitelist: ArrayList<EmailWhitelist>, onSaveCallback: ((EmailWhitelist)-> Unit), onRemoveCallback: ((EmailWhitelist)-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_whitelist, null, true)
        val emailEditText = view.findViewById<TextInputEditText>(R.id.et_email_whitelist)

        val rvWhitelist = view.findViewById<RecyclerView>(R.id.rv_whitelist)
        rvWhitelist.layoutManager = LinearLayoutManager(activity)
        val adapter = WhitelistAdapter(object: OnRemoveWhitelist{
            override fun onRemoveClicked(login: EmailWhitelist, index: Int) {
                onRemoveCallback(login)
                listWhitelist.remove(login)
                if (listWhitelist.isEmpty()){
                    rvWhitelist.visibility = View.GONE
                }
            }
        })

        val createWhitelistDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        view.btn_cancel_whitelist.setOnClickListener {
            createWhitelistDialog.dismiss()
        }

        view.btn_add_whitelist.setOnClickListener {
            if (!emailEditText.text.toString().isBlank() ){
                onSaveCallback.invoke(EmailWhitelist(emailEditText.text.toString()))
                adapter.addWhitelist(EmailWhitelist(emailEditText.text.toString()))
                emailEditText.setText("")
                rvWhitelist.visibility = View.VISIBLE
            }else{
                showToast(activity, activity.getString(R.string.fill_all_fields))
            }
        }
        adapter.emailWhitelist = listWhitelist

        if (listWhitelist.isNotEmpty()){
            rvWhitelist.visibility = View.VISIBLE
        }

        rvWhitelist.adapter = adapter
        createWhitelistDialog.show()
        return createWhitelistDialog

    }



    fun createLocationDialog(activity: Activity, listBlacklist: ArrayList<LocationBlacklist>, onSaveCallback: ((LocationBlacklist)-> Unit), onRemoveCallback: ((LocationBlacklist)-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_limit_location, null, true)
        val cityEditText = view.findViewById<TextInputEditText>(R.id.et_location_city)
        val stateEditText = view.findViewById<TextInputEditText>(R.id.et_location_state)

        val rvBlacklist = view.findViewById<RecyclerView>(R.id.rv_list_cities_banned)
        rvBlacklist.layoutManager = LinearLayoutManager(activity)
        val adapter = LocationAdapter(object: OnRemoveLocationClicked{
            override fun onRemoveClicked(location: LocationBlacklist, index: Int) {
                onRemoveCallback(location)
                listBlacklist.remove(location)
                if (listBlacklist.isEmpty()){
                    rvBlacklist.visibility = View.GONE
                }
            }
        })

        val createLocationDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        view.btn_cancel_location.setOnClickListener {
            createLocationDialog.dismiss()
        }

        view.btn_add_city.setOnClickListener {
            if (!cityEditText.text.toString().isBlank() && !stateEditText.text.toString().isBlank()){
                onSaveCallback.invoke(LocationBlacklist(cityEditText.text.toString(), stateEditText.text.toString()))
                adapter.addLocation(LocationBlacklist(cityEditText.text.toString(), stateEditText.text.toString()))
                rvBlacklist.visibility = View.VISIBLE
            }else{
                showToast(activity, activity.getString(R.string.fill_all_fields))
            }
        }
        adapter.locationBlacklist = listBlacklist

        if (!listBlacklist.isEmpty()){
            rvBlacklist.visibility = View.VISIBLE
        }

        rvBlacklist.adapter = adapter
        createLocationDialog.show()
        return createLocationDialog

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

    fun createAddProductDialog(activity: Activity, isEdit: Boolean? = false, product: Product? = null,  createCallback: ((Product) -> Unit), onDeleteCallback: ((String)-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_add_product, null, true)

        val idProductEditText = view.findViewById<TextInputEditText>(R.id.et_product_id)
        val numAvulsosEditText = view.findViewById<TextInputEditText>(R.id.et_num_avulsos)

        if (product != null){
            idProductEditText.setText(product.product_id)
            val numCredits = product.numCredits
            if (numCredits != null){
                numAvulsosEditText.setText(numCredits.toString())
            }

            if (isEdit == true) {
                idProductEditText.isEnabled = false
                view.create_structure_add_product.text = activity.getString(R.string.edit_product_label)
                view.btn_create_product.text = activity.getString(R.string.create_theme_edit_option)
                view.btn_delete_product?.visibility = View.VISIBLE
            }
        }

        if (!idProductEditText?.text.isNullOrBlank()){
            view.btn_create_product.isEnabled = true
        }
        idProductEditText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.btn_create_product.isEnabled = s?.isEmpty() != true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                view.btn_create_product.isEnabled = s?.isEmpty() != true
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.btn_create_product.isEnabled = s?.isEmpty() != true
            }
        })

        val createAddProductDialog  = AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true)
                .create()

        if (isEdit == false || isEdit == null) {
            view.btn_delete_product.visibility = View.GONE
        }

        view.btn_delete_product.setOnClickListener {
            createAddProductDialog.dismiss()
            onDeleteCallback.invoke(product!!.productKey)
        }

        view.btn_cancel_product.setOnClickListener {
            createAddProductDialog.dismiss()
        }

        view.btn_create_product.setOnClickListener {
            if (isEdit == false) {
                createCallback.invoke(Product(idProductEditText.text.toString(), numAvulsosEditText.text.toString().toInt()))
                createAddProductDialog.dismiss()
            } else {
                if (product != null) {
                    product.product_id = idProductEditText.text.toString()
                    product.numCredits = numAvulsosEditText.text.toString().toInt()
                    createCallback.invoke(product)
                    createAddProductDialog.dismiss()

                }

            }
        }
        createAddProductDialog.show()
        return createAddProductDialog
    }


    fun createAddPlanDialog(activity: Activity, isEdit: Boolean? = false, plansBilling: PlansBilling? = null,  createCallback: ((PlansBilling) -> Unit), cancelCallback: (() -> Unit), onDeleteCallback: (()-> Unit)): AlertDialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_add_plan, null, true)

        val idPlanEditText = view.findViewById<TextInputEditText>(R.id.et_plan_id)
        val idPlanEditText2 = view.findViewById<TextInputEditText>(R.id.et_plan_id2)
        // val qntLimitEssayEditText = view.findViewById<TextInputEditText>(R.id.et_essay_limit_quant)
        val checkboxIsActive = view.findViewById<CheckBox>(R.id.check_active_limit)
        val radioQuinze = view.findViewById<RadioButton>(R.id.radio_quinze)
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
                        period == EnumPeriod.QUINZENAL -> radioQuinze.isChecked = true
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
            }else{
                idPlanEditText2.setText("1")
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
            radioQuinze.isEnabled = isChecked
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

                var period = EnumPeriod.QUINZENAL

                if (checkboxIsActive.isChecked) {
                    if (radioQuinze.isChecked) {
                        period = EnumPeriod.QUINZENAL
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
                        var period = EnumPeriod.QUINZENAL
                        if (radioQuinze.isChecked) {
                            period = EnumPeriod.QUINZENAL
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
                .setCancelable(false)
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

    fun showOkMessage(activity: Activity, title: String, message: String, callback: (() -> Unit), cancellable: Boolean = true){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(cancellable)
        builder.setPositiveButton("Ok"){dialog, which ->
            callback.invoke()
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showGoToStoreMessage(activity: Activity, title: String, message: String, positiveCallback: (() -> Unit), negativeCallback: (() -> Unit)? = null, cancellable: Boolean = true){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(cancellable)
        builder.setPositiveButton("Ir para loja!"){dialog, which ->
            positiveCallback.invoke()
            dialog.dismiss()
        }
        builder.setNeutralButton("Ok"){dialog,_ ->
            negativeCallback?.invoke()
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    fun showYesNoMessage(activity: Activity, title: String, message: String, positiveCallback: (() -> Unit), negativeCallback: (() -> Unit)? = null, cancellable: Boolean = true){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(cancellable)
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


    fun showMessage(activity: Activity, title: String, message: String){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok"){dialog, which ->
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