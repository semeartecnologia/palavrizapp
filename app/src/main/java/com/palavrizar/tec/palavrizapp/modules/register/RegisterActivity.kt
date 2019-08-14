package com.palavrizar.tec.palavrizapp.modules.register

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup

import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.squareup.picasso.Picasso

import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterActivity : BaseActivity() {



    private var registerViewModel: RegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register)
        ButterKnife.bind(this)

        val extras = intent.extras
        if (extras != null) {
            email?.setText(extras.getString(Constants.EXTRA_LOGIN))
        }

        setupActionBar()
        initViewModel()
        setupButtonEvents()
        registerObservers()


    }

    fun setupActionBar() {
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = getString(R.string.actionbar_register_title)
        }
    }

    private fun initViewModel() {
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        registerViewModel!!.initViewModel()
    }

    private fun registerObservers() {
        registerViewModel!!.showMessageErrorRegister.observe(this, Observer
                { aBoolean ->
                    showToast(getString(R.string.register_fail), aBoolean ?: true)
                })

        registerViewModel!!.showMessageMissingFields.observe(this, Observer
                { aBoolean -> showToast(getString(R.string.fill_all_fields), aBoolean ?: true) })

        registerViewModel!!.showMessagePwdNotMatch.observe(this, Observer
                { aBoolean -> showToast(getString(R.string.pwd_not_match), aBoolean ?: true) })

        registerViewModel!!.isLoading.observe(this,
                Observer<Boolean> { this.toggleLoading(it ?: true) })
    }

    private fun toggleLoading(isLoading: Boolean) {
        if (isLoading) {
            btn_register?.text = ""
            progress_register?.visibility = View.VISIBLE
        } else {
            btn_register?.text = getString(R.string.register_now_btn)
            progress_register?.visibility = View.GONE
        }
    }

    private fun changeAvatarMale() {
        Picasso.get().load(R.drawable.avatar_man_512).into(profile_image)
    }

    private fun changeAvatarFemale() {
        Picasso.get().load(R.drawable.avatar_woman_512).into(profile_image)
    }

    private fun setupButtonEvents() {

        radioGroupGender?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_male -> changeAvatarMale()
                R.id.radio_female -> changeAvatarFemale()
                else -> {
                }
            }
        }

        btn_register?.setOnClickListener { v ->
            val emailText = email?.text?.toString() ?: return@setOnClickListener
            val passwordText = password?.text?.toString() ?: return@setOnClickListener
            val name = fullname?.text?.toString() ?: return@setOnClickListener
            val confPassword = confirm_password?.text?.toString() ?: return@setOnClickListener
            registerViewModel!!.registerWithEmail(this@RegisterActivity, emailText, passwordText, confPassword, name, radioGroupGender?.checkedRadioButtonId ?: 0)
        }

        btn_cancel?.setOnClickListener { v -> finish() }
    }


}
