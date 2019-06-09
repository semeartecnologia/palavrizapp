package com.semear.tec.palavrizapp.utils.interfaces

import com.semear.tec.palavrizapp.models.Themes

interface OnThemeClicked {
    fun onThemeClicked(theme: Themes)
    fun onDownloadPdfClicked(uri: String)
}