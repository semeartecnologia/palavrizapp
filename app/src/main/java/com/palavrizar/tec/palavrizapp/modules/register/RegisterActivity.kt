package com.palavrizar.tec.palavrizapp.modules.register

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import butterknife.ButterKnife
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.Utils
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.IOException
import java.util.*


class RegisterActivity : BaseActivity() {


    private var locationManager: LocationManager? = null
    private var registerViewModel: RegisterViewModel? = null
    private var provider: String = ""

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

        //   getUserLocation()
    }

    private fun checkLocationBlacklisted(email: String, onCompletion: (Boolean?) -> Unit){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        checkWhitelist(email){ isWhitelisted ->
            if (!isWhitelisted){
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
                        checkBlacklistCity(addresses[0].subAdminArea,onCompletion)
                    }

                }
            }else{
                //  loginViewModel?.startApplication(user)
                onCompletion(false)
            }
        }

    }

    private fun checkWhitelist(email: String, onCompletion: ((Boolean) -> Unit)){
        var isWhitelist = false
        registerViewModel?.getWhitelist {
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

    private fun checkBlacklistCity(city: String, onCompletion: (Boolean) -> Unit){
        var isBlacklisted = false
        registerViewModel?.getBlacklist {
            it.forEach { location ->
                if (location.city.toLowerCase() == city.toLowerCase()){
                    DialogHelper.showMessage(this, "", getString(R.string.app_not_available_sorry))
                    btn_register.isEnabled = false
                    isBlacklisted = true
                    onCompletion(true)
                }
            }
            if (!isBlacklisted){
                onCompletion(false)
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

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            224 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }else{
                    DialogHelper.showMessage(this, "", "Você precisa fornecer autorização de localização para continuar")
                }
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.actionbar_register_title)
    }

    private fun initViewModel() {
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        registerViewModel?.initViewModel()
    }

    private fun registerObservers() {
        registerViewModel?.showMessageErrorRegister?.observe(this, Observer
        { aBoolean ->
            showToast(getString(R.string.register_fail), aBoolean ?: true)
        })

        registerViewModel?.showMessageMissingFields?.observe(this, Observer
        { aBoolean -> showToast(getString(R.string.fill_all_fields), aBoolean ?: true) })

        registerViewModel?.showMessagePwdNotMatch?.observe(this, Observer
        { aBoolean -> showToast(getString(R.string.pwd_not_match), aBoolean ?: true) })

        registerViewModel?.isLoading?.observe(this,
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


    private fun setupButtonEvents() {

        email.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                if (!Utils.isValidEmail(email.text.toString())){
                    email.error = "E-mail inválido"
                }
            }
        }

        confirm_password?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                if (confirm_password.text.toString() != password.text.toString() ){
                    confirm_password.error = "Senhas diferentes"
                }
            }
        }


        btn_register?.setOnClickListener { v ->
            val emailText = email?.text?.toString() ?: return@setOnClickListener
            val passwordText = password?.text?.toString() ?: return@setOnClickListener
            val name = fullname?.text?.toString() ?: return@setOnClickListener
            val confPassword = confirm_password?.text?.toString() ?: return@setOnClickListener

            if (!Utils.isValidEmail(emailText)) {
                email.error = "E-mail inválido"
            } else {
                checkLocationBlacklisted(emailText) {
                    if (it == false) {
                        if (passwordText.length < 6) {
                            DialogHelper.showMessage(this, "", "A senha deve ter ao menos 6 caracteres")
                        } else {
                            registerViewModel?.registerWithEmail(this@RegisterActivity, emailText, passwordText, confPassword, name)
                        }
                    }
                }
            }


        }


        btn_cancel?.setOnClickListener { v -> finish() }
    }


}
