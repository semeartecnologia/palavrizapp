package com.semear.tec.palavrizapp.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

import com.google.firebase.database.IgnoreExtraProperties

import android.arch.persistence.room.ForeignKey.CASCADE
import com.semear.tec.palavrizapp.utils.constants.Constants.TABLE_THEMES


class Themes {

    var themeId: String = ""
    var themeName: String = ""
    var urlPdf: String? = null

    constructor() {}

    constructor(themeName: String, urlPdf: String) {
        this.themeName = themeName
        this.urlPdf = urlPdf
    }
}
