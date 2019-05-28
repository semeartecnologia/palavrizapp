package com.semear.tec.palavrizapp.modules.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.crashlytics.android.Crashlytics
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.semear.tec.palavrizapp.BuildConfig
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.utils.Commons
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Activity responsável por Login
 */
class LoginActivity : BaseActivity() {

    private var callbackManager: CallbackManager? = null

    companion object {
        const val G_SIGN_IN = 233
    }

    private var loginViewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        initViewModel()
        setupView()
        registerObservers()
    }

    private fun setupView(){
        initFabric()
        setupButtonEvents()
        initDebugData()
        setupCallbackFacebookManager()
    }

    private fun registerObservers(){
        loginViewModel?.showEmailPasswordIncorrectDialog?.observe(this,
                Observer {showLoginEmailErrorMessage(it == true)})

        loginViewModel?.isLoading?.observe(this,
                Observer {showProgressBar(it == true)})

        loginViewModel?.showCompleteFields?.observe(this,
                Observer {showToast(getString(R.string.fill_all_fields), it == true)
        })
    }

    private fun initFabric(){
        Fabric.with(this, Crashlytics())
    }

    private fun initDebugData(){
        if (BuildConfig.DEBUG){
            et_email?.setText("arthurmazer@hotmail.com")
            et_password?.setText("tamarindo")
        }
    }

    private fun setupButtonEvents(){
        btn_register?.setOnClickListener { loginViewModel?.startRegisterActivity() }
        btn_facebook_login?.setOnClickListener { login_facebook.performClick() }
        btn_google_login?.setOnClickListener { gmailLogin() }
        btn_email_login?.setOnClickListener { emailLogin() }
    }


    private fun setupCallbackFacebookManager(){
        this.callbackManager = loginViewModel?.initFacebookLogin(this, login_facebook)
    }

    private fun emailLogin() {
        loginViewModel?.authWithEmail(this, et_email.text.toString().trim(), et_password.text.toString())
    }

    private fun gmailLogin() {
        val signInIntent = loginViewModel?.googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, G_SIGN_IN)
    }

    fun initViewModel(){
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginViewModel?.initViewModel()
    }

    fun showLoginEmailErrorMessage(show: Boolean){
        if (show) {
            val title: String = getString(R.string.error_title)
            val text: String = getString(R.string.email_fail_login_text)
            val ok: String = getString(R.string.ok)
            val forgotPassword: String = getString(R.string.forgot_password)
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(title)
                    .setMessage(text)
                    .setPositiveButton(ok) { dialog, _ -> dialog.dismiss() }
                    .setNeutralButton(forgotPassword) { dialog, _ -> dialog.dismiss() }
                    .show()
        }
    }

    fun showProgressBar(show: Boolean){
        if (show){
            btn_facebook_login.visibility = View.GONE
            btn_email_login.visibility = View.INVISIBLE
            btn_google_login.visibility = View.INVISIBLE
            progress_login.visibility = View.VISIBLE
            et_email.isEnabled = false
            et_password.isEnabled = false
        }else{
            btn_facebook_login.visibility = View.VISIBLE
            btn_email_login.visibility = View.VISIBLE
            btn_google_login.visibility = View.VISIBLE
            progress_login.visibility = View.GONE
            et_email.isEnabled = true
            et_password.isEnabled = true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == G_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    loginViewModel!!.authWithGoogle(this@LoginActivity, account)
                }
            } catch (e: ApiException) {
                val titleAlert = getString(R.string.alert_gplay_title)
                val textAlert = getString(R.string.alert_gplay_text)
                val btnAlert = getString(R.string.alert_gplay_btn_accept)
                Commons.showAlert(this@LoginActivity, titleAlert, textAlert, btnAlert)
                e.printStackTrace()
            }

        }
    }

}

