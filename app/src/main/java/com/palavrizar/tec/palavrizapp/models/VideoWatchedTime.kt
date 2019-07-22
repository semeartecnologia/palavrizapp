package com.palavrizar.tec.palavrizapp.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(indices = [Index(value = ["id"], unique = true)])
class VideoWatchedTime(val videoKey: String = "", val duration: Int = 0) {

    @PrimaryKey
    var id: Long? = null
}