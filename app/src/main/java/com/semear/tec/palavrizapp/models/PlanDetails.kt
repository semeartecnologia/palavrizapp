package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

class PlanDetails(var plan_id: String = "",var title: String = "",var description: String = "",var price: String = "") : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(plan_id)
        writeString(title)
        writeString(description)
        writeString(price)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PlanDetails> = object : Parcelable.Creator<PlanDetails> {
            override fun createFromParcel(source: Parcel): PlanDetails = PlanDetails(source)
            override fun newArray(size: Int): Array<PlanDetails?> = arrayOfNulls(size)
        }
    }
}