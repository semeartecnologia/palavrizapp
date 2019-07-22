package com.palavrizar.tec.palavrizapp.utils.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.palavrizar.tec.palavrizapp.models.VideoWatchedTime

@Dao
interface VideoWatchedTimeDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertVideoWatched(videoWatchedTime: VideoWatchedTime): Long

    @Query("SELECT * FROM VideoWatchedTime WHERE videoKey=:videoKey")
    fun getVideoWatchedTimeById(videoKey: String): VideoWatchedTime

    @Query("SELECT * FROM VideoWatchedTime")
    fun getAllVideoWatchedTime(videoKey: String): VideoWatchedTime
}