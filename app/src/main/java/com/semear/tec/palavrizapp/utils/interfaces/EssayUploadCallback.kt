package com.semear.tec.palavrizapp.utils.interfaces

interface EssayUploadCallback {
    fun onSuccess()
    fun onFail()
    fun onProgress(progress: Int)
}