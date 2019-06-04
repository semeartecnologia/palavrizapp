package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

class Comment(var id: String? = "", var comment: String? = "", var author: User? = null, var reply: List<Reply>? = null) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readParcelable<User>(User::class.java.classLoader),
            source.createTypedArrayList(Reply.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(comment)
        writeParcelable(author, 0)
        writeTypedList(reply)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Comment> = object : Parcelable.Creator<Comment> {
            override fun createFromParcel(source: Parcel): Comment = Comment(source)
            override fun newArray(size: Int): Array<Comment?> = arrayOfNulls(size)
        }
    }
}

