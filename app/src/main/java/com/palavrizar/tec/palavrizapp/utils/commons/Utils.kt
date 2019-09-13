package com.palavrizar.tec.palavrizapp.utils.commons

import android.text.TextUtils

object Utils {

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}