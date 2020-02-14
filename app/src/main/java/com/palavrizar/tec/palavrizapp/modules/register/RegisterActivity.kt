package com.palavrizar.tec.palavrizapp.modules.register

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import butterknife.ButterKnife
import com.google.android.youtube.player.internal.r
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.commons.Utils
import com.palavrizar.tec.palavrizapp.utils.constants.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_register.*
import org.w3c.dom.Text
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
                        onCompletion(true)
                        e.printStackTrace()
                    }

                    if (addresses != null && addresses.isNotEmpty()) {
                        checkBlacklistCity(addresses[0].subAdminArea,onCompletion)
                    }else{
                        if (addresses == null || addresses.isEmpty()){
                            if (!isLocationEnabled()){
                                DialogHelper.showMessage(this, "", getString(R.string.location_not_available))
                            }else{
                                onCompletion(false)
                            }
                        }else {
                            onCompletion(true)
                        }
                    }

                }else{
                    if (!isLocationEnabled()){
                        DialogHelper.showMessage(this, "", getString(R.string.location_not_available))
                    }else{
                        onCompletion(false)
                    }
                }
            }else{
                //  loginViewModel?.startApplication(user)
                onCompletion(false)
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
                    val res = resources
                    val text = String.format(res.getString(R.string.app_not_available_sorry), location.city.toLowerCase(Locale.getDefault()).capitalize())
                    DialogHelper.showMessage(this, "", text)
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
        { errorCode ->
            if (errorCode == Constants.USER_ALREADY_REGISTERED_ERROR){
                showToast(getString(R.string.register_failed_email_already), true)
            }else{
                showToast(getString(R.string.register_fail), true)
            }

        })

        registerViewModel?.showMessageMissingFields?.observe(this, Observer
        { aBoolean -> showToast(getString(R.string.fill_all_fields), aBoolean ?: true) })

        registerViewModel?.showMessagePwdNotMatch?.observe(this, Observer
        { aBoolean ->
            showToast(getString(R.string.pwd_not_match), aBoolean ?: true)
            textInputPwdConfirm.error = "Senhas diferentes"
        })

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

    private fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private fun keyboardIsShown(isVisible: Boolean){
        if (isVisible){
            fab_hide_keyboard?.show()
        }else{
            fab_hide_keyboard?.hide()
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupButtonEvents() {

        val activity = this
        viewRoot.viewTreeObserver.addOnGlobalLayoutListener {
            var  heightDiff = viewRoot.rootView.height - viewRoot.height
            if (heightDiff > dpToPx(activity, 200f)) { // if more than 200 dp, it's probably a keyboard...
                keyboardIsShown(true)
            }else{
                keyboardIsShown(false)
            }
        }

        fab_hide_keyboard?.setOnClickListener {
            this.hideKeyboard()
        }

        email.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                if (!Utils.isValidEmail(email.text.toString())){
                    textInputEmail.error = "E-mail inválido"
                }else{
                    textInputEmail.error = null
                }
            }
        }

        confirm_password?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                if (confirm_password.text.toString() != password.text.toString() ){
                    textInputPwdConfirm.error = "Senhas diferentes"
                }else{
                    textInputPwdConfirm.error = null
                }
            }
        }

        confirm_password?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                confirm_password?.clearFocus()
                true
            }else{
                false
            }
        }



        btn_register?.setOnClickListener { v ->
            val emailText = email?.text?.toString() ?: return@setOnClickListener
            val passwordText = password?.text?.toString() ?: return@setOnClickListener
            val name = fullname?.text?.toString() ?: return@setOnClickListener
            val confPassword = confirm_password?.text?.toString() ?: return@setOnClickListener

            if (name.contains("Laboratoire")){
                return@setOnClickListener
            }

            if (!Utils.isValidEmail(emailText)) {
                textInputEmail.error = "E-mail inválido"
            } else {
                textInputEmail.error = null
                checkLocationBlacklisted(emailText) {
                    if (it == false) {
                        if (passwordText.length < 6) {
                            DialogHelper.showMessage(this, "", "A senha deve ter ao menos 6 caracteres")
                        } else {
                            registerViewModel?.registerWithEmail(this@RegisterActivity, emailText, passwordText, confPassword, name)
                        }
                    }else{
                        DialogHelper.showMessage(this, "", getString(R.string.app_not_available_sorry))
                    }
                }
            }


        }


        btn_cancel?.setOnClickListener { v -> finish() }
    }


}
