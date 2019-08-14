package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.palavrizar.tec.palavrizapp.models.LocationBlacklist

interface OnRemoveLocationClicked {
    fun onRemoveClicked(location: LocationBlacklist, index: Int)
}