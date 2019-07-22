package com.palavrizar.tec.palavrizapp.utils.interfaces

import com.palavrizar.tec.palavrizapp.models.Themes

interface OnThemeClicked {
    fun onThemeClicked(theme: Themes)
    fun onDownloadPdfClicked(uri: String)
}