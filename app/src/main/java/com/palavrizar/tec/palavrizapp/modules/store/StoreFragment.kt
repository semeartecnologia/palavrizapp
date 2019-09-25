package com.palavrizar.tec.palavrizapp.modules.store

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.SkuDetails
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.utils.adapters.ListProductsAdapter
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnProductClicked
import kotlinx.android.synthetic.main.store_fragment.*

class StoreFragment : Fragment(), OnProductClicked {


    private lateinit var adapter: ListProductsAdapter
    private lateinit var adapterPlans: ListProductsAdapter

    companion object {
        fun newInstance(isAdmin: Boolean):StoreFragment {
            val fragment = StoreFragment()
            val args = Bundle()
            args.putBoolean("isAdmin", isAdmin)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: StoreViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.store_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(StoreViewModel::class.java)
        adapter = ListProductsAdapter(this)
        adapterPlans = ListProductsAdapter(this)
        setupRecyclerProducts()
        registerObservers()

    }


    override fun onResume(){
        super.onResume()
        viewModel.executeRequest(activity?.applicationContext ?: return) {
            viewModel.fetchProductsList()
        }
    }

    override fun onProductClicked(skuDetails: SkuDetails) {
        viewModel.startBillingFlow(activity as Activity, skuDetails)
    }


    private fun registerObservers() {
        viewModel.listPlansLiveData.observe(this, Observer {
            val listPlansString = arrayListOf<String>()
            it?.forEach {plans ->
                listPlansString.add(plans.plan_id)
            }
            viewModel.loadPlansCatalog(listPlansString)
        })
        viewModel.listPlanSubsDetailsLiveData.observe(this, Observer {
            if (it != null){
                adapterPlans.plansList = it
            }
        })
        viewModel.listProductsLiveData.observe(this, Observer {
            val listProductsString = arrayListOf<String>()
            it?.forEach {prod ->
                listProductsString.add(prod.product_id)
            }
            viewModel.loadProducstCatalog(listProductsString)
        })
        viewModel.listproductDetailsLiveData.observe(this, Observer {
            if (it != null){
                adapter.plansList = it
                progress_loading_products?.visibility = View.GONE
            }
        })
    }


    private fun setupRecyclerProducts() {
        recycler_products?.layoutManager = LinearLayoutManager(context)
        recycler_products?.adapter = adapter

        recycler_plans?.layoutManager = LinearLayoutManager(context)
        recycler_plans?.adapter = adapterPlans
    }

}
