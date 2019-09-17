package com.palavrizar.tec.palavrizapp.modules.admin

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.Menu
import android.view.MenuItem
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.adapters.PagerAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : BaseActivity() {

    private var pagerAdapter = PagerAdapter(supportFragmentManager)
    private lateinit var adminViewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.toolbar_admin)
        setContentView(R.layout.activity_admin)

        initViewModel()
        setupTabs()
    }

    private fun initViewModel() {
        adminViewModel = ViewModelProviders.of(this).get(AdminViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_location -> {
                showDialogLocationBlacklist()
                true
            }
            R.id.action_whitelist -> {
                showDialogLoginWhitelist()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialogLoginWhitelist(){
        adminViewModel.getLoginWhitelist { list ->
            DialogHelper.createWhitelistDialog(this, list, {
                adminViewModel.saveLoginWhitelist(it)
            }){
                adminViewModel.deleteLoginWhitelist(it)
            }
        }

    }

    private fun showDialogLocationBlacklist(){
        adminViewModel.getLocationBlacklisted { list ->
            DialogHelper.createLocationDialog(this, list, {
                adminViewModel.saveLocationBlacklisted(it)
            }){
                adminViewModel.deleteLocationBlacklisted(it)
        }
        }

    }

    private fun setupTabs() {
        pagerAdapter = PagerAdapter(supportFragmentManager)
        pager?.adapter = pagerAdapter
        pager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout?.newTab()?.setText(R.string.tab_user)?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText(R.string.tab_plans)?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText(R.string.tab_themes)?.let { tabLayout?.addTab(it) }
        tabLayout?.newTab()?.setText(R.string.tab_videos)?.let { tabLayout?.addTab(it) }
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(p0: TabLayout.Tab?) {
                pager?.currentItem = p0?.position ?: 0
            }

        })
    }
}
