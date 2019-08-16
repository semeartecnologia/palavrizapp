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


class StoreViewModel(application: Application) : AndroidViewModel(application), PurchasesUpdatedListener, AcknowledgePurchaseResponseListener  {



    private val storeRepository = StoreRepository(application)
    private val userRepository = UserRepository(application)
    private val sessionManager = SessionManager(application)

    val listProductsLiveData = MutableLiveData<ArrayList<Product>>()
    val listproductDetailsLiveData = MutableLiveData<ArrayList<SkuDetails>>()

    private var mBillingClient: BillingClient? = null

    var skuPurchased: String? = null

    fun saveProduct(product: Product, onCompletion: () -> Unit){
        storeRepository.saveProduct(product){

        }
    }

    fun fetchProductsList(){
        storeRepository.getProducts{
            listProductsLiveData.postValue(it)
        }
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

    private fun giveUserCredits(productId: String){
        storeRepository.getProductByValue(productId) {
            userRepository.giveUserCredits(sessionManager.userLogged.userId, it?.numCredits ?: return@getProductByValue)

        }
    }

    override fun onAcknowledgePurchaseResponse(billingResult: BillingResult?) {
        if (skuPurchased != null){

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
        val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
                .build()
        mBillingClient?.launchBillingFlow(activity, billingFlowParams)
    }
}