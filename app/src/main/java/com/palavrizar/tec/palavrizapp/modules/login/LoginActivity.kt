package com.palavrizar.tec.palavrizapp.modules.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.Utils
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
    }

    private fun getUserLocation(user: User){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        if (!user.email.isNullOrBlank()) {
            checkWhitelist(user.email) { isWhitelisted ->
                if (!isWhitelisted) {
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
                            checkBlacklistCity(addresses[0].subAdminArea, user)
                        }

                    }else{
                        if (!isLocationEnabled()){
                            DialogHelper.showMessage(this, "", getString(R.string.location_not_available))
                        }else{
                            loginViewModel?.startApplication(user)
                        }
                    }
                } else {
                    loginViewModel?.startApplication(user)
                }
            }
        }

    }

    private fun checkWhitelist(email: String, onCompletion: ((Boolean) -> Unit)){
        var isWhitelist = false
        loginViewModel?.getWhitelist {
            it.forEach {
                if (email == it.email){
                    isWhitelist = true
                    onCompletion(true)
                }
            }
            if (!isWhitelist) {
                onCompletion(false)
            }
        }
    }

    private fun checkBlacklistCity(city: String, user: User){
        var isBlacklist = false
        loginViewModel?.getBlacklist {
            it.forEach { location ->
                if (location.city.toLowerCase(Locale.getDefault()) == city.toLowerCase(Locale.getDefault())){
                    val res = resources
                    val text = String.format(res.getString(R.string.app_not_available_sorry), location.city.toLowerCase(Locale.getDefault()).capitalize())
                    DialogHelper.showMessage(this, "", text)
                    btn_google_login?.isEnabled = false
                    btn_email_login?.isEnabled = false
                    isBlacklist = true
                }
            }

            if (!isBlacklist){
                loginViewModel?.startApplication(user)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val manager =  getSystemService( Context.LOCATION_SERVICE ) as LocationManager

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return false
        }
        return true
    }

    private fun requestLocationPermission(): Location? {
        return if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
                    224)
            null
        } else {
            if(locationManager?.getAllProviders()?.contains(LocationManager.GPS_PROVIDER) == true && locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ==true) {
                locationManager?.getLastKnownLocation(provider)
            }else{
                null
            }
        }
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 224){
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission()
            }else{
                DialogHelper.showMessage(this, "", "Você precisa fornecer autorização de localização para continuar")
            }
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
        loginViewModel?.forgotPasswordSent?.observe(this, Observer {
            DialogHelper.showMessage(this, "", getString(R.string.forgot_password_sent))
        })
        loginViewModel?.checkLocationLiveData?.observe(this, Observer {
            if (it != null) {
                getUserLocation(it)

            }
        })
    }

    private fun initFabric(){
        Fabric.with(this, Crashlytics())
    }

    private fun initDebugData(){
        if (BuildConfig.DEBUG){
            et_email?.setText("arthur@email.com")
            et_password?.setText("art1234")
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



    private fun showLoginEmailErrorMessage(show: Boolean){
        if (show) {
            val title: String = getString(R.string.error_title)
            val text: String = getString(R.string.email_fail_login_text)
            val ok: String = getString(R.string.ok)
            val forgotPassword: String = getString(R.string.forgot_password)
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(title)
                    .setMessage(text)
                    .setPositiveButton(ok) { dialog, _ -> dialog.dismiss() }
                    .setNeutralButton(forgotPassword) { dialog, _ ->
                        val email = et_email.text.toString().trim()
                        DialogHelper.createForgotPasswordDialog(this, email) {
                            if (!it.isBlank() && Utils.isValidEmail(it)) {
                                dialog.dismiss()
                                loginViewModel?.onForgotEmailSent(email)

                            }else{
                                DialogHelper.showToast(this, "E-mail inválido")
                            }
                        }
                    }
                    .show()
        }
    }

    fun showProgressBar(show: Boolean){
        if (show){
            btn_email_login.setText("")

            progress_login.visibility = View.VISIBLE
            et_email.isEnabled = false
            et_password.isEnabled = false
        }else{
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

