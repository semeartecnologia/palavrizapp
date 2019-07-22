package com.palavrizar.tec.palavrizapp.modules.plans

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.admin.plans.ListPlansFragment

class MyPlansActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_plans)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupView()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupView(){
        setupPlansFragment()
    }


    private fun setupPlansFragment() {
        val argumentFragment = ListPlansFragment()//Get Fragment Instance
        val data = Bundle()//Use bundle to pass data
        data.putBoolean("isAdmin", false)
        argumentFragment.arguments = data//Finally set argument bundle to fragment
        supportFragmentManager.beginTransaction().replace(R.id.framePlans, argumentFragment).commit()
    }
}
