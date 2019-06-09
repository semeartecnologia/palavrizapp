package com.semear.tec.palavrizapp.utils.repositories

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.semear.tec.palavrizapp.models.Essay
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.Commons
import com.semear.tec.palavrizapp.utils.constants.Constants
import java.io.File
import android.R.attr.bitmap
import com.semear.tec.palavrizapp.models.Themes
import com.semear.tec.palavrizapp.utils.interfaces.EssayUploadCallback
import java.io.ByteArrayOutputStream


class StorageRepository(val context: Context) {

    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private var videosParentRef = storageRef.child("videos/")
    private var essaysParentRef = storageRef.child("essays/")
    private var realtimeRepository = RealtimeRepository(context)


    fun uploadVideo(video: Video){

        var filename = video.path.split("/").lastOrNull() ?: ""

        val ref = videosParentRef.child(filename)
        val uploadTask = ref.putFile(Uri.fromFile(File(video.path)))

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {

                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener {
                    video.path = it.path ?: ""
                    uploadThumb(video)
                }
            }
        }
        uploadTask.addOnProgressListener {
            val progress = 100.0 * it.bytesTransferred / it.totalByteCount

            val intent = Intent()
            intent.action = Constants.BROADCAST_UPLOAD_PROGRESS
            intent.putExtra(Constants.BROADCAST_UPLOAD_PROGRESS, progress.toInt())
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        }
    }

    fun uploadVideoFeedback(urlVideo: String, onCompletion: (String?) -> Unit){

        var filename = urlVideo.split("/").lastOrNull() ?: ""

        val ref = videosParentRef.child("feedbacks/").child(filename)
        val uploadTask = ref.putFile(Uri.fromFile(File(urlVideo)))

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                onCompletion("")
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener {
                    onCompletion(it.path)
                }
            }else{
                onCompletion("")
            }
        }
        uploadTask.addOnProgressListener {
            val progress = 100.0 * it.bytesTransferred / it.totalByteCount

            val intent = Intent()
            intent.action = Constants.BROADCAST_UPLOAD_PROGRESS
            intent.putExtra(Constants.BROADCAST_UPLOAD_PROGRESS, progress.toInt())
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        }
    }

    fun uploadPdf(theme: Themes, onCompletion: (Themes?) -> Unit){

        val filename = theme.urlPdf?.split("/")?.lastOrNull() ?: ""

        val ref = essaysParentRef.child("themes/").child(filename)
        val uploadTask = ref.putFile(Uri.fromFile(File(theme.urlPdf)))

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                onCompletion(null)
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener {
                    theme.urlPdf = it.path ?: ""
                    Log.d("teste", "testao = ${theme.urlPdf}")
                    onCompletion(theme)
                }
            }else{
                onCompletion(null)
            }
        }
        uploadTask.addOnProgressListener {
            val progress = 100.0 * it.bytesTransferred / it.totalByteCount

            val intent = Intent()
            intent.action = Constants.BROADCAST_UPLOAD_PROGRESS
            intent.putExtra(Constants.BROADCAST_UPLOAD_PROGRESS, progress.toInt())
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        }
    }

    fun getPdf(filename: String, onCompletion: ((String) -> Unit)){
        val refThumb = essaysParentRef.child("themes/").child(filename)
        refThumb.downloadUrl.addOnSuccessListener {
            onCompletion(it.toString())
        }.addOnFailureListener {
            onCompletion("")
        }

    }

    private fun uploadThumb(video: Video) {
        val refThumb = videosParentRef.child("thumbs/").child("thumb-"+System.currentTimeMillis())

        val thumbTask = refThumb.putFile( Uri.fromFile(File(video.videoThumb)))

        thumbTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {

                task.exception?.let {
                    throw it
                }
            }
            return@Continuation refThumb.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent()
                intent.action = Constants.BROADCAST_UPLOAD_DONE
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)


                refThumb.downloadUrl.addOnSuccessListener {
                    video.videoThumb = it.path ?: ""
                    realtimeRepository.saveVideo(video)
                }

            }
        }
    }

    fun getEssay(filename: String, onCompletion: ((String) -> Unit)){
        val refThumb = essaysParentRef.child(filename)
        refThumb.downloadUrl.addOnSuccessListener {
            onCompletion(it.toString())
        }.addOnFailureListener {
            onCompletion("")
        }

    }

    fun uploadEssay(essay: Essay, userId: String, bmp: Bitmap, callback: EssayUploadCallback) {
        val filename = "essay-"+System.currentTimeMillis()
        val refThumb = essaysParentRef.child(filename)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val thumbTask = refThumb.putBytes(data)
        thumbTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    callback.onFail()
                    throw it
                }
            }
            return@Continuation refThumb.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                essay.url = filename
                realtimeRepository.saveEssay(essay, userId)
                callback.onSuccess()
            }
        }
    }
}