package com.semear.tec.palavrizapp.modules.admin.plans


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.PlansBilling
import com.semear.tec.palavrizapp.utils.adapters.ListPlansAdapter
import com.semear.tec.palavrizapp.utils.commons.DialogHelper
import kotlinx.android.synthetic.main.list_plans_fragment.*

class ListPlansFragment : Fragment(){

    private lateinit var adapter: ListPlansAdapter
    private var mBillingClient: BillingClient? = null

    companion object {
        fun newInstance() = ListPlansFragment()
    }

    private lateinit var viewModel: ListPlansViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_plans_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListPlansViewModel::class.java)
        adapter = ListPlansAdapter()
        setupRecyclerPlans()
        registerObservers()
        setupView()

        viewModel.initBillingClient(activity as Activity)
        viewModel.fetchPlanList()
    }



    private fun setupView() {
        fab_add_plan?.setOnClickListener {
            showCreateThemeDialog()
        }
    }

    private fun showCreateThemeDialog(plansBilling: PlansBilling? = null) {
        DialogHelper.createAddPlanDialog(activity as Activity
                ,false,
                plansBilling,
                {
                    viewModel.savePlan(it){}
                },
                {

                },{
        })
    }


    private fun registerObservers() {
        viewModel.listPlansLiveData.observe(this, Observer {
            var listPlansString = arrayListOf<String>()
            it?.forEach {plans ->
              listPlansString.add(plans.plan_id)
            }
            viewModel.loadProducstCatalog(listPlansString)
        })

        viewModel.listPlanDetailsLiveData.observe(this, Observer {
            if (it != null) {
                adapter.plansList = it
                progress_loading_plans?.visibility = View.GONE
            }else{
                progress_loading_plans?.visibility = View.GONE
            }
        })
    }



    private fun setupRecyclerPlans() {
        recycler_plans?.layoutManager = LinearLayoutManager(context)
        recycler_plans?.adapter = adapter
    }

}