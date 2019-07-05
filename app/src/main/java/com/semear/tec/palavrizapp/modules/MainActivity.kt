package com.semear.tec.palavrizapp.modules

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout

import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.modules.base.BaseActivity
import com.semear.tec.palavrizapp.modules.classroom.ClassroomActivity
import com.semear.tec.palavrizapp.modules.dashboard.DashboardFragment
import com.semear.tec.palavrizapp.utils.repositories.SessionManager

import butterknife.BindView
import butterknife.ButterKnife

import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_COD_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_DESCRPTION_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_SUBTITLE_VIDEO
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_TITLE_VIDEO

class MainActivity : BaseActivity(), DashboardFragment.OnFragmentInteractionListener {

    private var sessionManager: SessionManager? = null
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        initViewModel()
        registerObservers()
        sessionManager = SessionManager(applicationContext)
        changeFragment(DashboardFragment(), "Dashboard")
    }


    private fun registerObservers() {
        mainViewModel?.isUserOnline?.observe(this, Observer{ isOnline ->
            if (isOnline != null && (!isOnline)) {
                redirectToLogin()
            }
        })
    }

    fun setActionBarTitle(text: String) {
        if (supportActionBar != null)
            supportActionBar!!.title = text
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel?.initViewModel()
    }

    fun changeFragment(fragment: Fragment, fragName: String) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frameContent, fragment, fragName)
        if (fragment !is DashboardFragment)
            ft.addToBackStack("tag")
        ft.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel!!.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun OnPlansClicked() {

    }

    override fun OnThemesClicked() {

    }
}
