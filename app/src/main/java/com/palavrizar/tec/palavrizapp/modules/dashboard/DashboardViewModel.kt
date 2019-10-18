package com.palavrizar.tec.palavrizapp.modules.dashboard

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.android.billingclient.api.*
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.palavrizar.tec.palavrizapp.BuildConfig
import com.palavrizar.tec.palavrizapp.models.EnumPeriod

import com.palavrizar.tec.palavrizapp.models.User
import com.palavrizar.tec.palavrizapp.utils.commons.DateFormatHelper
import com.palavrizar.tec.palavrizapp.utils.commons.DialogHelper
import com.palavrizar.tec.palavrizapp.utils.repositories.PlansRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.SessionManager
import com.palavrizar.tec.palavrizapp.utils.repositories.ThemesRepository
import com.palavrizar.tec.palavrizapp.utils.repositories.UserRepository

@Suppress("IMPLICIT_CAST_TO_ANY")
class DashboardViewModel(application: Application) : AndroidViewModel(application), PurchasesUpdatedListener {
    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {

    }

    var sessionManager: SessionManager = SessionManager(getApplication())
    private val plansRepository = PlansRepository(application)
    private val userRepository = UserRepository(application)
    var themesRepository: ThemesRepository = ThemesRepository(getApplication())
    private var mBillingClient: BillingClient? = null
    var purchasedPlan = MutableLiveData<Purchase?>()

    var isUserOnline = MutableLiveData<Boolean>()
    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var loginManager : LoginManager = LoginManager.getInstance()
    var currentUserLivedata = MutableLiveData<User>()

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

    fun getUserFirebase(){
        userRepository.getUser(sessionManager.userLogged.userId, {
            currentUserLivedata.postValue(it)
        }, {})
    }

    fun logout(){
        sessionManager.setUserOffline()
        mAuth.signOut()
        loginManager.logOut()
        isUserOnline.postValue(false)
    }


    fun updateUserPlan(sku: String?, isRenewed: Boolean = false){
        sessionManager.userPlan = sku
        plansRepository.editUserPlan(sku ?: "") {

        }

        plansRepository.getPlansByValue(sku ?: return){
            if (it.size > 0){
                val plan = it[0]

                if (sessionManager.userLogged != null) {
                    userRepository.getUser(sessionManager.userLogged.userId,{user ->

                        var timeWait = when {
                            plan.period == EnumPeriod.QUINZENAL -> (DateFormatHelper.MILLISECONDS_IN_DAY * 15) + (DateFormatHelper.MILLISECONDS_IN_HOUR * 4)
                            plan.period == EnumPeriod.SEMANAL -> (DateFormatHelper.MILLISECONDS_IN_DAY * 7) + (DateFormatHelper.MILLISECONDS_IN_HOUR * 4)
                            plan.period == EnumPeriod.MENSAL -> (DateFormatHelper.MILLISECONDS_IN_DAY * 30) + (DateFormatHelper.MILLISECONDS_IN_HOUR * 4)
                            else -> (DateFormatHelper.MILLISECONDS_IN_DAY * 15) + (DateFormatHelper.MILLISECONDS_IN_HOUR * 4)
                        }

                        if (BuildConfig.DEBUG){
                            timeWait = 60 * 1000 * 3 // 3 mins no debug
                        }

                        if (user != null && (user.creditEarnedTime + timeWait <= System.currentTimeMillis()) && isRenewed){
                            userRepository.giveUserCredits(user.userId, plan.limitEssay ?: return@getUser)
                        }
                    }) {
                        //onFail
                    }

                }
            }
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
