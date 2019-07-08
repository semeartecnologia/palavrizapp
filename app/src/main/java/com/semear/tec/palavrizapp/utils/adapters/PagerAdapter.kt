package com.semear.tec.palavrizapp.utils.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.semear.tec.palavrizapp.modules.admin.plans.ListPlansFragment
import com.semear.tec.palavrizapp.modules.admin.themes.ListThemesFragment
import com.semear.tec.palavrizapp.modules.admin.users.ListUserFragment
import com.semear.tec.palavrizapp.modules.admin.videos.ListVideosFragment

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {


    override fun getItem(p0: Int): Fragment {
        return when(p0){
            0 -> ListUserFragment()
            1 -> ListPlansFragment()
            2 -> ListThemesFragment()
            3 -> ListVideosFragment()
            else -> ListUserFragment()
        }
    }

    override fun getCount(): Int {
        return 4
    }

}