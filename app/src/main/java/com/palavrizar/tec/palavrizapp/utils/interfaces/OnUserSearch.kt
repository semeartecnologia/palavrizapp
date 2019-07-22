package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.palavrizar.tec.palavrizapp.models.User

interface OnUserSearch {
    fun onUsersSearch(userList: ArrayList<User>)
}