package com.palavrizar.tec.palavrizapp.modules.admin.plans


import android.app.Activity
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.models.PlansBilling
import com.palavrizar.tec.palavrizapp.models.Product
import com.palavrizar.tec.palavrizapp.utils.adapters.ListPlansAdapter
import com.palavrizar.tec.palavrizapp.utils.adapters.ListProductsAdapter
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnPlanClicked
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnProductClicked
import kotlinx.android.synthetic.main.list_plans_fragment.*
import java.lang.Exception


class ListPlansFragment : Fragment(), OnPlanClicked, OnProductClicked {



    private lateinit var adapter: ListPlansAdapter
    private lateinit var adapterProducts: ListProductsAdapter


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
        adapterProducts = ListProductsAdapter(this)
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
            showPlanOrProductPickerDialog()
        }
        setupSpinner()
    }

    private fun setupAdmin() {
        if(isAdmin){
            fab_add_plan?.show()
            tv_user_see_plans?.visibility = View.GONE
        }else{
            spinner_plans_products?.visibility = View.GONE
        }
    }

    private fun setupSpinner(){
        val listOfTypes = arrayListOf<String>()
        listOfTypes.add("Planos")
        listOfTypes.add("Produtos")

        val adapter = ArrayAdapter(activity as Activity,
                android.R.layout.simple_spinner_item, listOfTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_plans_products?.adapter = adapter


        spinner_plans_products?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0){
                    frame_layout_recycle_plans.visibility = View.VISIBLE
                    frame_layout_recycle_products.visibility = View.GONE
                }else{
                    frame_layout_recycle_products.visibility = View.VISIBLE
                    frame_layout_recycle_plans.visibility = View.GONE
                }
            }

        }
    }

    private fun showPlanOrProductPickerDialog(){
        DialogHelper.createPlanOrProductDialog(activity as Activity, {
            //products
            showAddProductDialog()
        }){
            //plans
            showAddPlanDialog()
        }
    }

    private fun showAddProductDialog(product: Product? = null){
        DialogHelper.createAddProductDialog(activity as Activity,
                false,
                product,
                {
                    //create callback
                    viewModel.saveProduct(it)
                },
                {
                    //delete callback
                }
        )
    }


    private fun showAddPlanDialog(plansBilling: PlansBilling? = null) {
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
                viewModel.fetchProductList()
            }

        }
    }

    fun queryPurchases(){
        viewModel.queryPurchases()
    }

    override fun onProductClicked(skuDetails: SkuDetails) {

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
        viewModel.listProductsLiveData.observe(this, Observer {
            val listProductsString = arrayListOf<String>()
            it?.forEach {prods ->
                listProductsString.add(prods.product_id)
            }
            viewModel.loadProducstInAppsCatalog(listProductsString)
        })
        viewModel.listProductsSkuDetailsLiveData.observe(this, Observer {
            if (it != null){
                adapterProducts.plansList = it
            }
        })
        viewModel.listPlanSubsDetailsLiveData.observe(this, Observer {
            if (it != null){
                progress_loading_plans?.visibility = View.GONE
                adapter.plansList = it
            }
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
        frame_layout_recycle_plans?.visibility = View.GONE
        frame_layout_recycle_products?.visibility = View.GONE
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
        startActivity(browserIntent)
    }

    private fun setupRecyclerPlans() {
        recycler_plans?.layoutManager = LinearLayoutManager(context)
        recycler_plans?.adapter = adapter
    }

}