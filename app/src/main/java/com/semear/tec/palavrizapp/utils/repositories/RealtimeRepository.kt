package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.google.firebase.database.*
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.models.VideoCategory


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

    fun getEssayList(userId: String, onCompletion: ((ArrayList<Essay>) -> Unit)){
        val reference = "essays/"
        var essayList = arrayListOf<Essay>()
        val queryReference = mDatabaseReference.child(reference).child("$userId/")
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                essayList.clear()
                dataSnapshot.children.mapNotNullTo(essayList) { it.getValue<Essay>(Essay::class.java) }
                onCompletion(essayList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(essayList)
            }
        })
    }

    fun getVideosList(category: String, onCompletion: ((ArrayList<Video>) -> Unit)){
        val reference = "videos/"
        var videoList = arrayListOf<Video>()
        val queryReference = mDatabaseReference.child(reference).child("$category/")
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoList.clear()
                dataSnapshot.children.mapNotNullTo(videoList) { it.getValue<Video>(Video::class.java) }
                onCompletion(videoList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(videoList)
            }
        })
    }

    fun getVideosCategoryList(onCompletion: ((ArrayList<VideoCategory>) -> Unit)){
        val reference = "videoCategory/"
        var categoryList = arrayListOf<VideoCategory>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoryList.clear()
                dataSnapshot.children.mapNotNullTo(categoryList) { it.getValue<VideoCategory>(VideoCategory::class.java) }
                onCompletion(categoryList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(categoryList)
            }
        })
    }
}