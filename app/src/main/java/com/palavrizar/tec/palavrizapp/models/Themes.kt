package com.palavrizar.tec.palavrizapp.models


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
