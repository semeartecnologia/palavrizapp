package com.semear.tec.palavrizapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Video(var orderVideo: String = "0", var videoPlan: String? = "", var videoKey: String = "", var title: String = "", var description: String = "", var category: String = "", var path: String = "", var videoThumb: String? = null, var pdfPath: String? = null, var themeName: String = "", var concept: String = "", var structure: String = "") : Parcelable {
    @get:Exclude
    var listOfPlans: ArrayList<Plans>? = arrayListOf()

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
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
        writeString(orderVideo)
        writeString(videoPlan)
        writeString(videoKey)
        writeString(title)
        writeString(description)
        writeString(category)
        writeString(path)
        writeString(videoThumb)
        writeString(pdfPath)
        writeString(themeName)
        writeString(concept)
        writeString(structure)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Video> = object : Parcelable.Creator<Video> {
            override fun createFromParcel(source: Parcel): Video = Video(source)
            override fun newArray(size: Int): Array<Video?> = arrayOfNulls(size)
        }
    }
}
