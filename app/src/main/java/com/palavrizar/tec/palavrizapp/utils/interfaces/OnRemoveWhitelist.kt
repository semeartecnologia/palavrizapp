package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.palavrizar.tec.palavrizapp.models.EmailWhitelist


interface OnRemoveWhitelist {
    fun onRemoveClicked(login: EmailWhitelist, index: Int)
}