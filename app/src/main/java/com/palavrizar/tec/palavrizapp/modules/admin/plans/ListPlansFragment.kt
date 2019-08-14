package com.palavrizar.tec.palavrizapp.modules.admin.plans


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.PlansBilling
import com.palavrizar.tec.palavrizapp.utils.adapters.ListPlansAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnPlanClicked
import kotlinx.android.synthetic.main.list_plans_fragment.*
import java.lang.Exception


class ListPlansFragment : Fragment(), OnPlanClicked {


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
            fab_add_plan?.show()
            tv_user_see_plans?.visibility = View.GONE
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

    override fun onResume() {
        super.onResume()

        if (!isAdmin) {
            viewModel.executeRequest(activity?.applicationContext ?: return) {
                queryPurchases()
            }
        }else{
            viewModel.executeRequest(activity?.applicationContext ?: return) {
                viewModel.fetchPlanList()
            }

        }
    }

    fun queryPurchases(){
        viewModel.queryPurchases()
    }

    override fun onPlanClicked(skuDetails: SkuDetails) {
        if (!isAdmin){
            viewModel.startBillingFlow(activity as Activity, skuDetails)
        }else{
            viewModel.getPlan(skuDetails.sku){
                var a = it
                DialogHelper.createAddPlanDialog(activity as Activity
                        ,true,
                        a[0],
                        {pb ->
                            viewModel.editPlan(a[0].planFirebaseKey, pb)
                        },
                        {

                        },{
                    viewModel.deletePlan(a[0].planFirebaseKey)
                })
            }
        }
    }

    private fun registerObservers() {
        viewModel.listPlansLiveData.observe(this, Observer {
            val listPlansString = arrayListOf<String>()
            it?.forEach {plans ->
                listPlansString.add(plans.plan_id)
            }
            viewModel.loadProducstCatalog(listPlansString)
        })
        viewModel.listPlanSubsDetailsLiveData.observe(this, Observer {
            //if (it != null){
                progress_loading_plans?.visibility = View.GONE
                adapter.plansList = it!!
            //}
        })
        viewModel.planSubDetailsLiveData.observe(this, Observer {
            setupUserHasPlan(it)
        })
        viewModel.getUserCreditsLiveData.observe(this, Observer {
            if (it != null) {
                setupUserCredits(it)
            }
        })

    }

    private fun setupUserHasPlan(planDetails: SkuDetails?){
        tv_user_see_plans?.visibility = View.GONE
        frame_layout_recycle?.visibility = View.GONE
        layout_has_plan?.visibility = View.VISIBLE
        tv_user_plan_title?.text = planDetails?.title ?: ""
        tv_user_plan_desc?.text = planDetails?.description ?: ""

        btn_update_plan?.setOnClickListener {
            manageAccount()
        }
    }

    private fun setupUserCredits(numCredits: Int){
        try {
            tv_user_plan_credits?.text = numCredits.toString()
        }catch(e: Exception){

        }
    }

    fun manageAccount(){
        val browserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/account/subscriptions"))
        Log.d("AQUI", Uri.parse("https://play.google.com/store/account/subscriptions").toString())
        startActivity(browserIntent)
    }

    private fun setupRecyclerPlans() {
        recycler_plans?.layoutManager = LinearLayoutManager(context)
        recycler_plans?.adapter = adapter
    }

}