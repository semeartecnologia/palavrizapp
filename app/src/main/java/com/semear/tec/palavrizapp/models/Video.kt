package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable

class Video(var orderVideo: Int = 0, var videoPlan: String? = "", var videoKey: String = "", var title: String = "", var description: String = "", var category: String = "", var path: String = "", var videoThumb: String? = null) : Parcelable {
    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(orderVideo)
        writeString(videoPlan)
        writeString(videoKey)
        writeString(title)
        writeString(description)
        writeString(category)
        writeString(path)
        writeString(videoThumb)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Video> = object : Parcelable.Creator<Video> {
            override fun createFromParcel(source: Parcel): Video = Video(source)
            override fun newArray(size: Int): Array<Video?> = arrayOfNulls(size)
        }
    }
}
