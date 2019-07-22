package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.palavrizar.tec.palavrizapp.models.User

interface OnGetUser {
    fun onSuccess(user: User)
}