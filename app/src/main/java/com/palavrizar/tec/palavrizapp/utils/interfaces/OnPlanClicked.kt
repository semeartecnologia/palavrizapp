package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.android.billingclient.api.SkuDetails

interface OnPlanClicked {
    fun onPlanClicked(skuDetails: SkuDetails)
}