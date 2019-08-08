package com.palavrizar.tec.palavrizapp.utils.repositories

import android.content.Context
import com.google.firebase.database.*
import com.palavrizar.tec.palavrizapp.models.*
import com.palavrizar.tec.palavrizapp.utils.constants.Constants


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

    fun editVideo(video: Video, onCompletion: () -> Unit){
        val childUpdates = HashMap<String, Any?>()
        childUpdates["/videos/${Constants.NO_PLAN}/${video.videoKey}/"] = video


        video.videoPlan?.split("/")?.forEach {
            if (it.isNotBlank()){
                video.listOfPlans?.add(it)
            }
        }

        if (video.listOfPlans?.contains(Constants.PLAN_FREE_ID) == true){
            childUpdates["/videos/${Constants.PLAN_FREE_ID}/${video.videoKey}/"] = video
        }else{
            childUpdates["/videos/${Constants.PLAN_FREE_ID}/${video.videoKey}/"] = null
        }

        getSinglePlans { billingList ->
            val listPlansId = billingList.map { it.plan_id }


            video.listOfPlans?.forEach {
                if ( listPlansId.contains(it)){
                    childUpdates["/videos/$it/${video.videoKey}/"] = video
                }else{
                    if (it != Constants.PLAN_FREE_ID) {
                        childUpdates["/videos/$it/${video.videoKey}/"] = null
                    }
                }
            }


            mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener {
                onCompletion.invoke()
            }.addOnFailureListener {
            }
        }


    }

    fun editVideoOrder(videoList: ArrayList<Video>, onCompletion: (Boolean) -> Unit){
        val childUpdates = HashMap<String, Any?>()
        videoList.forEach {video ->
            childUpdates["/videos/${Constants.NO_PLAN}/${video.videoKey}/orderVideo/"] = video.orderVideo

            video.videoPlan?.split("/")?.forEach {
                if (it.isNotBlank()){
                    video.listOfPlans?.add(it)
                }
            }

            getSinglePlans { billingList ->
                val listPlansId = billingList.map { it.plan_id }
                if (video.listOfPlans?.contains(Constants.PLAN_FREE_ID) == true){
                    childUpdates["/videos/${Constants.PLAN_FREE_ID}/${video.videoKey}/orderVideo/"] = video.orderVideo
                }
                video.listOfPlans?.forEach {
                    if (listPlansId.contains(it)){
                        childUpdates["/videos/$it/${video.videoKey}/orderVideo/"] = video.orderVideo
                    }
                }

                mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener {
                    onCompletion.invoke(true)
                }.addOnFailureListener {
                    onCompletion.invoke(false)
                }
            }

        }


    }

    fun saveVideo(video: Video){
        val reference = "videos/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }

        video.videoKey = key
        val childUpdates = HashMap<String, Any?>()
        childUpdates["/videos/${Constants.NO_PLAN}/$key/"] = video
        video.videoPlan?.split("/")?.forEach {
            if (it.isNotBlank()) {
                childUpdates["/videos/$it/$key/"] = video
            }
        }
        mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener {
        }.addOnFailureListener {
        }
    }

    fun deleteVideo(videoKey: String, onCompletion: (Boolean) -> Unit){
        val childUpdates = HashMap<String, Any?>()

        getSinglePlans { billingList ->
            childUpdates["/videos/${Constants.NO_PLAN}/$videoKey/"] = null
            childUpdates["/videos/${Constants.PLAN_FREE_ID}/$videoKey/"] = null
            billingList.forEach {
                childUpdates["/videos/${it.plan_id}/$videoKey/"] = null
            }
            mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener {
                onCompletion.invoke(true)
            }.addOnFailureListener {
            }
        }
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

    fun saveTheme(theme: Themes, onCompletion: () -> Unit){
        val reference = "themes/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }
        theme.themeId = key
        mDatabaseReference.child(reference).child("$key/").setValue(theme).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun savePlan(plansBilling: PlansBilling, onCompletion: () -> Unit){
        val reference = "plans/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }
        plansBilling.planFirebaseKey = key

        mDatabaseReference.child(reference).child("$key/").setValue(plansBilling).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun getPlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        val reference = "plans/"
        var plansList = arrayListOf<PlansBilling>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                plansList.clear()
                dataSnapshot.children.mapNotNullTo(plansList) { it.getValue<PlansBilling>(PlansBilling::class.java) }
                onCompletion(plansList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(plansList)
            }
        })
    }

    fun getPlanById(planId: String, onCompletion: (PlansBilling?) -> Unit){
        val reference = "plans/"
        val queryReference = mDatabaseReference.child(reference).child(planId)
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val plan = dataSnapshot.getValue(PlansBilling::class.java)
                onCompletion(plan)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(null)
            }
        })
    }

    fun getPlansByValue(value: String, onCompletion: (ArrayList<PlansBilling>) -> Unit){
        val reference = "plans/"
        var plansList = arrayListOf<PlansBilling>()
        val queryReference = mDatabaseReference.child(reference)
                .orderByChild("plan_id")
                .equalTo(value)
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                plansList.clear()
                dataSnapshot.children.mapNotNullTo(plansList) { it.getValue<PlansBilling>(PlansBilling::class.java) }
                onCompletion(plansList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(plansList)
            }
        })
    }

    fun getSinglePlans(onCompletion: (ArrayList<PlansBilling>) -> Unit){
        val reference = "plans/"
        var plansList = arrayListOf<PlansBilling>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                plansList.clear()
                dataSnapshot.children.mapNotNullTo(plansList) { it.getValue<PlansBilling>(PlansBilling::class.java) }
                onCompletion(plansList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(plansList)
            }
        })
    }

    fun editTheme(theme: Themes, onCompletion: () -> Unit){
        val reference = "themes/"
        mDatabaseReference.child(reference).child("${theme.themeId}/").setValue(theme).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun editUserPlan(plan: String, user: User, onCompletion: () -> Unit){
        val reference = "users/"
        mDatabaseReference.child(reference).child("${user.userId}/").child("plan/").setValue(plan).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun editUserCredits(numCredits: Int, userId: String, onCompletion: () -> Unit){
        val reference = "users/"
        val childUpdates = HashMap<String, Any?>()
        childUpdates["$userId/essayCredits/"] = numCredits
        childUpdates["$userId/creditEarnedTime/"] = System.currentTimeMillis()

        mDatabaseReference.child(reference).updateChildren(childUpdates).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun userHasCredit(userId: String, onCompletion: (Boolean) -> Unit){
        getUser(userId, {
            if (it != null) {
                if (it.essayCredits >= 1) {
                    onCompletion.invoke(true)
                }else{
                    onCompletion.invoke(false)
                }
            }
        },{})
    }

    fun removeOneCreditIfPossible(userId: String, onCompletion: () -> Unit, onFail: () -> Unit){
        val reference = "users/"

        getUser(userId, {
            if (it != null) {
                if (it.essayCredits >= 1) {
                    mDatabaseReference.child(reference).child("$userId/").child("essayCredits/").setValue(it.essayCredits - 1).addOnCompleteListener {
                        onCompletion.invoke()
                    }
                }else{
                    onFail.invoke()
                }
            }
        },{})

    }

    fun deleteTheme(themeId: String){
        val reference = "themes/"
        mDatabaseReference.child(reference).child(themeId).removeValue()
    }

    fun editPlan(planId: String, plansBilling: PlansBilling, onCompletion: () -> Unit){
        val reference = "plans/"
        mDatabaseReference.child(reference).child("$planId/").setValue(plansBilling).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun deletePlan(planId: String){
        val reference = "plans/"
        mDatabaseReference.child(reference).child(planId).removeValue()
    }

    fun deleteUser(userId: String){
        val reference = "users/"
        mDatabaseReference.child(reference).child(userId).removeValue()
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
        var reference = "essaysWaiting/${essay.essayId}/"
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
        val reference = "essaysWaiting/"

        var essayList = arrayListOf<Essay>()
        val queryReference = mDatabaseReference.child(reference)
                .orderByKey()
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
        var reference = "essaysWaiting/"
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
        var reference = "essaysWaiting/"
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
            childUpdates["/essaysWaiting/${essay.essayId}"] = null
            childUpdates["/essaysDone/${essay.feedback?.user?.userId}/${essay.essayId}"] = essay
            childUpdates["/essays/${user.userId}/${essay.essayId}/feedback"] = essay.feedback
            childUpdates["/essays/${user.userId}/${essay.essayId}/status"] = status
        }else {
            childUpdates["/essaysWaiting/${essay.essayId}/feedback"] = essay.feedback
            childUpdates["/essaysWaiting/${essay.essayId}/status"] = status
            childUpdates["/essays/${user.userId}/${essay.essayId}/feedback"] = essay.feedback
            childUpdates["/essays/${user.userId}/${essay.essayId}/status"] = status
        }
        mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener {
            onCompletion.invoke()
        }
    }

    fun getNextVideo(plan: String, actualOrder: String, onCompletion: ((Video?) -> Unit)){
        val reference = "videos/"
        var videoList = arrayListOf<Video>()
        val queryReference = mDatabaseReference.child(reference).child(plan)
                .orderByChild("orderVideo")
                .startAt(actualOrder)

        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoList.clear()
                try {
                    dataSnapshot.children.mapNotNullTo(videoList) { it.getValue<Video>(Video::class.java) }
                    if (videoList.size > 1){
                        onCompletion(videoList[1])
                    }else{
                        onCompletion(null)
                    }
                }catch(e: Exception){
                    onCompletion(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(null)
            }
        })
    }

    fun getVideosList(plan: String, onCompletion: ((ArrayList<Video>) -> Unit), videoFilter: String? = null){

        val reference = "videos/"
        var videoList = arrayListOf<Video>()

       var query= videoFilter ?: "orderVideo"

        var queryReference = mDatabaseReference.child(reference).child(plan)
                .orderByChild(query)


        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoList.clear()
                dataSnapshot.children.forEach {
                    val video = it.getValue(Video::class.java)
                    var isVideoRemoved = false
                    if (video != null) {

                        if (videoFilter == "concept" && !video.concept.isNullOrBlank()){
                            videoList.add(video)
                        }else if(videoFilter == "structure" && !video.structure.isNullOrBlank()){
                            videoList.add(video)
                        }else if(videoFilter == "themeName" && !video.themeName.isNullOrBlank()){
                            videoList.add(video)
                        }else if (videoFilter == null){
                            videoList.add(video)
                        }
                    }
                }
                var arrayFinal = arrayListOf<Video>()
                arrayFinal.addAll(videoList.sortedWith(compareBy { it.orderVideo }))
                onCompletion(arrayFinal)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(videoList)
            }
        })
    }

    fun saveStructure(structure: Structure, onCompletion: () -> Unit){
        val reference = "videoStructures/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }
        mDatabaseReference.child(reference).child("$key/").setValue(structure).addOnCompleteListener {
            onCompletion.invoke()
        }
    }


    fun getVideosStructureList(onCompletion: ((ArrayList<Structure>) -> Unit)){
        val reference = "videoStructures/"
        var structureList = arrayListOf<Structure>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                structureList.clear()
                dataSnapshot.children.mapNotNullTo(structureList) { it.getValue<Structure>(Structure::class.java) }
                onCompletion(structureList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(structureList)
            }
        })
    }

    fun saveConcept(concept: Concept, onCompletion: () -> Unit){
        val reference = "videoConcepts/"
        var key = mDatabaseReference.child(reference).push().key
        if (key == null){
            key = "-" + System.currentTimeMillis().toString()
        }
        mDatabaseReference.child(reference).child("$key/").setValue(concept).addOnCompleteListener {
            onCompletion.invoke()
        }
    }


    fun getVideosConceptList(onCompletion: ((ArrayList<Concept>) -> Unit)){
        val reference = "videoConcepts/"
        var conceptList = arrayListOf<Concept>()
        val queryReference = mDatabaseReference.child(reference)
        queryReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                conceptList.clear()
                dataSnapshot.children.mapNotNullTo(conceptList) { it.getValue<Concept>(Concept::class.java) }
                onCompletion(conceptList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                onCompletion(conceptList)
            }
        })
    }
}