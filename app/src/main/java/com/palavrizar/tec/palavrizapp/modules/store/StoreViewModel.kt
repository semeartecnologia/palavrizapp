package com.palavrizar.tec.palavrizapp.modules.store

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.android.billingclient.api.*
import com.palavrizar.tec.palavrizapp.models.PlansBilling
import com.palavrizar.tec.palavrizapp.models.Product
import com.palavrizar.tec.palavrizapp.utils.repositories.PlansRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.StoreRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository
import com.android.vending.billing.IInAppBillingService
import android.os.IBinder
import android.content.ComponentName
import android.content.ServiceConnection
import com.android.billingclient.api.ConsumeParams
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper

class StoreViewModel(application: Application) : AndroidViewModel(application), PurchasesUpdatedListener, AcknowledgePurchaseResponseListener  {



    private val storeRepository = StoreRepository(application)
    private val plansRepository = PlansRepository(application)
    private val userRepository = UserRepository(application)
    private val sessionManager = SessionManager(application)

    val listPlansLiveData = MutableLiveData<ArrayList<PlansBilling>>()
    val listPlanSubsDetailsLiveData = MutableLiveData<ArrayList<SkuDetails>>()
    val listProductsLiveData = MutableLiveData<ArrayList<Product>>()
    val listproductDetailsLiveData = MutableLiveData<ArrayList<SkuDetails>>()
    val skuPurchasedSuccess = MutableLiveData<String>()

    private var mBillingClient: BillingClient? = null

    private var skuTypePurchased: String = ""
    private var skuTitlePurchased: String = ""
    var skuPurchased: String? = null

    fun fetchProductsList(){
        storeRepository.getProducts{
            listProductsLiveData.postValue(it)
        }
        plansRepository.getPlans {
            listPlansLiveData.postValue(it)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK){
            purchases?.forEach {

                if (!it.isAcknowledged ){
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(it.purchaseToken)
                            .build()
                    skuPurchased = it.sku
                    mBillingClient?.acknowledgePurchase(acknowledgePurchaseParams, this)



                    if (this.skuTypePurchased == BillingClient.SkuType.SUBS){
                        giveUserCredits(it.sku)
                    }else if (this.skuTypePurchased == BillingClient.SkuType.INAPP){
                        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
                        mBillingClient?.consumeAsync(consumeParams) { billingResult, purchaseToken -> }
                        giveUserSoloCredits(it.sku)
                    }

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

    private fun giveUserSoloCredits(productId: String){
        storeRepository.getProductByValue(productId) {prod ->
            if (prod != null) {
                userRepository.getUserSoloCredits(sessionManager.userLogged.userId) { numCredits ->
                    storeRepository.giveUserSoloCredits((prod.numCredits?.plus(numCredits))
                            ?: return@getUserSoloCredits, sessionManager.userLogged.userId) {}
                }
            }

        }
    }

    fun loadPlansCatalog(skuList: ArrayList<String>) {
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

    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult?) {
        if (skuPurchased != null && skuTypePurchased == BillingClient.SkuType.SUBS && billingResult?.responseCode == BillingClient.BillingResponseCode.OK){
            val sku = skuPurchased
            sessionManager.userPlan = sku
            skuPurchasedSuccess.postValue(skuTitlePurchased)
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


    fun loadProducstCatalog(skuList: ArrayList<String>) {
        if (mBillingClient?.isReady == true) {
            val params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP)
                    .build()
            mBillingClient?.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode.responseCode == BillingClient.BillingResponseCode.OK ) {
                    val listProductDetails = arrayListOf<SkuDetails>()
                    skuDetailsList.forEach {
                        listProductDetails.add(it)
                    }
                    listproductDetailsLiveData.postValue(listProductDetails)
                }
            }
        }
    }

    fun startBillingFlow(activity: Activity, skuDetails: SkuDetails){
        skuTypePurchased = skuDetails.type
        skuTitlePurchased = skuDetails.title
        val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
                .build()
        mBillingClient?.launchBillingFlow(activity, billingFlowParams)
    }
}