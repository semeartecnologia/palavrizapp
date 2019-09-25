package com.palavrizar.tec.palavrizapp.modules

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.modules.dashboard.DashboardFragment
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.modules.classroom.ClassroomActivity
import android.content.Intent
import com.palavrizar.tec.palavrizapp.models.Video
import com.palavrizar.tec.palavrizapp.utils.constants.Constants


class MainActivity : BaseActivity(), DashboardFragment.OnFragmentInteractionListener {

    private var sessionManager: SessionManager? = null
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        sessionManager = SessionManager(applicationContext)
        if (sessionManager?.isUserFirstTime == true){
            startClassroomActivity()
            changeFragment(DashboardFragment(), "Dashboard")
        }else {
            changeFragment(DashboardFragment(), "Dashboard")
        }
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
        if (fragment !is DashboardFragment) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            ft.addToBackStack("tag")
        }
        ft.commit()
    }


    override fun OnPlansClicked() {

    }

    private fun startClassroomActivity() {
        mainViewModel?.startClassroomActivity(this)
    }

    override fun OnThemesClicked() {

    }
}
