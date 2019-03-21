package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.semear.tec.palavrizapp.models.Video

class RealtimeRepository(val context: Context) {

    val mDatabaseReference: DatabaseReference  = FirebaseDatabase.getInstance().reference
    var reference = "videos/"

    fun saveVideo(video: Video){
        mDatabaseReference.child(reference).child(video.category+"/").setValue(video)
    }
}