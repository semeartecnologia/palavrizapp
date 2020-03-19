package com.palavrizar.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Essay(var title: String = "", var theme: String = "", var themeId: String = "", var author: User? = null, var postDate: String = "", var status: StatusEssay = StatusEssay.UPLOADED, var url: String = "", var essayId: String = "", var feedback: Feedback? = null, var filename: String = "") : Parcelable {
    @get:Exclude
    var isReadMode: Boolean? = false
    @get:Exclude
    var isReviewMode: Boolean? = false

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readParcelable<User>(User::class.java.classLoader),
            source.readString(),
            StatusEssay.values()[source.readInt()],
            source.readString(),
            source.readString(),
            source.readParcelable<Feedback>(Feedback::class.java.classLoader),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(theme)
        writeString(themeId)
        writeParcelable(author, 0)
        writeString(postDate)
        writeInt(status.ordinal)
        writeString(url)
        writeString(essayId)
        writeParcelable(feedback, 0)
        writeString(filename)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Essay> = object : Parcelable.Creator<Essay> {
            override fun createFromParcel(source: Parcel): Essay = Essay(source)
            override fun newArray(size: Int): Array<Essay?> = arrayOfNulls(size)
        }
    }
}

