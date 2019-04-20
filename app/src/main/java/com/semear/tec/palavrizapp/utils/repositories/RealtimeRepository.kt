package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.Video
import com.google.android.youtube.player.internal.w
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener



class RealtimeRepository(val context: Context) {

    val mDatabaseReference: DatabaseReference  = FirebaseDatabase.getInstance().reference

    fun saveVideo(video: Video){
        var reference = "videos/"
        var key = mDatabaseReference.child("videos/").push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }

        mDatabaseReference.child(reference).child(video.category+"/").child("$key/").setValue(video)
    }

    fun saveEssay(essay: Essay, userId: String){
        var reference = "essays/"
        var key = mDatabaseReference.child("essays/").push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }
        mDatabaseReference.child(reference).child("$userId/").child("$key/").setValue(essay)
    }

    fun getEssayList(userId: String){
        val reference = "essays/"
        val queryReference = mDatabaseReference.child(reference).child("$userId/")
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    var a = ""
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                var b = ""
            }
        })
    }
}