package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

class Essay(var title: String = "", var theme: String = "", var author: User? = null, var postDate: String = "", var status: StatusEssay = StatusEssay.UPLOADED, var url: String = "", var essayId: String = "", var feedback: Feedback? = null) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readParcelable<User>(User::class.java.classLoader),
            source.readString(),
            StatusEssay.values()[source.readInt()],
            source.readString(),
            source.readString(),
            source.readParcelable<Feedback>(Feedback::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(theme)
        writeParcelable(author, 0)
        writeString(postDate)
        writeInt(status.ordinal)
        writeString(url)
        writeString(essayId)
        writeParcelable(feedback, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Essay> = object : Parcelable.Creator<Essay> {
            override fun createFromParcel(source: Parcel): Essay = Essay(source)
            override fun newArray(size: Int): Array<Essay?> = arrayOfNulls(size)
        }
    }
}

