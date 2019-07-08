package com.semear.tec.palavrizapp.utils.interfaces

import com.android.billingclient.api.SkuDetails
import com.semear.tec.palavrizapp.models.PlanDetails

interface OnPlanClicked {
    fun onPlanClicked(skuDetails: SkuDetails)
}