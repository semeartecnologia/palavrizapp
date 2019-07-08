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
import com.android.billingclient.api.SkuDetails
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.models.PlanDetails
import com.semear.tec.palavrizapp.models.PlansBilling
import com.semear.tec.palavrizapp.utils.adapters.ListPlansAdapter
import com.semear.tec.palavrizapp.utils.commons.DialogHelper
import com.semear.tec.palavrizapp.utils.interfaces.OnPlanClicked
import kotlinx.android.synthetic.main.list_plans_fragment.*

class ListPlansFragment : Fragment(), OnPlanClicked{


    private lateinit var adapter: ListPlansAdapter


    companion object {
        fun newInstance(isAdmin: Boolean):ListPlansFragment {
            val fragment = ListPlansFragment()
            val args = Bundle()
            args.putBoolean("isAdmin", isAdmin)
            fragment.arguments = args
            return fragment
        }
    }

    private var isAdmin = false
    private lateinit var viewModel: ListPlansViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_plans_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListPlansViewModel::class.java)
        adapter = ListPlansAdapter(this)
        setupRecyclerPlans()
        registerObservers()
        setupView()

        getExtras()
        viewModel.initBillingClient(activity as Activity)
        viewModel.fetchPlanList()
    }

    private fun getExtras() {
        isAdmin = arguments?.getBoolean("isAdmin") ?: false
        setupAdmin()
    }


    private fun setupView() {
        fab_add_plan?.setOnClickListener {
            showCreateThemeDialog()
        }
    }

    private fun setupAdmin() {
        if(isAdmin){
            fab_add_plan?.visibility = View.VISIBLE
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

    override fun onPlanClicked(skuDetails: SkuDetails) {
        viewModel.startBillingFlow(activity as Activity, skuDetails)
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