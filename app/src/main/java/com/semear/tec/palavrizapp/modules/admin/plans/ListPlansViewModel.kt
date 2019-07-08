package com.semear.tec.palavrizapp.modules.admin.plans

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.semear.tec.palavrizapp.models.PlanDetails
import com.semear.tec.palavrizapp.models.PlansBilling
import com.semear.tec.palavrizapp.utils.repositories.PlansRepository
import com.semear.tec.palavrizapp.utils.repositories.ThemesRepository

class ListPlansViewModel(application: Application) : AndroidViewModel(application), PurchasesUpdatedListener {
    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {

    }


    private val plansRepository = PlansRepository(application)
    val listPlansLiveData = MutableLiveData<ArrayList<PlansBilling>>()
    val listPlanDetailsLiveData = MutableLiveData<ArrayList<SkuDetails>>()
    private var mBillingClient: BillingClient? = null

    fun savePlan(plansBilling: PlansBilling,  onCompletion: () -> Unit){
        plansRepository.savePlan(plansBilling, onCompletion)
    }

    fun fetchPlanList(){
        plansRepository.getPlans {
            listPlansLiveData.postValue(it)
        }
    }

    fun initBillingClient(context: Context) {
        mBillingClient = BillingClient
                .newBuilder(context)
                .enablePendingPurchases()
                .setListener(this)
                .build()

        mBillingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("billao","BILLING | startConnection | RESULT OK")
                } else {
                    Log.d("billao", "BILLING | startConnection | RESULT: $billingResult?.responseCode")
                }
            }

            override fun onBillingServiceDisconnected() {
                println("BILLING | onBillingServiceDisconnected | DISCONNECTED")
            }
        })
    }


    fun loadProducstCatalog(skuList: ArrayList<String>) {
        var a = skuList
        if (mBillingClient?.isReady == true) {
            val params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP)
                    .build()
            mBillingClient?.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode.responseCode == BillingClient.BillingResponseCode.OK ) {
                    println("querySkuDetailsAsync, responseCode: $responseCode")
                    val listPlanDetails = arrayListOf<SkuDetails>()
                    skuDetailsList.forEach {
                        /*var planDetail = PlanDetails("", it.title, it.description, it.price)*/
                        listPlanDetails.add(it)
                    }
                    listPlanDetailsLiveData.postValue(listPlanDetails)
                    //initProductAdapter(skuDetailsList)
                } else {
                    println("Can't querySkuDetailsAsync, responseCode: $responseCode")
                }
            }
        } else {
            println("Billing Client not ready")
        }
    }

    fun startBillingFlow(activity: Activity, skuDetails: SkuDetails){
        val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
                .build()
        mBillingClient?.launchBillingFlow(activity, billingFlowParams)
    }
}