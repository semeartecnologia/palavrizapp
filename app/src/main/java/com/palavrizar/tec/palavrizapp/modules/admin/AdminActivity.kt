package com.palavrizar.tec.palavrizapp.modules.admin

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.modules.base.BaseActivity
import com.palavrizar.tec.palavrizapp.utils.adapters.PagerAdapter
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : BaseActivity() {

    private var pagerAdapter = PagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.toolbar_admin)
        setContentView(R.layout.activity_admin)

        setupTabs()
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
