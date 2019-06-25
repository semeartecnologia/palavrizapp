package com.semear.tec.palavrizapp.utils.interfaces

import com.semear.tec.palavrizapp.models.User

interface OnUserSearch {
    fun onUsersSearch(userList: ArrayList<User>)
}