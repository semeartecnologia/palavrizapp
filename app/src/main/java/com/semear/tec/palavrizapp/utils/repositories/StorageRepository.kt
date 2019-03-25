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
import com.semear.tec.palavrizapp.utils.Commons
import com.semear.tec.palavrizapp.utils.constants.Constants
import java.io.File

class StorageRepository(val context: Context) {

    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private var videosParentRef = storageRef.child("videos/")
    private lateinit var realtimeRepository: RealtimeRepository


    fun uploadVideo(video: Video){

        var filename = video.path.split("/").lastOrNull() ?: ""

        val ref = videosParentRef.child(filename)
        realtimeRepository = RealtimeRepository(context)
        val uploadTask = ref.putFile(Uri.fromFile(File(video.path)))

        Log.d("palavrizapp-servic", "uplaod started")
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {

                task.exception?.let {
                    Log.d("palavrizapp-servic", "uplaod failed " + it.message)
                    Log.d("palavrizapp-servic", "uplaod failed " + it.localizedMessage)
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener {
                    video.path = it.path ?: ""
                    uploadThumb(video)
                    Log.d("teste", "ah la " + it.path)
                }
            }
        }
        uploadTask.addOnProgressListener {
            val progress = 100.0 * it.bytesTransferred / it.totalByteCount
            Log.d("teste", "progress: $progress")

            val intent = Intent()
            intent.action = Constants.BROADCAST_UPLOAD_PROGRESS
            intent.putExtra(Constants.BROADCAST_UPLOAD_PROGRESS, progress.toInt())
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            Log.d("teste", "broadcastado")

        }
    }

    private fun uploadThumb(video: Video) {
        val refThumb = videosParentRef.child("thumbs/").child("thumb-"+System.currentTimeMillis())

        val thumbTask = refThumb.putFile( Uri.fromFile(File(video.videoThumb)))

        thumbTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {

                task.exception?.let {
                    Log.d("teste", "uplaod THUMB failed " + it.message)
                    Log.d("teste", "uplaod THUMB failed " + it.cause)
                    Log.d("teste", "uplaod THUMB failed " + it.stackTrace)
                    throw it
                }
            }
            return@Continuation refThumb.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("teste", "uplaod task THUMB sucessssss")

                Log.d("teste", "uplaod TUMB success: " + refThumb.downloadUrl)
                val intent = Intent()
                intent.action = Constants.BROADCAST_UPLOAD_DONE
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)


                refThumb.downloadUrl.addOnSuccessListener {
                    video.videoThumb = it.path ?: ""
                    Log.d("teste", "uplaod task COMPLETO")
                    realtimeRepository.saveVideo(video)
                }

            }
        }
    }
}