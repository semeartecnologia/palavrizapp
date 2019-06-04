package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

class Reply(var comment: String? = "", var author: User? = null) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readParcelable<User>(User::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(comment)
        writeParcelable(author, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Reply> = object : Parcelable.Creator<Reply> {
            override fun createFromParcel(source: Parcel): Reply = Reply(source)
            override fun newArray(size: Int): Array<Reply?> = arrayOfNulls(size)
        }
    }
}