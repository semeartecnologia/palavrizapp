package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.constants.Constants
import java.io.File

class StorageRepository(val context: Context) {

    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    var videosParentRef = storageRef.child("videos/")
    private lateinit var realtimeRepository: RealtimeRepository


    fun uploadVideo(video: Video){

        var filename = video.path.split("/").lastOrNull() ?: ""

        val ref = videosParentRef.child(filename)
        realtimeRepository = RealtimeRepository(context)
        val uploadTask = ref.putFile(Uri.fromFile(File(video.path)))

        Log.d("palavrizapp-servic", "uplaod started")
        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {

                task.exception?.let {
                    Log.d("palavrizapp-servic", "uplaod failed " + it.message)
                    Log.d("palavrizapp-servic", "uplaod failed " + it.localizedMessage)
                    throw it
                }
            }
            Log.d("palavrizapp-servic", "uplaod success: " + ref.downloadUrl)
            val intent = Intent()
            intent.action = Constants.BROADCAST_UPLOAD_DONE
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            realtimeRepository.saveVideo(video)
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("palavrizapp-servic", "uplaod task sucessssss")
                val downloadUri = task.result
            } else {
            }
        }
    }
}