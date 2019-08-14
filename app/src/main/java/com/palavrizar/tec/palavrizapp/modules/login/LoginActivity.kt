package com.palavrizar.tec.palavrizapp.modules.login

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import com.crashlytics.android.Crashlytics
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.palavrizar.tec.palavrizapp.BuildConfig
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_login.*
import java.io.IOException
import java.util.*


/**
 * Activity responsável por Login
 */
class LoginActivity : BaseActivity() {

    private var callbackManager: CallbackManager? = null
    private var locationManager: LocationManager? = null
    private var provider: String = ""


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

        getUserLocation()
    }

    private fun getUserLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        val criteria = Criteria()
        provider = locationManager?.getBestProvider(criteria, false).toString()
        val location = requestLocationPermission()

        if (location != null) {
            //System.out.println("Provider $provider has been selected.")
            //onLocationChanged(location)
            val lat = location.latitude
            val lng = location.longitude

            val gcd = Geocoder(this, Locale.getDefault())
            var addresses: List<Address>? = null
            try {
                addresses = gcd.getFromLocation(lat, lng, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (addresses != null && addresses.isNotEmpty()) {
                checkBlacklistCity(addresses[0].subAdminArea)
            }

        }
    }

    private fun checkBlacklistCity(city: String){
        loginViewModel?.getBlacklist {
            it.forEach { location ->
                if (location.city.toLowerCase() == city.toLowerCase()){
                    DialogHelper.showMessage(this, "", getString(R.string.app_not_available_sorry))
                    btn_google_login?.isEnabled = false
                    btn_email_login?.isEnabled = false
                }
            }
        }
    }

    private fun requestLocationPermission(): Location? {
        return if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
                    224)
            null
        } else {
            locationManager?.getLastKnownLocation(provider)
        }
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
//            btn_facebook_login.visibility = View.GONE
//            btn_email_login.visibility = View.INVISIBLE
//            btn_google_login.visibility = View.INVISIBLE
            btn_email_login.setText("")

            progress_login.visibility = View.VISIBLE
            et_email.isEnabled = false
            et_password.isEnabled = false
        }else{
//            btn_facebook_login.visibility = View.VISIBLE
//            btn_email_login.visibility = View.VISIBLE
//            btn_google_login.visibility = View.VISIBLE
            btn_email_login.setText(R.string.action_sign_in)
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
                DialogHelper.showAlert(this@LoginActivity, titleAlert, textAlert, btnAlert)
                e.printStackTrace()
            }

        }
    }

}

