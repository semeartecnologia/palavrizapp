package com.semear.tec.palavrizapp.modules.upload

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.semear.tec.palavrizapp.models.Video
import com.semear.tec.palavrizapp.utils.constants.Constants.EXTRA_VIDEO
import com.semear.tec.palavrizapp.utils.repositories.RealtimeRepository
import com.semear.tec.palavrizapp.utils.repositories.StorageRepository

class UploadService : Service() {

    private lateinit var storageRepository: StorageRepository
    private lateinit var realtimeRepository: RealtimeRepository

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onCreate() {
        Log.d("palavrizapp-service", "created")
        initRepositories()
        showNotification()
    }

    fun initRepositories(){
        storageRepository = StorageRepository(applicationContext)
        realtimeRepository = RealtimeRepository(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("palavrizapp-service", "uploaded service binded")
        val video = intent?.getParcelableExtra<Video>(EXTRA_VIDEO)
        if (video != null) {
            storageRepository.uploadVideo(video)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification(){
        Log.d("palavrizapp-service", "showing")
        //val notificationIntent = Intent(applicationContext, UploadActivity::class.java)
        //val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        //val notification = NotificationCompat.Builder(this, "default")
                //.setContentTitle(getText(R.string.notification_title))
               // .setContentText(getText(R.string.notification_content))
                //.setSmallIcon(R.drawable.ic_contact)
                //.setContentIntent(pendingIntent)
              //  .setOngoing(true)
            //    .setTicker(getText(R.string.notification_ticker))
          //      .build()
        //startForeground(NOTIFICATION_ID, notification)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("palavrizapp-service", "uploaded service unbinded")
        return super.onUnbind(intent)
    }


}