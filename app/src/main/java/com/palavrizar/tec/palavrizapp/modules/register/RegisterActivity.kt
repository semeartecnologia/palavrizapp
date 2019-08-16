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
import android.view.View
import butterknife.ButterKnife
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
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
        registerViewModel?.getBlacklist {
            it.forEach { location ->
                if (location.city.toLowerCase() == city.toLowerCase()){
                    DialogHelper.showMessage(this, "", getString(R.string.app_not_available_sorry))
                    btn_register.isEnabled = false
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

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            224 -> {
                locationManager?.getLastKnownLocation(provider)
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
            registerViewModel?.registerWithEmail(this@RegisterActivity, emailText, passwordText, confPassword, name, radioGroupGender?.checkedRadioButtonId ?: 0)
        }

        btn_cancel?.setOnClickListener { v -> finish() }
    }


}
