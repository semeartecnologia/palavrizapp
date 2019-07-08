package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

data class PlansBilling(var plan_id: String = "", var limitEssay: Int? = null, var period: EnumPeriod? = null) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader)?.let { EnumPeriod.values()[it as Int] }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(plan_id)
        writeValue(limitEssay)
        writeValue(period?.ordinal)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PlansBilling> = object : Parcelable.Creator<PlansBilling> {
            override fun createFromParcel(source: Parcel): PlansBilling = PlansBilling(source)
            override fun newArray(size: Int): Array<PlansBilling?> = arrayOfNulls(size)
        }
    }
}