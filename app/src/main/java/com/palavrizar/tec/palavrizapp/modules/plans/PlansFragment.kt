package com.palavrizar.tec.palavrizapp.modules.plans

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.billingclient.api.SkuDetails

import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.adapters.ListPlansAdapter
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnPlanClicked


class PlansFragment : Fragment(), OnPlanClicked {
    override fun onPlanClicked(skuDetails: SkuDetails) {

    }

    private var recyclerPlans: RecyclerView? = null
    private var sessionManager: SessionManager? = null
    private var btnUpdatePlan: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_plans, container, false)

        if (context != null)
            sessionManager = SessionManager(context!!)

        val mAdapter = ListPlansAdapter(this)
        btnUpdatePlan = v.findViewById(R.id.btn_update_plan)
        recyclerPlans = v.findViewById(R.id.rvPlans)
        recyclerPlans?.layoutManager = LinearLayoutManager(context)
        recyclerPlans?.adapter = mAdapter

        /*btnUpdatePlan?.setOnClickListener { view ->
            sessionManager?.setUserPlan(mAdapter.lastCheckedPos)
            if (activity != null)
                (activity as MainActivity).changeFragment(DashboardFragment(), "Dashboard")
        }*/

        return v
    }

    fun setEnableUpdateButton(isEnabled: Boolean?) {
        btnUpdatePlan?.isEnabled = isEnabled!!
    }


}
