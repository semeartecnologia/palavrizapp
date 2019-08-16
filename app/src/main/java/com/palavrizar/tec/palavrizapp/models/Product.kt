package com.palavrizar.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable


data class Product(var product_id: String = "", var numCredits: Int? = null) : Parcelable {
    var productKey: String = ""

    constructor(source: Parcel) : this(
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(product_id)
        writeValue(numCredits)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Product> = object : Parcelable.Creator<Product> {
            override fun createFromParcel(source: Parcel): Product = Product(source)
            override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
        }
    }
}