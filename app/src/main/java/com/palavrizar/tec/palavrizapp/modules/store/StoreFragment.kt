package com.palavrizar.tec.palavrizapp.modules.store

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat.getExtras
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.SkuDetails
import com.palavrizar.tec.palavrizapp.R
import com.palavrizar.tec.palavrizapp.utils.adapters.ListPlansAdapter
import com.palavrizar.tec.palavrizapp.utils.interfaces.OnPlanClicked
import kotlinx.android.synthetic.main.store_fragment.*

class StoreFragment : Fragment(), OnPlanClicked {


    private lateinit var adapter: ListPlansAdapter


    companion object {
        fun newInstance(isAdmin: Boolean):StoreFragment {
            val fragment = StoreFragment()
            val args = Bundle()
            args.putBoolean("isAdmin", isAdmin)
            fragment.arguments = args
            return fragment
        }
    }

    private var isAdmin = false

    private lateinit var viewModel: StoreViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.store_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(StoreViewModel::class.java)
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

    private fun setupAdmin() {
        /*if(isAdmin){
        }*/
    }



    override fun onPlanClicked(skuDetails: SkuDetails) {

    }

    private fun setupRecyclerPlans() {
        recycler_products?.layoutManager = LinearLayoutManager(context)
        recycler_products?.adapter = adapter
    }

    private fun registerObservers() {

    }

    private fun setupView(){

    }


}
