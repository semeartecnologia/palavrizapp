package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import com.google.firebase.database.*
import com.semear.tec.palavrizapp.models.*


class RealtimeRepository(val context: Context) {

    private val mDatabaseReference: DatabaseReference  = FirebaseDatabase.getInstance().reference

    fun setUserType(userId: String, userType: UserType, onCompletion: () -> Unit){
        val reference = "users/"
        mDatabaseReference.child(reference).child("$userId/")
                .child("userType").setValue(userType).addOnCompleteListener {
                    onCompletion()
                }.addOnFailureListener {
                }
    }

    fun getUserList(lastVisible: String? = null, onCompletion: (ArrayList<User>) -> Unit){
        val reference = "users/"

        val userList = arrayListOf<User>()
        val queryReference = mDatabaseReference.child(reference)
                .orderByChild("fullname")
                .limitToFirst(15)
        if(lastVisible != null){
            queryReference.startAt(lastVisible)
        }
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                dataSnapshot.children.mapNotNullTo(userList) { it.getValue<User>(User::class.java) }
                onCompletion(userList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(userList)
            }
        })
    }

    fun saveVideo(video: Video){
        val reference = "videos/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }

        video.videoKey = key

        mDatabaseReference.child(reference).child(video.category+"/").child("$key/").setValue(video)
    }

    fun saveEssay(essay: Essay, userId: String){
        val reference = "essays/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }
        essay.essayId = key
        mDatabaseReference.child(reference).child("$userId/").child("$key/").setValue(essay)
        saveEssayWaitingForFeedback(essay)
    }

    fun setEssayUnreadableStatus(essay: Essay, userId: String, onCompletion: () -> Unit){
        val childUpdates = HashMap<String, Any?>()

        essay.status = StatusEssay.NOT_READABLE

        childUpdates["/essaysWaiting/${essay.themeId}/${essay.essayId}"] = null
        childUpdates["/essaysDone/${essay.themeId}/${essay.feedback?.user?.userId}/${essay.essayId}"] = essay
        childUpdates["/essays/$userId/${essay.essayId}/status"] = essay.status
        mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener {
            onCompletion.invoke()
        }.addOnFailureListener {
        }
    }

    fun getEssayDoneList(themeId: String, author: User, onCompletion: (ArrayList<Essay>) -> Unit){
        val reference = "essaysDone/"
        var essayList = arrayListOf<Essay>()
        val queryReference = mDatabaseReference.child(reference).child("$themeId/")
                .child("${author.userId}/")
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
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

    fun saveComment(comment: Comment, videoKey: String, onCompletion: () -> Unit){
        val reference = "comments/"
        comment.time = System.currentTimeMillis()
        var commentKey = mDatabaseReference.child(reference).push().key
        comment.id = commentKey
        mDatabaseReference.child(reference).child("$videoKey/$commentKey/").setValue(comment).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun saveReply(reply: Reply, commentId: String, videoKey: String, onCompletion: () -> Unit){
        val reference = "reply/$commentId/"
        var commentKey = mDatabaseReference.child(reference).push().key
        mDatabaseReference.child(reference).child("$commentKey/").setValue(reply).addOnCompleteListener {
            onCompletion.invoke()
        }
    }


    fun loadReply(commentId: String,  onCompletion: (ArrayList<Reply>) -> Unit ){
        val reference = "reply/$commentId/"
        var replyList = arrayListOf<Reply>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                replyList.clear()
                dataSnapshot.children.mapNotNullTo(replyList) { it.getValue<Reply>(Reply::class.java) }
                onCompletion(replyList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(replyList)
            }
        })
    }

    fun loadComments(videoKey: String, onCompletion: (ArrayList<Comment>) -> Unit){
        val reference = "comments/"
        var commentList = arrayListOf<Comment>()
        val queryReference = mDatabaseReference.child(reference).child("$videoKey/")
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList.clear()


                dataSnapshot.children.mapNotNullTo(commentList) { it.getValue<Comment>(Comment::class.java) }
                onCompletion(commentList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(commentList)
            }
        })
    }

    fun saveTheme(theme: Themes){
        val reference = "themes/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }
        mDatabaseReference.child(reference).child("$key/").setValue(theme)
    }

    fun deleteTheme(themeId: String){
        val reference = "themes/"
        mDatabaseReference.child(reference).child(themeId).removeValue()
    }

    fun getThemes(onCompletion: (ArrayList<Themes>) -> Unit){
        val reference = "themes/"
        var themeList = arrayListOf<Themes>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                themeList.clear()
                dataSnapshot.children.mapNotNullTo(themeList) { it.getValue<Themes>(Themes::class.java) }
                onCompletion(themeList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(themeList)
            }
        })
    }

    private fun saveEssayWaitingForFeedback(essay: Essay){
        var reference = "essaysWaiting/${essay.theme}/${essay.essayId}/"
        mDatabaseReference.child(reference).setValue(essay)
    }

    fun getEssayListByUser(userId: String, onCompletion: ((ArrayList<Essay>) -> Unit)){
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

    fun getEssayList(onCompletion: ((ArrayList<Essay>) -> Unit)){
        val reference = "essaysWaiting/Tema Teste/"

        var essayList = arrayListOf<Essay>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                essayList.clear()
                try {
                    dataSnapshot.children.mapNotNullTo(essayList) { it.getValue<Essay>(Essay::class.java) }
                    onCompletion(essayList)
                }catch(e: Exception){
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(arrayListOf())
            }
        })
    }

    fun getUser(userId: String, onCompletion: (User?) -> Unit, onFail: () -> Unit){
        val reference = "users/"
        val queryReference = mDatabaseReference.child(reference).child("$userId/")
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                onCompletion(user)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFail.invoke()
            }
        })
    }

    fun getEssayWaitingById(essayId: String,  onCompletion: (Essay?) -> Unit, onFail: () -> Unit){
        var reference = "essaysWaiting/Tema Teste/"
        val queryReference = mDatabaseReference.child(reference).child(essayId)
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                onFail.invoke()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val essay = dataSnapshot.getValue(Essay::class.java)
                onCompletion(essay)
            }

        })
    }

    fun getEssayWaitingListenerChange(essayId: String, onChange: (Essay?) -> Unit, onFail: () -> Unit){
        var reference = "essaysWaiting/Tema Teste/"
        val queryReference = mDatabaseReference.child(reference).child(essayId)
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                onFail.invoke()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val essay = dataSnapshot.getValue(Essay::class.java)
                onChange(essay)
            }

        })
    }

    fun setFeedbackOwnerOnEssay(essay: Essay, user: User, status: StatusEssay, onCompletion: () -> Unit){
        val childUpdates = HashMap<String, Any?>()
        if (status == StatusEssay.FEEDBACK_READY){
            essay.status = status
            childUpdates["/essaysWaiting/${essay.themeId}/${essay.essayId}"] = null
            childUpdates["/essaysDone/${essay.themeId}/${essay.feedback?.user?.userId}/${essay.essayId}"] = essay
            childUpdates["/essays/${user.userId}/${essay.essayId}/feedback"] = essay.feedback
            childUpdates["/essays/${user.userId}/${essay.essayId}/status"] = status
        }else {
            childUpdates["/essaysWaiting/${essay.themeId}/${essay.essayId}/feedback"] = essay.feedback
            childUpdates["/essaysWaiting/${essay.themeId}/${essay.essayId}/status"] = status
            childUpdates["/essays/${user.userId}/${essay.essayId}/feedback"] = essay.feedback
            childUpdates["/essays/${user.userId}/${essay.essayId}/status"] = status
        }
        mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun getVideosList(plans: Plans, category: String, onCompletion: ((ArrayList<Video>) -> Unit)){

        //TODO FILTER BY PLAN
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