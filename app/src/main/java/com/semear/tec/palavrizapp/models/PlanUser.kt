package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

data class PlanUser(var plan_id: String = "", var expirationDate: Long) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(plan_id)
        writeLong(expirationDate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PlanUser> = object : Parcelable.Creator<PlanUser> {
            override fun createFromParcel(source: Parcel): PlanUser = PlanUser(source)
            override fun newArray(size: Int): Array<PlanUser?> = arrayOfNulls(size)
        }
    }
}