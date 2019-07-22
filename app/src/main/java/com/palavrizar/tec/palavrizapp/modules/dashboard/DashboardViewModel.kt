package com.palavrizar.tec.palavrizapp.modules.dashboard

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.android.billingclient.api.*

import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.utils.repositories.PlansRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository

class DashboardViewModel(application: Application) : AndroidViewModel(application), PurchasesUpdatedListener {
    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {

    }

    var sessionManager: SessionManager = SessionManager(getApplication())
    private val plansRepository = PlansRepository(application)
    var themesRepository: ThemesRepository = ThemesRepository(getApplication())
    private var mBillingClient: BillingClient? = null
    var purchasedPlan = MutableLiveData<Purchase?>()

    val currentUser: User?
        get() = sessionManager.userLogged

    fun setFirstTimeFalse(){
        sessionManager.isUserFirstTime = false

    }

    fun executeRequest(context: Context, run: () -> Unit) {
        if (mBillingClient?.isReady == true) {
            run()
        }else{
            startConnection(context, run)
        }
    }

    fun updateUserPlan(sku: String?){
        sessionManager.userPlan = sku
        plansRepository.editUserPlan(sku ?: "") {

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
                purchasedPlan.postValue(null)
            }else {
                purchasedPlan.postValue(purchasesResult.purchasesList[0])
            }
        }
    }


}
