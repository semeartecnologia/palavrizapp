package com.palavrizar.tec.palavrizapp.modules.admin.plans

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.android.billingclient.api.*
import com.palavrizar.tec.palavrizapp.models.PlansBilling
import com.palavrizar.tec.palavrizapp.utils.repositories.PlansRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository


class ListPlansViewModel(application: Application) : AndroidViewModel(application), PurchasesUpdatedListener, AcknowledgePurchaseResponseListener  {



    private val plansRepository = PlansRepository(application)
    private val userRepository = UserRepository(application)
    private val sessionManager = SessionManager(application)

    val listPlansLiveData = MutableLiveData<ArrayList<PlansBilling>>()
    val listPlanSubsDetailsLiveData = MutableLiveData<ArrayList<SkuDetails>>()
    val planSubDetailsLiveData = MutableLiveData<SkuDetails>()
    val getUserCreditsLiveData = MutableLiveData<Int>()

    private var mBillingClient: BillingClient? = null

    var skuPurchased: String? = null

    fun savePlan(plansBilling: PlansBilling,  onCompletion: () -> Unit){
        plansRepository.savePlan(plansBilling, onCompletion)
    }


    fun fetchPlanList(){
        plansRepository.getPlans {
            listPlansLiveData.postValue(it)
        }
    }

    fun deletePlan(planId: String){
        plansRepository.deletePlan(planId)
    }

    fun getPlan(value: String, onCompletion: (ArrayList<PlansBilling>) -> Unit){
        plansRepository.getPlansByValue(value, onCompletion)
    }

    fun editPlan(planId: String, plansBilling: PlansBilling){
        plansRepository.editPlan(planId, plansBilling){}
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {


        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK){
            purchases?.forEach {


                if (!it.isAcknowledged){
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(it.purchaseToken)
                            .build()

                    skuPurchased = it.sku
                    mBillingClient?.acknowledgePurchase(acknowledgePurchaseParams, this)
                    giveUserCredits(it.sku)
                }

            }
        }else{
        }
    }

    fun giveUserCredits(planId: String){
        plansRepository.getPlansByValue(planId) {
            userRepository.giveUserCredits(sessionManager.userLogged.userId, it[0].limitEssay ?: return@getPlansByValue)

        }

    }

    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult?) {
        if (skuPurchased != null){
            val sku = skuPurchased
            sessionManager.userPlan = sku
            plansRepository.editUserPlan(sku ?: "") {

            }
        }
    }

    fun executeRequest(context: Context, run: () -> Unit) {
        if (mBillingClient?.isReady == true) {
            run()
        }else{
            startConnection(context, run)
        }
    }

    fun startConnection(context: Context, run: () -> Unit) {
        mBillingClient = BillingClient
                .newBuilder(context)
                .enablePendingPurchases()
                .setListener(this)
                .build()

        mBillingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    run()
                }
            }

            override fun onBillingServiceDisconnected() {
            }
        })
    }



    fun queryPurchases() {
        val purchasesResult = mBillingClient?.queryPurchases(BillingClient.SkuType.SUBS)
        if (mBillingClient?.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)?.responseCode ==  BillingClient.BillingResponseCode.OK) {
            if (purchasesResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                purchasesResult.purchasesList?.addAll(
                        purchasesResult.purchasesList)
            }
        }

        if (purchasesResult != null && purchasesResult.purchasesList != null) {
            if (purchasesResult.purchasesList.isEmpty()){
                fetchPlanList()
            }else {
                if (purchasesResult.purchasesList.size > 0) {
                    getSingleProductCatalog(purchasesResult.purchasesList[0].sku)
                    getUserCredits()
                }
            }
        }else{
            fetchPlanList()
        }
    }

    private fun getUserCredits(){
        userRepository.getUserCredits(sessionManager.userLogged.userId){
            getUserCreditsLiveData.postValue(it)
        }
    }

    private fun getSingleProductCatalog(sku: String) {
        val skuList = arrayListOf<String>()
        skuList.add(sku)
        if (mBillingClient?.isReady == true) {
            val params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.SUBS)
                    .build()
            mBillingClient?.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode.responseCode == BillingClient.BillingResponseCode.OK ) {
                    planSubDetailsLiveData.postValue(skuDetailsList[0])
                }
            }
        }
    }

    fun loadProducstCatalog(skuList: ArrayList<String>) {
        if (mBillingClient?.isReady == true) {
            val params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.SUBS)
                    .build()
            mBillingClient?.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode.responseCode == BillingClient.BillingResponseCode.OK ) {
                    //println("querySkuDetailsAsync, responseCode: $responseCode")
                    val listPlanDetails = arrayListOf<SkuDetails>()
                    skuDetailsList.forEach {
                        /*var planDetail = PlanDetails("", it.title, it.description, it.price)*/
                        listPlanDetails.add(it)
                    }
                    listPlanSubsDetailsLiveData.postValue(listPlanDetails)
                    //initProductAdapter(skuDetailsList)
                } else {
              //      println("Can't querySkuDetailsAsync, responseCode: $responseCode")
                }
            }
        } else {
            //println("Billing Client not ready")
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