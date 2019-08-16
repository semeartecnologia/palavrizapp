package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.android.billingclient.api.SkuDetails

interface OnProductClicked {
    fun onProductClicked(skuDetails: SkuDetails)
}