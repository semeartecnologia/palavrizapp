package com.semear.tec.palavrizapp.modules.login.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.crashlytics.android.Crashlytics
import com.facebook.CallbackManager
import com.semear.tec.palavrizapp.BuildConfig
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.login.LoginRegisterViewModel
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_login.*

class LoginFragment : Fragment() {

    private var loginViewModel: LoginRegisterViewModel? = null
    val G_SIGN_IN = 233
    var callbackManager: CallbackManager? = null

    companion object {
        fun newInstance(): LoginFragment{
            return LoginFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        initViewModel()
    }

    fun initViewModel(){
        loginViewModel = ViewModelProviders.of(activity ?: return).get(LoginRegisterViewModel::class.java)
        loginViewModel?.initViewModel()
    }

    fun setupView(){
        initFabric()
        initDebugData()
        setupRegisterButton()
        setupFacebookLoginButton()
        setupCallbackFacebookManager()
        setupGmailLoginButton()
        setupEmailLoginButton()
        setVersionNameText()
    }

    fun initFabric(){
        Fabric.with(activity, Crashlytics())
    }

    fun initDebugData(){
        if (BuildConfig.DEBUG){

        }
    }

    fun setupRegisterButton(){
        btn_register.setOnClickListener { loginViewModel?.startRegisterActivity(email.getText().toString()) }
    }

    fun setupFacebookLoginButton(){
        btn_facebook_login.setOnClickListener { login_facebook.performClick() }
    }

    fun setupGmailLoginButton(){
        btn_google_login.setOnClickListener { gmailLogin() }
    }

    fun setupEmailLoginButton(){
        email_sign_in_button.setOnClickListener { emailLogin() }
    }

    fun setVersionNameText(){
        tv_version_name.text = loginViewModel?.versionName
    }

    fun setupCallbackFacebookManager(){
        this.callbackManager = loginViewModel?.initFacebookLogin(activity, login_facebook)
    }

    fun emailLogin() {
        loginViewModel?.authWithEmail(activity, email.toString(), password.toString())
    }

    fun gmailLogin() {
        val signInIntent = loginViewModel?.googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, G_SIGN_IN)
    }

}