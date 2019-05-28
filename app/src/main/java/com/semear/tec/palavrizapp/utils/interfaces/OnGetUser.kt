package com.semear.tec.palavrizapp.utils.interfaces

import com.semear.tec.palavrizapp.models.User

interface OnGetUser {
    fun onSuccess(user: User)
}