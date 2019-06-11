package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

class Feedback(var user: User? = null, var urlVideo: String? = "", var text: String? = "") : Parcelable {
    constructor(source: Parcel) : this(
            source.readParcelable<User>(User::class.java.classLoader),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(user, 0)
        writeString(urlVideo)
        writeString(text)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Feedback> = object : Parcelable.Creator<Feedback> {
            override fun createFromParcel(source: Parcel): Feedback = Feedback(source)
            override fun newArray(size: Int): Array<Feedback?> = arrayOfNulls(size)
        }
    }
}